package de.fhkiel.pepper.demo.motion;

import android.os.Bundle;
import android.util.Log;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private static final String TAG = MainActivity.class.getName();

    private QiContext qiContext;

    // Attributes to save animations
    private Animate animateScan;
    private Animate animateDance;
    private Animate animateMove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this, this);

        // TODO: Connect buttons to motion function (see below)
        findViewById(R.id.btnScan).setOnClickListener(v -> {
            motion(animateScan);
        });

        findViewById(R.id.btnDance).setOnClickListener(v -> {
            motion(animateDance);
        });

        findViewById(R.id.btnMove).setOnClickListener(v -> {
            motion(animateMove);
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

        // TODO: Create a animation from resource
        Animation animationScan = AnimationBuilder.with(qiContext)
                .withResources(R.raw.animation_scan).build();
        Animation animationDance = AnimationBuilder.with(qiContext)
                .withResources(R.raw.dance_b003).build();
        Animation animationMove = AnimationBuilder.with(qiContext)
                .withResources(R.raw.trajectory_move).build();

        // TODO: Build the animation ans save it to activity
        this.animateScan = AnimateBuilder.with(qiContext)
                .withAnimation(animationScan).build();
        this.animateDance = AnimateBuilder.with(qiContext)
                .withAnimation(animationDance).build();
        this.animateMove = AnimateBuilder.with(qiContext)
                .withAnimation(animationMove).build();
    }

    @Override
    public void onRobotFocusLost() {
        this.qiContext = null;

        // TODO: Remove listeners from animates
        if(animateScan != null){
            animateScan.removeAllOnStartedListeners();
        }
        if(animateDance != null){
            animateDance.removeAllOnStartedListeners();
        }
        if(animateMove != null){
            animateMove.removeAllOnStartedListeners();
        }
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        this.qiContext = null;
    }

    private void motion(Animate animate){
        new Thread(() -> {

            // Run the animate as asnc future
            Future<Void> future = animate.async().run();

            // Add a listener if animation starts
            animate.addOnStartedListener(() -> {
                Log.i( TAG, "Animation started." );
            });

            // Add listeners, if animation ends
            future.thenConsume(voidFuture -> {

                if( voidFuture.isSuccess() ){
                    Log.i(TAG, "Animation successfull stopped.");
                } else if( voidFuture.hasError() ){
                    Log.e(TAG, "Animation finished with error!", voidFuture.getError() );
                }

            });

        }).start();
    }
}