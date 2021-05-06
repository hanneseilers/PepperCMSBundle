package de.fhkiel.pepper.lib;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Main class to hadnle basic PepperLib functions
 */
public class PepperLib implements RobotLifecycleCallbacks {

    private static final String TAG = PepperLib.class.getName();

    // Intent keys cotaining data from CMS
    private static final String INTENT_KEY_APP = "app";
    private static final String INTENT_KEY_DATA = "data";
    private static final String INTENT_KEY_USER = "user";

    // Object to store intent data
    private JSONObject appData;
    private JSONObject payloadData;
    private JSONObject userData;

    // Robot context
    private QiContext qiContext;

    // App Context
    private Context context;

    // Listener
    private ArrayList<PepperLibCMSCallbackListener> pepperLibCMSCallbackListeners = new ArrayList<>();

    public PepperLib(Context context) {
        this.context = context;
    }

    /**
     * Checks if lib has {@link QiContext} object.
     * @return  true if lib has {@link QiContext} object, false otherwise.
     */
    public boolean hasQiContext(){
        return (this.qiContext != null);
    }

    /**
     * Checks if lib has {@link Context} object.
     * @return  true if lib has {@link Context} object, false otherwise.
     */
    public boolean hasContext(){
        return (this.context != null);
    }

    /**
     * Adds a {@link PepperLibCMSCallbackListener} listener to internal listeners list.
     * @param listener  {@link PepperLibCMSCallbackListener} to add
     * @return          true if listener was added, otherwise false
     */
    public boolean addPepperLibCMSCallbackListener(PepperLibCMSCallbackListener listener){
        return this.pepperLibCMSCallbackListeners.add(listener);
    }

    /**
     * Removes a {@link PepperLibCMSCallbackListener} from internal list.
     * @param listener  {@link PepperLibCMSCallbackListener} to remove from list.
     * @return          true if successfulle, false otherwise
     */
    public boolean removePepperLibCMSCallbackListener(PepperLibCMSCallbackListener listener){
        return this.pepperLibCMSCallbackListeners.remove(listener);
    }

    /**
     * Function to get the current {@link QiContext} object.
     * @return      {@link QiContext} object or null, if no one set.
     */
    public QiContext getQiContext() {
        return qiContext;
    }

    /**
     * Function to get urrent {@link Context} object.
     * @return      {@link Context} object or null, if no one set.
     */
    public Context getContext(){
        return context;
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;
        Log.d(TAG, "Gained robot focus.");
    }

    @Override
    public void onRobotFocusLost() {
        this.qiContext = null;
        Log.w(TAG, "Lost robot focus!");
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        this.qiContext = null;
        Log.e(TAG, "Robot focus refused! " + reason);
    }

    /**
     * Reads data from an {@link Intent}.
     * Needs to be done after activity started.
     * @param intent    {@link Intent} of apps activity
     * @return          true, if intent query was started, false otherwise
     *                  This normally happens, if there is no intent.
     */
    public boolean processCMSIntent(Intent intent){
        if(intent != null){
            new Thread(() -> {

                // Handling intent data
                //Log.d( TAG, "app: " + intent.getStringExtra(INTENT_KEY_APP) );
                //Log.d( TAG, "user: " + intent.getStringExtra(INTENT_KEY_USER) );
                //Log.d( TAG, "data: " + intent.getStringExtra(INTENT_KEY_DATA) );

                // Get Intent data from CMS, if available and store them in JSONObjects

                // App Data: Information about this app
                // Data structure defined by CMS
                if( intent.hasExtra(INTENT_KEY_APP) ){
                    try {
                        this.appData = new JSONObject( intent.getStringExtra(INTENT_KEY_APP) );
                    } catch (JSONException e){
                        Log.w( TAG, e);
                    }
                }

                // User Data: Data about the user loged in to the CMS
                // Data structure defined by CMS
                if( intent.hasExtra(INTENT_KEY_USER) ){
                    try {
                        this.userData = new JSONObject( intent.getStringExtra(INTENT_KEY_USER) );
                    } catch (JSONException e){
                        Log.w( TAG, e);
                    }
                }

                // Payload Data: Data stored by this app inside CMS for the logged in user.
                // Only has to be valid JSON, structure defined by this App
                if( intent.hasExtra(INTENT_KEY_DATA) ){
                    try {
                        this.payloadData = new JSONObject( intent.getStringExtra(INTENT_KEY_DATA) );
                    } catch (JSONException e){
                        Log.w( TAG, e);
                    }
                }

                // call listener
                for( PepperLibCMSCallbackListener listener : this.pepperLibCMSCallbackListeners ){
                    listener.onCMSIntentProcessed(this.appData, this.userData, this.payloadData);
                }

            }).start();
            return true;
        }
        return false;
    }

    /**
     * Creates a {@link Intent} for returning to CMS on clsoing this app
     * @return  Intent to return to CMS
     */
    public Intent getReturnCMSIntent(){
        Intent intent = new Intent();

        if( this.appData != null ){
            intent.putExtra( INTENT_KEY_APP, this.appData.toString() );
        }

        if( this.userData != null ){
            intent.putExtra( INTENT_KEY_USER, this.userData.toString() );
        }

        if( this.payloadData != null ){
            intent.putExtra( INTENT_KEY_DATA, this.payloadData.toString() );
        }

        return intent;
    }

    /**
     * Gets informations about this app, passed via intent from CMS
     * @return  Data of this app, null if not available.
     */
    public JSONObject getAppData(){
        return this.appData;
    }

    /**
     * Gets informations about the logged in uer, passed via intent from CMS
     * @return  Data of the logged in user, null if not available.
     */
    public JSONObject getUserData(){
        return this.userData;
    }

    /**
     * Gets payload informations for the logged in user and this app, passed via intent from CMS
     * @return  Payload data of this app, null if not available.
     */
    public JSONObject getPayloadData(){
        return this.payloadData;
    }

    /**
     * Sets the payload data for the logged in user and this app.
     * Can have any structure the app wants to use.
     * @param payloadData   Data to set as payload.
     */
    public void setPayloadData(JSONObject payloadData){
        this.payloadData = payloadData;
    }

    /**
     * Interface to get listener callbacks for callbacks for CMS based functions
     */
    public interface PepperLibCMSCallbackListener {
        /**
         * Called if intent data was queried
         * @param appData       Data about this app, stored in the CMS
         * @param userData      Data about the user logged in
         * @param payloadDdata  User specific payload data, stored by this app insied CMS
         */
        void onCMSIntentProcessed(JSONObject appData, JSONObject userData, JSONObject payloadDdata);
    }

}
