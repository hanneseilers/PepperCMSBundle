package de.fhkiel.pepper.cms.demo.basic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import de.fhkiel.pepper.lib.BasicPepperActivity;

public class BasicActivity extends BasicPepperActivity{

    // Tag for logging
    private static final String TAG = BasicActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide big speech bar
        setSpeechBarStrategy(SpeechBarDisplayStrategy.IMMERSIVE, SpeechBarDisplayPosition.TOP);

        // close app on button click and set timestamp to return data
        findViewById(R.id.btnClose).setOnClickListener(v -> {
            // Demo setting intent result to return to CMS
            // Data is stored in CMS with the user currently logged in and passed back
            // as Intent data for this user on next App start
            try {
                JSONObject payloadData = new JSONObject();
                payloadData.put( "timestamp", (new Date()).getTime() );
                getPepperLib().setPayloadData(payloadData);
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
        Intent intent = getPepperLib().getReturnCMSIntent();
        Log.d( TAG, "set result intent" );
        setResult( Activity.RESULT_OK, intent );
        this.finishAndRemoveTask();
    }

    /*
        Implementing RobotLifecycleCallbacks interface to get callbacks on robot focus.
        ATTENTION! The extended BasicPepperActivity handles the registration at the QiSDK
        Also super() Function must be called! Otherwise the PepperLib is not working!
     */

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        super.onRobotFocusGained(qiContext);
        Log.i(TAG, "Robot focus gained.");
    }

    @Override
    public void onRobotFocusLost() {
        super.onRobotFocusLost();
        Log.w(TAG, "Robot focus lost!");
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        super.onRobotFocusRefused(reason);
        Log.e(TAG, "Robot focus refused: " + reason + "!");
    }
}