package de.fhkiel.pepper.cms.demo.basic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import de.fhkiel.pepper.lib.BasicPepperActivity;
import de.fhkiel.pepper.lib.PepperLib;

public class BasicActivity extends BasicPepperActivity {

    // Tag for logging
    private static final String TAG = BasicActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // close app on button click and set timestamp to return data
        findViewById(R.id.btnClose).setOnClickListener(v -> {
            // Demo setting intent result to return to CMS
            // Data is tored in CMS with the user currently logged in and passed back
            // as Intent data for this user on next App start
            try {
                JSONObject payloadData = new JSONObject();
                payloadData.put( "timestamp", (new Date()).getTime() );
                this.pepperLib.setPayloadData(payloadData);
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

}