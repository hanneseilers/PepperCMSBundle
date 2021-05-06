package de.fhkiel.pepper.lib.modules;

import android.util.Log;
import de.fhkiel.pepper.lib.PepperLib;

abstract class PepperLibModule {

    protected final String TAG = this.getClass().getName();
    protected PepperLib pepperLib;

    public PepperLibModule(PepperLib pepperLib) {
        this.pepperLib = pepperLib;
    }

    /**
     * Showing log error that no {@link com.aldebaran.qi.sdk.QiContext} is found.
     * Maybe due to no robot focus.
     * @param message   {@link String} message to sho.
     */
    protected void errorNoQiCOntext(String message){
        Log.e(TAG, "No QiContext found!\n" +
                (message != null ? message + "\n" : "") + "Maybe no application did not gained robot focus!");
    }

    /**
     * Showing log error that no {@link com.aldebaran.qi.sdk.QiContext} is found.
     * Maybe due to no robot focus.
     */
    protected void errorNoQiCOntext(){
        errorNoQiCOntext(null);
    }
}
