package de.fhkiel.pepper.cms.demo.basic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.Say;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    // Tag for logging
    private static final String TAG = MainActivity.class.getName();

    // Intent keys cotaining data from CMS
    private static final String INTENT_KEY_APP = "app";
    private static final String INTENT_KEY_DATA = "data";
    private static final String INTENT_KEY_USER = "user";

    // Object to store intent data
    private JSONObject app;
    private JSONObject data;
    private JSONObject user;

    // Robot context
    private QiContext qiContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide big speech bar
        //setSpeechBarDisplayStrategy( SpeechBarDisplayStrategy.IMMERSIVE );

        // Register Robot SDK
        QiSDK.register(this, this);

        // Handling intent data
         Intent intent = getIntent();
        Log.w( TAG, "app: " + intent.getStringExtra(INTENT_KEY_APP) );
        Log.w( TAG, "user: " + intent.getStringExtra(INTENT_KEY_USER) );
        Log.w( TAG, "data: " + intent.getStringExtra(INTENT_KEY_DATA) );

        // Get Intent data from CMS, if available and store them in JSONObjects

        // App Data: Information about this app
        // Data structure defined by CMS
        if( intent.hasExtra(INTENT_KEY_APP) ){
            try {
                this.app = new JSONObject( intent.getStringExtra(INTENT_KEY_APP) );
            } catch (JSONException e){
                Log.w( TAG, e);
            }
        }

        // User Data: Data about the user loged in to the CMS
        // Data structure defined by CMS
        if( intent.hasExtra(INTENT_KEY_USER) ){
            try {
                this.user = new JSONObject( intent.getStringExtra(INTENT_KEY_USER) );
            } catch (JSONException e){
                Log.w( TAG, e);
            }
        }

        // Data: Data stored by this app inside CMS for the logged in user.
        // Only has to be valid JSON, structure defined by this App
        if( intent.hasExtra(INTENT_KEY_DATA) ){
            try {
                this.data = new JSONObject( intent.getStringExtra(INTENT_KEY_DATA) );
            } catch (JSONException e){
                Log.w( TAG, e);
            }
        }

        // close app on button click and set timestamp to return data
        findViewById(R.id.btnClose).setOnClickListener(v -> {
            if( this.data == null ){
                this.data = new JSONObject();
            }

            // Demo setting intent result to return to CMS
            // Data is tored in CMS with the user currently logged in and passed back
            // as Intent data for this user on next App start
            /*
            try {
                this.data.put("timestamp", (new Date()).getTime());
                Log.d( TAG, "set timestamp as result");
            } catch (JSONException e){
                Log.w( TAG, e);
            }
            */

            closeApp();
        });
    }

    /**
     *  Closes app and includes data inside return intent
     */
    private void closeApp(){
        Intent intent = new Intent();

        if( this.app != null ){
            intent.putExtra( INTENT_KEY_APP, this.app.toString() );
        }

        if( this.user != null ){
            intent.putExtra( INTENT_KEY_USER, this.user.toString() );
        }

        if( this.data != null ){
            intent.putExtra( INTENT_KEY_DATA, this.data.toString() );
        }

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
        this.qiContext = qiContext;
        Log.d(TAG, "Robot focus gained.");
    }

    @Override
    public void onRobotFocusLost() {
        this.qiContext = null;
        Log.w(TAG, "Robot focus lost.");
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        this.qiContext = null;
        Log.e(TAG, "Robot focus refused!");
    }
}