package de.fhkiel.pepper.cms_lib.repository;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import de.fhkiel.pepper.cms_lib.JSONObjectable;
import de.fhkiel.pepper.cms_lib.apps.PepperApp;
import de.fhkiel.pepper.cms_lib.repository.interfaces.PepperCMSRepositoryIsAliveCallable;

/**
 * Class to build a repository remote site.
 * Works only with valid https connections.
 */
public class PepperCMSRepository implements JSONObjectable {
    private URL githubRepositoryURL;
    private URL simpleRepositoryURL;
    private boolean isValidChecked = false;
    private boolean isValid = false;
    private boolean hasUpdates = false;

    private String title;
    private String description;
    private long lastUpdated = 0;
    private ArrayList<PepperApp> apps = new ArrayList<>();

    private final static String TAG = PepperCMSRepository.class.getName();
    //private final static String URL_ENCODING = "UTF-8";
    private final static int URL_DEFAULT_TIMEOUT_MSEC = 15 * 1000;

    private static final String REPO_SIMPLE_JSON_REPO_FILE = "repo.json";
    //private static final String REPO_SIMPLE_TAG_TYPE = "type";
    private static final String REPO_SIMPLE_TAG_TITLE = "title";
    private static final String REPO_SIMPLE_TAG_DESC = "description";
    private static final String REPO_SIMPLE_TAG_LAST_UPDATE = "lastUpdate";
    private static final String REPO_SIMPLE_TAG_APPS_ARRAY = "apps";

    /**
     * Gets the repositories {@link URL}.
     * @return  {@link URL}
     */
    public URL getRepositoryURL(){
        if(isGitHubRepository()) return githubRepositoryURL;
        if(isSimpleRepository()) return simpleRepositoryURL;
        return null;
    }

    private boolean isGitHubRepository(){
        return githubRepositoryURL != null;
    }

    private boolean isSimpleRepository(){
        return simpleRepositoryURL != null;
    }

    public boolean isRepositoryChecked() {
        return isValidChecked;
    }

    public boolean isRepositoryValid(){
        if(!isRepositoryChecked()){
            Log.w(TAG, "\t> valid not checked until now");
        }
        return isValid;
    }

    public boolean hasUpdates() {
        return hasUpdates;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public ArrayList<PepperApp> getApps() {
        return apps;
    }

    /**
     * Creates a simple file system based repository.
     * @param url       {@link URL} of remote site
     * @return
     * @throws MalformedURLException
     */
    public static PepperCMSRepository createSimpleRepository(String url) throws MalformedURLException {
        Log.d(TAG, "Creating simple repository for: " + url);
        PepperCMSRepository repo =  new PepperCMSRepository();
        repo.simpleRepositoryURL = createURL(url);
        return repo;
    }

    /**
     * Creates a github releases based repository.
     * @param url       {@link URL} of github remote
     * @return
     * @throws MalformedURLException
     */
    public static PepperCMSRepository createGithubRepository(String url) throws MalformedURLException {
        Log.d(TAG, "creating github repository");
        PepperCMSRepository repo =  new PepperCMSRepository();
        repo.githubRepositoryURL = createURL(url);
        return repo;
    }

    /**
     * Creates a {@link URL} object out of a {@link String}
     * @param url   {@link String} of url
     * @return      {@link URL} object
     * @throws MalformedURLException
     */
    private static URL createURL(String url) throws MalformedURLException {
        /*try {
            url = URLEncoder.encode(url, URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new MalformedURLException("Cannot encode URL due to not supported encoding: " + URL_ENCODING);
        }*/

        return new URL(url);
    }

    /**
     * Tests if repository {@link URL} is valid.
     */
    public void testURL(PepperCMSRepositoryIsAliveCallable callback){
        Log.d(TAG, "\t> testing URL is in progress.");

        new Thread(() -> {
            isValid = isURLAlive(getRepositoryURL());
            isValidChecked = true;
            callback.onRepositoryTested(isValid);
        }).start();

    }

    /**
     * Test an {@link URL}, if it is correct and working.
     * @param url   {@link URL} to test
     * @return      true if {@link URL} is valid, otherwise false
     */
    private synchronized boolean isURLAlive(URL url){

        Log.d(TAG, "\t> testing URL: " + url);
        boolean ret = false;

        try{

            if( !isRepositoryChecked() ) {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();

                // set default timeout to connection and reading and connect
                httpsURLConnection.setConnectTimeout(URL_DEFAULT_TIMEOUT_MSEC);
                httpsURLConnection.setReadTimeout(URL_DEFAULT_TIMEOUT_MSEC);
                httpsURLConnection.connect();

                // try to read from url
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(httpsURLConnection.getInputStream())
                );

                String line = null;
                while ((line = bufferedReader.readLine()) != null);    // reading the url to nowhere

                bufferedReader.close();

                ret = true;
            } else {
                ret = isValid;
            }

        } catch (IOException e){
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }

        return ret;

    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (isGitHubRepository())
                jsonObject.put("githubRepositoryURL", getRepositoryURL().toString());
            if (isSimpleRepository())
                jsonObject.put("simpleRepositoryURL", getRepositoryURL());

            jsonObject.put(REPO_SIMPLE_TAG_TITLE, this.title);
            jsonObject.put(REPO_SIMPLE_TAG_DESC, this.description);
            jsonObject.put(REPO_SIMPLE_TAG_LAST_UPDATE, this.lastUpdated);
        } catch (JSONException e){
            Log.e(TAG, "\t> Cannot write data: " + e);
        }
        return jsonObject;
    }

    public static PepperCMSRepository fromJsonObject(JSONObject jsonObject){
        try {
            PepperCMSRepository repository = null;
            if(jsonObject.has("githubRepositoryURL"))
                repository = PepperCMSRepository.createGithubRepository(jsonObject.getString("githubRepositoryURL"));
            if(jsonObject.has("simpleRepositoryURL"))
                repository = PepperCMSRepository.createSimpleRepository(jsonObject.getString("simpleRepositoryURL"));
            if(repository != null){
                if(jsonObject.has(REPO_SIMPLE_TAG_TITLE)) repository.title = jsonObject.getString(REPO_SIMPLE_TAG_TITLE);
                if(jsonObject.has(REPO_SIMPLE_TAG_DESC)) repository.description = jsonObject.getString(REPO_SIMPLE_TAG_DESC);
                if(jsonObject.has(REPO_SIMPLE_TAG_LAST_UPDATE)) repository.lastUpdated = jsonObject.getLong(REPO_SIMPLE_TAG_LAST_UPDATE);
            }

            return repository;
        } catch (JSONException | MalformedURLException e){
            Log.d(TAG, "Error converting from JSONObject!");
        }
        return null;
    }

    public ArrayList<PepperApp> getPepperApps() throws MalformedURLException {
        Log.d(TAG, "\t> Crawling repository: " + getRepositoryURL());
        if(isRepositoryChecked() && isRepositoryValid()){

            // crawling data from repository
            try {

                // general repository data
                JSONObject responseObject = PepperCMSRemote.getObject( new URL(getRepositoryURL(), REPO_SIMPLE_JSON_REPO_FILE) );
                if( responseObject != null
                        && responseObject.has(REPO_SIMPLE_TAG_TITLE)
                        && responseObject.has(REPO_SIMPLE_TAG_APPS_ARRAY) ) {
                    try {

                        Log.d(TAG, "\t\t> getting basic repository information");

                        if (responseObject.has(REPO_SIMPLE_TAG_TITLE))
                            this.title = responseObject.getString(REPO_SIMPLE_TAG_TITLE);
                        if (responseObject.has(REPO_SIMPLE_TAG_DESC))
                            this.description = responseObject.getString(REPO_SIMPLE_TAG_DESC);
                        if (responseObject.has(REPO_SIMPLE_TAG_LAST_UPDATE)) {
                            Long updated = responseObject.getLong(REPO_SIMPLE_TAG_LAST_UPDATE);
                            if( updated > lastUpdated ) hasUpdates = true;
                            lastUpdated = updated;
                        }

                        // get apps from sub directories
                        Log.d(TAG, "\t\t> checking for app directories");
                        if (responseObject.has(REPO_SIMPLE_TAG_APPS_ARRAY)) {
                            // get list of apps subdirectories
                            JSONArray appDirectories = responseObject.getJSONArray(REPO_SIMPLE_TAG_APPS_ARRAY);
                            for (int i = 0; i < appDirectories.length(); i++) {
                                String directory = appDirectories.getString(i);
                                Log.d(TAG, "\t\t> looking at sub directory '" + directory + "' for apps...");

                                URL directoryURL = new URL(getRepositoryURL(), directory + "app.json");
                                JSONArray appArray = PepperCMSRemote.getArray(directoryURL);
                                Log.d(TAG, "found array size = " + appArray.length());

                                // crawling apps
                                this.apps.clear();
                                for( int n=0; n<appArray.length(); n++ ){
                                    JSONObject jsonAppData = appArray.getJSONObject(n);
                                    PepperApp app = PepperApp.fromJson(jsonAppData);

                                    if(app != null){
                                        Log.w(TAG, "\t> added");
                                        this.apps.add(app);
                                    } else{
                                        Log.e(TAG, "Cannot parse apps json data!");
                                    }
                                }

                            }

                            return this.apps;
                        }

                    } catch (JSONException e){
                        Log.e(TAG, "Error in parsing json data: " + e);
                    }
                }

            } catch (PepperCMSRemote.AuthFailedException e){
                Log.e(TAG, "\t\t> Authetification failed!");
            }

        } else {
            Log.w(TAG, "\t> Repository is not valid or no checked until now!");
        }

        return null;
    }
}
