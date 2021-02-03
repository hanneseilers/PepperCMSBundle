package de.fhkiel.pepper.cms.demobasicapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private static final String TAG = MainActivity.class.getName();

    private static final String INTENT_KEY_APP = "app";
    private static final String INTENT_KEY_DATA = "data";
    private static final String INTENT_KEY_USER = "user";

    private JSONObject app;
    private JSONObject data;
    private JSONObject user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this, this);

        Intent intent = getIntent();
        Log.w( TAG, "app: " + intent.getStringExtra(INTENT_KEY_APP) );
        Log.w( TAG, "user: " + intent.getStringExtra(INTENT_KEY_USER) );
        Log.w( TAG, "data: " + intent.getStringExtra(INTENT_KEY_DATA) );

        if( intent.hasExtra(INTENT_KEY_APP) ){
            ((TextView) findViewById(R.id.txtIntentApp)).setText( intent.getStringExtra(INTENT_KEY_APP) );

            try {
                this.app = new JSONObject( intent.getStringExtra(INTENT_KEY_APP) );
            } catch (JSONException e){
                Log.w( TAG, e);
            }
        }

        if( intent.hasExtra(INTENT_KEY_USER) ){
            ((TextView) findViewById(R.id.txtIntentUser)).setText( intent.getStringExtra(INTENT_KEY_USER) );

            try {
                this.user = new JSONObject( intent.getStringExtra(INTENT_KEY_USER) );
            } catch (JSONException e){
                Log.w( TAG, e);
            }
        }

        if( intent.hasExtra(INTENT_KEY_DATA) ){
            ((TextView) findViewById(R.id.txtIntentData)).setText( intent.getStringExtra(INTENT_KEY_DATA) );

            try {
                this.data = new JSONObject( intent.getStringExtra(INTENT_KEY_DATA) );
            } catch (JSONException e){
                Log.w( TAG, e);
            }
        }

        findViewById(R.id.btnClose).setOnClickListener(v -> {
            if( this.data == null ){
                this.data = new JSONObject();
            }

            try {
                this.data.put("timestamp", (new Date()).getTime());
                Log.d( TAG, "set timestamp as result");
            } catch (JSONException e){
                Log.w( TAG, e);
            }

            closeApp();
        });
    }

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
    public void onRobotFocusGained(QiContext qiContext) {}

    @Override
    public void onRobotFocusLost() {}

    @Override
    public void onRobotFocusRefused(String reason) {}
}