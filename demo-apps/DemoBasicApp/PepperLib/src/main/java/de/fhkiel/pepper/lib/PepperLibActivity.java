package de.fhkiel.pepper.lib;

import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;

public interface PepperLibActivity {
    PepperLib getPepperLib();

    /**
     * Sets the robot speechbar options.Should be calle in onCreate()
     * @param strategy  {@link SpeechBarDisplayStrategy} strategy
     * @param position  {@link SpeechBarDisplayPosition} position
     */
    void setSpeechBarStrategy(SpeechBarDisplayStrategy strategy, SpeechBarDisplayPosition position);
}
