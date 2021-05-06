package de.fhkiel.pepper.cms.demo.basic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import de.fhkiel.pepper.lib.PepperLib;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks, PepperLib.PepperLibCMSCallbackListener {

    // Tag for logging
    private static final String TAG = MainActivity.class.getName();

    // Reference to Pepper Library
    public PepperLib pepperLib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide big speech bar
        //setSpeechBarDisplayStrategy( SpeechBarDisplayStrategy.IMMERSIVE );

        // Register Robot SDK
        QiSDK.register(this, this);

        // Create PepperLib object
        this.pepperLib = new PepperLib();

        // Process intent data from CMS
        this.pepperLib.processCMSIntent(getIntent());

        // close app on button click and set timestamp to return data
        findViewById(R.id.btnClose).setOnClickListener(v -> {
            // Demo setting intent result to return to CMS
            // Data is tored in CMS with the user currently logged in and passed back
            // as Intent data for this user on next App start
            try {
                JSONObject payloadData = new JSONObject();
                payloadData.put( "timestamp", (new Date()).getTime() );
                Log.d( TAG, "set timestamp as result");
            } catch (JSONException e){
                Log.w( TAG, e);
            }


            closeApp();
        });
    }

    /**
     *  Closes app and includes data inside return intent
     */
    private void closeApp(){
        Intent intent = pepperLib.getReturnCMSIntent();
        Log.d( TAG, "set result intent" );
        setResult( Activity.RESULT_OK, intent );
        this.finishAndRemoveTask();
    }

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.pepperLib.onRobotFocusGained(qiContext);
        Log.d(TAG, "Robot focus gained.");
    }

    @Override
    public void onRobotFocusLost() {
        this.pepperLib.onRobotFocusLost();
        Log.w(TAG, "Robot focus lost.");
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        this.pepperLib.onRobotFocusRefused(reason);
        Log.e(TAG, "Robot focus refused!");
    }

    @Override
    public void onCMSIntentProcessed(JSONObject appData, JSONObject userData, JSONObject payloadDdata) {
        // Intent data is ready
        Log.i(TAG, "Processed CMS Intent.");
    }
}