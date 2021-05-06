package de.fhkiel.pepper.cms.demo.motion;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.actuation.Animate;

import de.fhkiel.pepper.lib.PepperLib;
import de.fhkiel.pepper.lib.PepperMotion;

public class MotionActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private static final String TAG = MotionActivity.class.getName();

    // Reference to Pepper Library
    public PepperLib pepperLib;

    // Reference to Pepper Motion Library
    public PepperMotion pepperMotion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide big speech bar
        setSpeechBarDisplayStrategy( SpeechBarDisplayStrategy.IMMERSIVE );

        // Register Robot SDK
        QiSDK.register(this, this);

        // Create PepperLib objects
        this.pepperLib = new PepperLib(this);
        this.pepperMotion = new PepperMotion(pepperLib);

        // Process intent data from CMS
        this.pepperLib.processCMSIntent(getIntent());
    }

    /**
     * Executes an async animation with given animation rescource.
     * @param animationRescource        Rescource of animation
     */
    private void executeAnimationAsync(int animationRescource){
        new Thread(() -> {
            setAnimationLabelText( "" );
            Animate animate = pepperMotion.createAnimation(animationRescource);

            // attach label listener
            animate.addOnStartedListener(() -> {
                Log.d(TAG, "---- started " + animationRescource + " animation");
            });
            animate.addOnLabelReachedListener((label, time) -> {
                Log.i(TAG, "LABEL REACHED: " + label + " @ " + time);
                setAnimationLabelText( time + ": " + label );
            });

            Future<Void> future = animate.async().run();
            future.andThenConsume(value -> {
                Log.d(TAG, "---- animation " + animationRescource + " ended");
                setAnimationLabelText( "" );
            });

        }).start();

    }

    /**
     *  Executes an sync animation with given animation rescource.
     *  Need to be executed outside ui or main loop!
     *  @param animationRescource        Rescource of animation
     */
    private void executeAnimation(int animationRescource){
        setAnimationLabelText( "" );
        Animate animate = pepperMotion.createAnimation(animationRescource);

        // attach label listener
        animate.addOnStartedListener(() -> {
            Log.d(TAG, "---- started " + animationRescource + " animation");
        });
        animate.addOnLabelReachedListener((label, time) -> {
            Log.i(TAG, "LABEL REACHED: " + label + " @ " + time);
            setAnimationLabelText( time + ": " + label );
        });

        // HERE ANIMATION IS NOT EXECUTED ASYNC! FUNCTION WILL WAIT UNTIL IT ENDS
        // YOU CAN NOT DO ANITHING ELSE UNTIL IT FINISHED!
        animate.run();
        Log.d(TAG, "---- animation " + animationRescource + " ended");
        setAnimationLabelText( "" );
    }

    private void setAnimationLabelText(String text){
        runOnUiThread(() -> {
            ((TextView) findViewById(R.id.txtLabels)).setText(text);
        });
    }

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        pepperLib.onRobotFocusGained(qiContext);
        Log.d(TAG, "Robot focus gained.");

        // connect buttons
        findViewById(R.id.btnAnimation).setOnClickListener(v -> {
            executeAnimationAsync(R.raw.dance_b005);
        });
        findViewById(R.id.btnAnimationLabels).setOnClickListener(v -> {
            executeAnimationAsync(R.raw.salute_right_b001);
        });
        findViewById(R.id.btnTrajectory).setOnClickListener(v -> {
            new Thread(() -> {
                Log.i(TAG, "executed before animation");
                executeAnimation(R.raw.trajectory_00);
                Log.i(TAG, "Executed after animation");
            } ).start();
        });
    }

    @Override
    public void onRobotFocusLost() {
        pepperLib.onRobotFocusLost();
        Log.e(TAG, "Robot focus lost!");
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        pepperLib.onRobotFocusRefused(reason);
        Log.w(TAG, "Robot focus refused!");
    }
}