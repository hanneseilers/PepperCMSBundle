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

    private static final String TAG = MainActivity.class.getName();

    private static final String INTENT_KEY_APP = "app";
    private static final String INTENT_KEY_DATA = "data";
    private static final String INTENT_KEY_USER = "user";

    private JSONObject app;
    private JSONObject data;
    private JSONObject user;

    private QiContext qiContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide big speech bar
        setSpeechBarDisplayStrategy( SpeechBarDisplayStrategy.IMMERSIVE );

        QiSDK.register(this, this);

        // Handling intent data
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

        // close app on button click and set timestamp to return data
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

    /**
     * Say something
     * @param rescource Rescource id of the text to say
     */
    private void say(int rescource){
        if( this.qiContext != null ){
            new Thread(() -> {

                // Synchronize to wait, if robot is already talking
                synchronized (this) {
                    Log.i(TAG, "---- say: " + getString(rescource));
                    Phrase phrase = new Phrase(getString(rescource));
                    Say say = SayBuilder
                            .with(this.qiContext)
                            .withPhrase(phrase)
                            .build();
                    say.run();
                }

            }).start();
        } else {
            Log.e(TAG, "---- no context");
        }
    }

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;
        say(R.string.sayFocusGained);
        say(R.string.sayFocusGained);
    }

    @Override
    public void onRobotFocusLost() {
        this.qiContext = null;
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        this.qiContext = null;
    }
}