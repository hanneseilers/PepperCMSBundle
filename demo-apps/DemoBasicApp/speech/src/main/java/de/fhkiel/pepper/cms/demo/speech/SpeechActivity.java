package de.fhkiel.pepper.cms.demo.speech;

import android.os.Bundle;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;

import de.fhkiel.pepper.lib.BasicPepperActivity;

public class SpeechActivity extends BasicPepperActivity {

    @SuppressWarnings("CodeBlock2Expr")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSpeechBarStrategy(SpeechBarDisplayStrategy.IMMERSIVE, SpeechBarDisplayPosition.TOP);

        findViewById(R.id.btnMenuSay).setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, FragmentSay.class, null)
                    .setReorderingAllowed(true)
                    .commit();
        });
        findViewById(R.id.btnMenuListen).setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, FragmentListen.class, null)
                    .setReorderingAllowed(true)
                    .commit();
        });
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        super.onRobotFocusGained(qiContext);
    }
}