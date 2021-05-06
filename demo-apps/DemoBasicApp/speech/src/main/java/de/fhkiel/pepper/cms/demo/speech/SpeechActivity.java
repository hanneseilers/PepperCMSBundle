package de.fhkiel.pepper.cms.demo.speech;

import android.os.Bundle;

import de.fhkiel.pepper.lib.BasicPepperActivity;

public class SpeechActivity extends BasicPepperActivity {

    // Tag for logging
    private static final String TAG = SpeechActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}