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

    private Animate animateScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this, this);

        findViewById(R.id.btnScan).setOnClickListener(v -> {
            new Thread(() -> {
                Future<Void> animateFuture = animateScan.async().run();
                animateScan.addOnStartedListener(() -> {
                    Log.i( TAG, "Animation Scan started." );
                });
                animateFuture.thenConsume(voidFuture -> {
                    if( voidFuture.isSuccess() ){
                        Log.i(TAG, "Animation Scan successfull stopped.");
                    } else if( voidFuture.hasError() ){
                        Log.e(TAG, "Animation Scan finished with error!", voidFuture.getError() );
                    }
                });
            }).start();
        });
    }

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Animation animationScan = AnimationBuilder.with(qiContext)
                .withResources(R.raw.animation_scan).build();
        animateScan = AnimateBuilder.with(qiContext)
                .withAnimation(animationScan).build();
    }

    @Override
    public void onRobotFocusLost() {
        if(animateScan != null){
            animateScan.removeAllOnStartedListeners();
        }
    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }
}