package de.fhkiel.pepper.demo.basic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    private static final String TAG = MainActivity.class.getName();

    private JSONObject app;
    private JSONObject data;
    private JSONObject user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this, this);

        // get intent
        Intent intent = getIntent();

        Log.w(TAG, "user: " + intent.getStringExtra("user"));

        if(intent != null && intent.hasExtra("user")){
            String user = intent.getStringExtra("user");
            ((TextView) findViewById(R.id.txtIntentUser)).setText(user);

            try{
                this.user = new JSONObject(user);
            } catch (JSONException e){
                e.printStackTrace();
            }
        } else {
            ((TextView) findViewById(R.id.txtIntentUser)).setText("no intent data 'user'");
        }



        if(intent != null && intent.hasExtra("app")){
            String app = intent.getStringExtra("app");
            ((TextView) findViewById(R.id.txtIntentApp)).setText(app);

            try {
                this.app = new JSONObject(app);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            ((TextView) findViewById(R.id.txtIntentApp)).setText("no intent data 'app'");
        }

        Log.i(TAG, "data " + (intent.hasExtra("data")) );
        if(intent != null && intent.hasExtra("data")){
            String data = intent.getStringExtra("data");
            Log.i(TAG, "data:\n" + data);
            ((TextView) findViewById(R.id.txtIntentData)).setText(data);

            try {
                this.data = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            ((TextView) findViewById(R.id.txtIntentData)).setText("no intent data 'data'");
        }

        findViewById(R.id.btnClose).setOnClickListener(v -> {
            this.finish();
        });

        findViewById(R.id.btnData).setOnClickListener(v -> {
            if(this.data == null){
                this.data = new JSONObject();
            }

            try {
                this.data.put("time", (new Date()).getTime());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            this.returnToCMS();
        });

    }

    private void returnToCMS(){
        Intent returnIntent = new Intent();
        if( this.app != null ) {
            Log.d(TAG, "set results app: " + this.data);
            returnIntent.putExtra("app", this.app.toString());
        }
        if( this.data != null ) {
            Log.d(TAG, "set result data: " + this.data);
            returnIntent.putExtra("data", this.data.toString());
        }
        if( this.user != null ){
            Log.d(TAG, "set result user: " + this.user);
            returnIntent.putExtra("user", this.user.toString());
        }
        Log.d(TAG, "set result intent");
        this.setResult(Activity.RESULT_OK, returnIntent);
    }

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    /**
     * Called when focus is gained
     *
     * @param qiContext the robot context
     */
    @Override
    public void onRobotFocusGained(QiContext qiContext) {

    }

    /**
     * Called when focus is lost
     */
    @Override
    public void onRobotFocusLost() {

    }

    /**
     * Called when focus is refused
     *
     * @param reason the reason
     */
    @Override
    public void onRobotFocusRefused(String reason) {

    }
}