package de.fhkiel.pepper.lib.modules;

import android.util.Log;
import de.fhkiel.pepper.lib.PepperLib;

abstract class PepperLibModule {

    protected final String TAG = this.getClass().getName();
    protected PepperLib pepperLib;

    public PepperLibModule(PepperLib pepperLib) {
        this.pepperLib = pepperLib;
    }

    protected void errorNoQiCOntext(String message){
        Log.e(TAG, "NO QiContext found.\n" +
                (message != null ? message + "\n" : "") + "Maybe no application did not gained robot focus!");
    }
}
