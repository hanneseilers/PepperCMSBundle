package de.fhkiel.pepper.cms_core.apps;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import de.fhkiel.pepper.cms_lib.repository.PepperCMSRemote;
import de.fhkiel.pepper.cms_lib.repository.interfaces.PepperCMSRepositoryLoadedCallable;
import de.fhkiel.pepper.cms_lib.repository.PepperCMSRepository;
import de.fhkiel.pepper.cms_lib.apps.PepperApp;
import de.fhkiel.pepper.cms_lib.apps.PepperAppInterface;
import de.fhkiel.pepper.cms_lib.apps.PepperCMSControllerInterface;
import de.fhkiel.pepper.cms_lib.users.User;

public class PepperCMSController implements PepperCMSControllerInterface {
    private static final String TAG = PepperCMSController.class.getName();
    private static final int REQUEST_CODE = 53;
    private static final int REQUEST_FILE_CODE = 24149;

    private static final String localConfig = "config/";
    private static final String localAppsFile = "apps.local";
    private static final String localRepositoriesFile = "repositories.config";

    private final Activity activity;
    private static boolean useOnlineSaved = false;
    private String storagePath = "games/db/";
    private HashMap<Integer, PepperApp> apps = new HashMap<>();
    private HashMap<Integer, PepperApp> updatableApps = new HashMap<>();
    private HashMap<Integer, PepperApp> installableApps = new HashMap<>();

    private FileDescriptor appDataFile = null;
    private HashMap<String, Intent>  pendingIntentResults = new HashMap<>();
    private ArrayList<PepperCMSRepository> repositories = new ArrayList<>();

    private User autheticatedUser;

    public PepperCMSController(Activity activity) { this.activity = activity; }

    public HashMap<Integer, PepperApp> getApps() {
        return apps;
    }

    public HashMap<Integer, PepperApp> getUpdatableApps() {
        return updatableApps;
    }

    public HashMap<Integer, PepperApp> getInstallableApps() {
        return installableApps;
    }

    @Override
    public boolean startCMS(boolean useOnline, boolean isRestart){

        if(isRunningOnUiThread()){
            Log.e(TAG, "Start cms only aside from UI tread!");
            return false;
        }

        // TODO: remove after testing
        if(!isRestart) {
            clearRepositories();
        }

        // save online config
        useOnlineSaved = useOnline;

        // check internet state
        if(useOnline && this.activity != null){
            ConnectivityManager connectivityManager = (ConnectivityManager) this.activity.getApplicationContext().getSystemService( Context.CONNECTIVITY_SERVICE );
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            // check if connection is available, if not only load remove flag
            if(!networkInfo.isConnected() || !networkInfo.isAvailable()){
                useOnline = false;
                Log.w(TAG, "Internet connection is not connected or available. Enable it to load online remote data.");
            }
        }

        // load apps
        getPepperApps(useOnline);

        // TODO: Further things

        return true;
    }

    @Override
    public boolean retstartCMS() {
        return startCMS(useOnlineSaved, true);
    }

    @Override
    public boolean startPepperApp(PepperApp app, User user) {
        Log.d(TAG, "trying to start:" + app.getName() + " - " + app.getIntentPackage()+ "/" + app.getIntentClass());

        // process pending intent results
        processPendingIntentResults();

        // create intent
        Intent intent = new Intent();
        intent.setClassName(app.getIntentPackage(), app.getIntentClass());

        intent.putExtra("app", app.toJSONObject().toString() );
        if(user != null) {
            Log.d(TAG, "\t> user '" + user.getUsername() + "' found");
            intent.putExtra("user", user.toJSONObject().toString());
            String gameData = loadAppData(app.getHashCode(), user);
            if( gameData != null ){
                Log.d(TAG, "\t> game data found. length " + gameData.length());
                intent.putExtra("data", gameData );
            }
        }

        try {
            Log.d(TAG, "\t> starting intent: " + intent);
            activity.startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e){
            Log.e(TAG, "\t> Execption on intent: " + e.getMessage());
            return false;
        }

        // notify about app start
        notifyOnAppStart(app);

        return true;
    }

    @Override
    public void loadLocalPepperApps() {
        Log.d(TAG, "Loading Pepper apps from local rescource");

        // check if on ui thread!
        if( isRunningOnUiThread() ){
            return;
        }

        // get offline rescources
        File config = getFile(localConfig, localAppsFile);
        String data = loadFileData(config);

        // add Apps from string
        addPepperApps(data);
    }

    @Override
    public void loadRemotePepperApps() {
        Log.d(TAG, "\t> Loading Pepper apps from online rescources");

        loadRepositories(repository -> {
            repository.testURL(isValid -> {
                if( isValid ) {
                    Log.d(TAG, "Repository " + repository.getRepositoryURL() + " is valid.");
                    
                    try {

                        ArrayList<PepperApp> repositoryApps = repository.getPepperApps();
                        Log.d(TAG, "found " + +repositoryApps.size() + " apps.");

                        // TODO: Checking with loaded apps

                    } catch (MalformedURLException e){
                        Log.e(TAG, "Malformed repository url: " + e);
                    }

                } else {
                    Log.e(TAG, "Repository \" + reprository.getRepositoryURL() + \" is NOT valid!");
                }
            });
        });
    }

    /**
     * @return true if on UI thread, false otherwise.
     */
    private boolean isRunningOnUiThread(){
        // check if on ui thread!
        if( Thread.currentThread() == Looper.getMainLooper().getThread() ){
            Log.e(TAG, "Running online app rescource update on ui thread! This is not allowed!");
            return true;
        }
        return false;
    }

    /**
     * Function to load available {@link PepperApp}
     * Notifies all listeners, if apps loaded.
     *
     * @param load If set to true, apps are also loaded from online rescource
     */
    @Override
    public void getPepperApps(boolean load) {
        Log.d(TAG, "getting Pepper apps.");
        this.apps.clear();
        this.updatableApps.clear();

        new Thread(() -> {

            // get local apps
            //loadLocalPepperApps();

            // get online recources
            Log.d(TAG, "\t> use oline sources: " + load);
            if (load){
                loadRemotePepperApps();
            }

            // save results
            //savePepperApps();

            // process pending events
            //processPendingIntentResults();

            // notify listener
            for (PepperAppInterface listener : PepperCMSControllerInterface.pepperAppInterfaceListener) {
                Log.d(TAG, "\t> calling callbacks");
                listener.onPepperAppsLoaded(this.apps, false);
            }

        }).start();
    }

    /**
     * Adds {@link PepperApp}s to list from {@link String} data.
     * @param data  JSON formatted {@link String} data.
     */
    private void addPepperApps(String data){
        try {

            if( data != null ) {
                JSONArray jsonPepperApps = new JSONArray(data);
                ArrayList<PepperApp> appsArray = jsonToPepperApps(jsonPepperApps);

                // save apps
                for (PepperApp app : appsArray) {
                    Log.d(TAG, "\t> adding new app: " + app);
                    addPepperApp(app);
                }
            }

        } catch (JSONException e){
            e.printStackTrace();
            Log.d(TAG, "\t> Cannot read app data string:\n" + data);
        }
    }

    /**
     * Adds a {@link PepperApp} to {@link HashMap}.
     * @param app   {@link PepperApp} to add
     */
    private void addPepperApp(PepperApp app){
        int hash = app.getIdentifier().hashCode();

        if( this.apps.containsKey(hash) ){
            // check for newer version
            PepperApp localApp = this.apps.get(hash);
            if( localApp.getCurrentVersion() < app.getLatestVersion() ){
                app.setCurrentVersion( localApp.getCurrentVersion() );
                this.updatableApps.put(hash, app);
            }
        }

        // add app to list
        this.installableApps.put(hash, app);
    }

    /**
     * Saves {@link HashMap} of {@link PepperApp}s to file.
     */
    private void savePepperApps(){

        // create json array of loaded apps
        Log.d(TAG, "\t > Saving pepper apps.");
        JSONArray jsonArray = new JSONArray();
        for( int hash : this.apps.keySet() ){
            PepperApp app = this.apps.get(hash);
            JSONObject jsonObject = app.toJSONObject();
            jsonArray.put(jsonObject);
        }

        // save data to file
        File config = getFile(localConfig, localAppsFile);
        saveFileData(config, jsonArray.toString());
    }

    /**
     * Converts {@link JSONArray} with {@link JSONObject}s of {@link PepperApp}s to list.
     * @param jsonArray {@link JSONArray} to parse
     * @return          {@link ArrayList}, empty list if json is not valid. Invalid app data is skipped, until other json is correct.
     */
    private ArrayList<PepperApp> jsonToPepperApps(JSONArray jsonArray){
        ArrayList<PepperApp> apps = new ArrayList<>();

        try {
            if(jsonArray!=null) {
                for (int i = 0; i < jsonArray.length(); i++) {

                    // create object from array content and parse into PepperApp object
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    PepperApp app = PepperApp.fromJson(jsonObject);

                    if( app != null ) {
                        apps.add(app);
                    }

                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        return apps;
    }

    /**
     * Gets list of {@link PepperApp}s, ready for update.
     *
     * @return {@link HashMap} of {@link PepperApp}s.
     */
    @Override
    public HashMap<Integer, PepperApp> getUpdateablePepperApps() {
        return this.updatableApps;
    }

    private void notifyOnAppStart(PepperApp app){
        for(PepperAppInterface listener : PepperCMSControllerInterface.pepperAppInterfaceListener){
            listener.onAppStarted(app);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "received intent result data, request: " + requestCode + " , result: " + (resultCode == Activity.RESULT_OK ? "ok" : "not ok") );
        if( requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK
                && data != null && data.hasExtra("app") ){

            try {
                JSONObject appData = new JSONObject(data.getStringExtra("app"));
                if(appData.has("hashcode")) {
                    pendingIntentResults.put(appData.getString("hashcode"), data);
                    processPendingIntentResults();
                }
            } catch (JSONException e){
                e.printStackTrace();
            }

        } else if(requestCode == REQUEST_FILE_CODE && resultCode == Activity.RESULT_OK){
            try {
                Uri uri = data.getData();
                this.appDataFile = activity.getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor();
            } catch (FileNotFoundException e){
                Log.e(TAG, "file not found:\n" + e);
            }
        }
    }

    @Override
    public void setStoragePath(String path) {
        storagePath = path;
    }

    /**
     * Gets the currently authenticated user
     * @return {@link User}, null if no one authenticated
     */
    @Override
    public User getAuthenticatedUser() {
        return this.autheticatedUser;
    }

    /**
     * Gets a default user object. Can be used to get a valid {@link User} object, if no user is autheticated.
     * @return {@link User} object of default user.
     */
    @Override
    public User getDefaultUser() {
        return new User();
    }

    /**
     * Sets the current autheticated {@link User}. Use null, if no one is authenticated or user authentification got lost.
     * @param user  {@link User} to set as authenticated user
     */
    @Override
    public void setAuthenticatedUser(User user) {
        this.autheticatedUser = user;
    }

    private void processPendingIntentResults(){
        Log.d(TAG, "processing " + pendingIntentResults.size() + " peding intent results");

        for( String hashcode : pendingIntentResults.keySet() ){
            Intent intent = pendingIntentResults.get(hashcode);
            if( intent.hasExtra("data") ){

                // get user from intent
                User user = null;
                if(intent.hasExtra("user")){
                    try {
                        user = User.fromJSONObject(new JSONObject(intent.getStringExtra("user")));
                    } catch (JSONException e){
                        Log.e(TAG, "cannot read user data from intent result:\n" + intent.getStringExtra("user"));
                    }
                }

                // save app data to file
                saveAppData(hashcode, intent.getStringExtra("data"), user);

            }
        }

    }

    /**
     * Saves app data to external files dir. And if not available or not writeable, to internal files dir.
     * Exsisting data is overwritten.
     * @param hashcode  {@link String} hash code of app
     * @param data      {@link String} data of app.
     * @param user      {@link User} data belongs to. If null, data is saved as user 'none'.
     */
    private void saveAppData(String hashcode, String data, User user){
        Log.d(TAG, "save data for app " + hashcode);
        Log.d(TAG, "user: " + (user != null ? user.toJSONObject().toString() : "none"));
        Log.d(TAG, "data:\n" + data);

        File appFile = getGameFile(hashcode, user);
        saveFileData(appFile, data);
    }

    private boolean saveFileData(File file, String data){
        if( data != null ) {
            try {

                Log.d(TAG, "\t\t> save to: " + file.getPath());
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(data.getBytes());
                outputStream.close();
                return true;

            } catch (IOException e) {
                e.printStackTrace();
                Log.w(TAG, "\t\t> Cannot write data to file: " + file.getName());
            }
        } else {
            Log.w(TAG, "\t\t> no data!");
        }
        return false;
    }

    /**
     * Loads the {@link User}s {@link PepperApp} data from stored file.
     * @param hashcode  Hashcode of {@link PepperApp}
     * @param user      {@link User} to load data for
     * @return          {@link String} of app data, null if no data found.
     */
    private String loadAppData(String hashcode, User user){
        Log.d(TAG, "load data for app " + hashcode);
        Log.d(TAG, "user: " + user.toJSONObject().toString());

        File appFile = getGameFile(hashcode, user);
        return loadFileData(appFile);
    }

    /**
     * Loads {@link String} data from file.
     * @param file  {@link File} to load data from
     * @return      {@link String} content of file, null if not found
     */
    private String loadFileData(File file){
        if(file != null){
            try {

                Log.d(TAG, "\t\t> loading from: " + file.getPath());
                FileInputStream fileInputStream = new FileInputStream(file);
                DataInputStream inputStream = new DataInputStream(fileInputStream);
                BufferedReader reader = new BufferedReader(new InputStreamReader((inputStream)));
                String line;
                StringBuilder data = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    data.append(line);
                }
                inputStream.close();
                return data.toString();

            } catch (IOException e){
                Log.w(TAG, "\t> Cannot read data from file: " + file.getName());
            }
        }

        Log.w(TAG, "\t> No data file found.");
        return null;
    }

    /**
     * Gets {@link PepperApp} data file for one {@link User}.
     * @param hashcode  Hashcode of {@link PepperApp}
     * @param user      {@link User} to get the data file for
     * @return          Data {@link File}
     */
    private File getGameFile(String hashcode, User user){
        if(activity != null){
            String appFileName = hashcode + ".json";
            String storageFilePath = storagePath + (user != null ? user.getUserFilePathName() : "none");
            return getFile(storageFilePath, appFileName);
        }
        return null;
    }

    /**
     * Gets a {@link File} from storage.
     * If possible from external storage.
     * @param path      {@link String} of relative path. (No leading slash!)
     * @param filename  {@link String} of filename
     * @return          {@link File} or null, if not found.
     */
    private File getFile(String path, String filename){
        File appFile = null;
        if(activity != null){
            appFile = new File(activity.getFilesDir() + "/" + path, filename);
            if (isExternalStorageWriteable()) {
                appFile = new File(activity.getExternalFilesDir(path), filename);
                Log.d(TAG, "\t> Using file from external storage.");
            }
        } else {
            Log.e(TAG, "\t> No activity set. Cannot load file!");
        }
        return appFile;
    }

    /**
     * Checks wheter or not extranal device is available and writeable
     * @return  True if external storage is writeable, false otherwise.
     */
    private static boolean isExternalStorageWriteable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

    /**
     * Saves {@link PepperCMSRepository}s data to file.
     * return   true if successfull, false otherwise.
     */
    private boolean saveRepositories(){
        Log.d(TAG, "\t> saving repository data for " + repositories.size() + " repositories.");
        File repositoryFile = getFile(localConfig, localRepositoriesFile);
        JSONArray jsonArray = new JSONArray();
        for( PepperCMSRepository repository : repositories ){
            jsonArray.put( repository.toJSONObject() );
        }
        return saveFileData(repositoryFile, jsonArray.toString());
    }

    @Override
    public boolean clearRepositories() {
        Log.w(TAG, "Clearing all repository data!");
        this.repositories.clear();
        return saveRepositories();
    }

    /**
     * Loads {@link PepperCMSRepository}s from file.
     * @return  {@link ArrayList} of {@link PepperCMSRepository}s
     */
    private void loadRepositories(PepperCMSRepositoryLoadedCallable callback){
        Log.d(TAG, "\t> loading repository data");
        repositories.clear();       // clear old repository list
        Log.d(TAG, "\t> old repository data cleared");
        File repositoryFile = getFile(localConfig, localRepositoriesFile);

        try {

            String data = loadFileData(repositoryFile);
            if( data != null ){

                JSONArray jsonArray = new JSONArray(data);
                if( jsonArray != null ) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Log.d(TAG, "\t\t> adding new repository...");
                        PepperCMSRepository repository = PepperCMSRepository.fromJsonObject( jsonArray.getJSONObject(i) );
                        addRepository( repository );
                        callback.repositoryLoaded(repository);
                    }
                } else {
                    Log.w(TAG, "\t\t> No repository file content found!");
                }

            } else {
                Log.w(TAG, "\t\t> No repository file found.");
            }

        } catch (JSONException e){
            Log.w(TAG, "\t\t> Cannot read json data for repositories: " + e);
        }
    }

    @Override
    public ArrayList<PepperCMSRepository> getRepositories() {
        return repositories;
    }

    @Override
    public ArrayList<PepperCMSRepository> addRepository(PepperCMSRepository repo){
        if( !this.repositories.contains(repo) ){
            this.repositories.add(repo);
            saveRepositories();
        }
        return this.repositories;
    }

    @Override
    public ArrayList<PepperCMSRepository> removeRepository(PepperCMSRepository repo){
        if( this.repositories.contains(repo) ){
            this.repositories.remove(repo);
        }
        return this.repositories;
    }
}
