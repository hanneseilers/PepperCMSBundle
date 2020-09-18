package de.fhkiel.pepper.demo.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;

public class MainActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

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
        } else {
            ((TextView) findViewById(R.id.txtIntentApp)).setText("no intent data 'app'");
        }

        if(intent != null && intent.hasExtra("data")){
            String app = intent.getStringExtra("data");
            ((TextView) findViewById(R.id.txtIntentData)).setText(app);
        } else {
            ((TextView) findViewById(R.id.txtIntentData)).setText("no intent data 'data'");
        }

        findViewById(R.id.btnClose).setOnClickListener(view -> {
            this.finish();
        });
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