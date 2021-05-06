package de.fhkiel.pepper.lib;

/**
 * Class for robot conversation (speaking and listening)
 */
public class PepperSpeech {

    private static final String TAG = PepperSpeech.class.getName();
    private PepperLib pepperLib;

    /**
     * Constructor
     * @param pepperLib     Reference to {@link PepperLib} object.
     */
    public PepperSpeech(PepperLib pepperLib) {
        this.pepperLib = pepperLib;
    }
}
