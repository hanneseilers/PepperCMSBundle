package de.fhkiel.pepper.lib;

import android.os.Bundle;
import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;

import org.json.JSONObject;

/**
 * Basic Activity to extend for own Pepper Applications
 */
@SuppressWarnings("unused")
public class BasicPepperActivity extends RobotActivity implements RobotLifecycleCallbacks, PepperLib.PepperLibCMSCallbackListener, PepperLibActivity {

    // Tag for logging
    private static final String TAG = BasicPepperActivity.class.getName();

    // Reference to Pepper Library
    private PepperLib pepperLib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hide big speech bar
        setSpeechBarDisplayStrategy( SpeechBarDisplayStrategy.IMMERSIVE );

        // Register Robot SDK
        QiSDK.register(this, this);

        // Create PepperLib object
        this.pepperLib = new PepperLib(this);

        // Process intent data from CMS
        this.pepperLib.processCMSIntent(getIntent());

    }

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    @Override
    public PepperLib getPepperLib() {
        return pepperLib;
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
    public void onCMSIntentProcessed(JSONObject appData, JSONObject userData, JSONObject payloadData) {
        // Intent data is ready
        Log.i(TAG, "Processed CMS Intent.");
    }
}