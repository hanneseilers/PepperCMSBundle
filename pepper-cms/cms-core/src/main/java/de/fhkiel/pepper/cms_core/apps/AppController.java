package de.fhkiel.pepper.cms_core.apps;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import de.fhkiel.pepper.cms_lib.users.User;
import de.fhkiel.pepper.cms_lib.apps.PepperApp;
import de.fhkiel.pepper.cms_lib.apps.PepperAppController;
import de.fhkiel.pepper.cms_lib.apps.PepperAppInterface;

public class AppController implements PepperAppController {
    private static final String TAG = AppController.class.getName();

    private Context context;

    public AppController(Context context) {
        this.context = context;
    }

    @Override
    public boolean startPepperApp(PepperApp app, User user) {
        Log.d(TAG, "trying to start:" + app.getName() + " - " + app.getIntentPackage()+ "/" + app.getIntentClass());
        Intent intent = new Intent();
        intent.setClassName(app.getIntentPackage(), app.getIntentClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra("app", app.toJSONObject().toString() );
        if(user != null && user.getUsername().trim().replace(".", "").length() > 0) {
            intent.putExtra("user", user.toJSONObject().toString());
            if( user.getGamedata().containsKey( app.getName() ) ){
                intent.putExtra("data", user.getGamedata().get( app.getName() ).toString() );
            }
        }

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e){
            Log.e(TAG, "Execption on intent: " + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public void loadPepperApps() {
        new Thread(() -> {

            // TODO: load apps from online source

            // TODO: Modify pepper app list
            String pepperapps = "[" +
                    "  {" +
                    "    \"name\":  \"TestApp\"," +
                    "    \"intentPackage\": \"de.fhkiel.pepper.demo.app\"," +
                    "    \"intentClass\": \"de.fhkiel.pepper.demo.app.MainActivity\"" +
                    "  }" +
                    "]";

            // LOADING APPS FROM RESCOURCE FILE
            Log.d(TAG, "Loading apps from rescource file");
            try {
                JSONArray jsonPepperApps = jsonPepperApps = new JSONArray(pepperapps);
                ArrayList<PepperApp> apps = jsonToPepperApps(jsonPepperApps);
                for (PepperAppInterface listener : PepperAppController.pepperAppInterfaceListener) {
                    Log.d(TAG, "calling callback");
                    listener.onPepperAppsLoaded(apps);
                }
            } catch (JSONException e){
                e.printStackTrace();
            }

        }).start();
    }

    /*
     * Converts a @JSONArray into @ArrayList of @PepperApp objects.
     */
    private ArrayList<PepperApp> jsonToPepperApps(JSONArray jsonArray){
        ArrayList<PepperApp> apps = new ArrayList<>();

        try {
            if(jsonArray!=null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Log.d(TAG, "processing json to PepperApp: " + jsonObject.toString());
                    if (jsonObject.has("name") && jsonObject.has("intentPackage") && jsonObject.has("intentClass")) {
                        PepperApp app = new PepperApp(jsonObject.getString("name"));
                        app.setIntentPackage(jsonObject.getString("intentPackage"));
                        app.setIntentClass(jsonObject.getString("intentClass"));
                        // Add code to add additional rescources here
                        apps.add(app);
                    }
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        return apps;
    }

    /*
     * Reading json data into @JSONArray from resource file.
     */
    private JSONArray readJSONfromRescource(int rescource){
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(rescource), Charset.forName("UTF-8")));
        String json = "";
        String line = "";

        try {
            while ((line = bufferedReader.readLine()) != null) {
                json += line;
            }
            bufferedReader.close();
            return new JSONArray(json);
        } catch (IOException | JSONException e){
            e.printStackTrace();
        }

        return null;
    }

    private void notifyOnAppStart(PepperApp app){
        for(PepperAppInterface listener : PepperAppController.pepperAppInterfaceListener){
            listener.onAppStarted(app);
        }
    }
}
