package de.fhkiel.pepper.cms.demo.motion;

import android.os.Bundle;
import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
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
        Log.d(TAG, "Robot focus gained.");
        say(R.string.sayFocusGained);
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