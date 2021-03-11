package de.fhkiel.pepper.cms.demo.motion;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.Say;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private static final String TAG = MainActivity.class.getName();

    private QiContext qiContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        QiSDK.register(this, this);
    }

    /**
     * Say something
     * @param rescource Rescource id of the text to say
     */
    private void say(int rescource) {
        if (this.qiContext != null) {
            new Thread(() -> {

                // Synchronize to wait, if robot is already talking
                synchronized (this) {
                    Log.d(TAG, "---- say: " + getString(rescource));
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

    /**
     * Executes an animation with given animation rescource.
     * @param animationRescource        Rescource of animation
     */
    private void executeAnimation(int animationRescource){

        if(this.qiContext != null) {
            new Thread(() -> {
                String rescourceName = getResources().getResourceName(animationRescource);
                setAnimationLabelText( "" );
                Log.d(TAG, "---- execute animation: " + rescourceName);
                Animation animation = AnimationBuilder.with(this.qiContext)
                        .withResources(animationRescource)
                        .build();
                Animate animate = AnimateBuilder.with(this.qiContext)
                        .withAnimation(animation)
                        .build();

                // attach label listener
                animate.addOnStartedListener(() -> {
                    Log.d(TAG, "---- " + rescourceName + " started");
                });
                animate.addOnLabelReachedListener((label, time) -> {
                    Log.i(TAG, "LABEL REACHED: " + label + " @ " + time);
                    setAnimationLabelText( time + ": " + label );
                });

                Future<Void> future = animate.async().run();
                future.andThenConsume(value -> {
                    Log.d(TAG, "---- animation "  + rescourceName + " ended");
                    setAnimationLabelText( "" );
                });

            }).start();
        } else {
            Log.e(TAG, "---- no context");
        }

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
        this.qiContext = qiContext;
        Log.d(TAG, "Robot focus gained.");
        say(R.string.sayFocusGained);

        // connect buttons
        findViewById(R.id.btnAnimation).setOnClickListener(v -> {
            executeAnimation(R.raw.dance_b005);
        });
        findViewById(R.id.btnAnimationLabels).setOnClickListener(v -> {
            executeAnimation(R.raw.salute_right_b001);
        });
        findViewById(R.id.btnTrajectory).setOnClickListener(v -> {
            executeAnimation(R.raw.trajectory_00);
        });
    }

    @Override
    public void onRobotFocusLost() {
        this.qiContext = null;
        Log.e(TAG, "Robot focus lost!");
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        this.qiContext = null;
        Log.w(TAG, "Robot focus refused!");
    }
}