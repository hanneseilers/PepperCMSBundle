package de.fhkiel.pepper.demo.app;

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

public class MainActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    private static final String TAG = MainActivity.class.getName();

    private JSONObject app;
    private JSONObject data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this, this);

        // get intent
        Intent intent = getIntent();

        if(intent != null && intent.hasExtra("user")){
            String user = intent.getStringExtra("user");
            ((TextView) findViewById(R.id.txtIntentUser)).setText(user);
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

        if(intent != null && intent.hasExtra("data")){
            String data = intent.getStringExtra("data");
            ((TextView) findViewById(R.id.txtIntentData)).setText(data);

            try {
                this.data = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            ((TextView) findViewById(R.id.txtIntentData)).setText("no intent data 'data'");
        }

        findViewById(R.id.btnClose).setOnClickListener(view -> {
            this.finish();
        });
    }

    @Override
    protected void onDestroy() {
        Intent returnIntent = new Intent();
        if( this.app != null ) {
            Log.d(TAG, "set results app: " + this.data);
            returnIntent.putExtra("app", this.app.toString());
        }
        if( this.data != null ) {
            Log.d(TAG, "set result data: " + this.data);
            returnIntent.putExtra("data", this.data.toString());
        }
        Log.d(TAG, "set result intent");
        this.setResult(Activity.RESULT_OK, returnIntent);
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