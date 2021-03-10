package de.fhkiel.pepper.cms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.gridlayout.widget.GridLayout;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;

import java.net.MalformedURLException;
import java.util.HashMap;

import de.fhkiel.pepper.cms_core.apps.PepperCMSController;
import de.fhkiel.pepper.cms_lib.apps.PepperApp;
import de.fhkiel.pepper.cms_lib.apps.PepperCMSControllerInterface;
import de.fhkiel.pepper.cms_lib.apps.PepperAppInterface;
import de.fhkiel.pepper.cms_lib.repository.PepperCMSRepository;
import de.fhkiel.pepper.cms_lib.users.User;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks, PepperAppInterface {
    private static final String TAG = MainActivity.class.getName();

    private PepperCMSControllerInterface pepperCMS;
    private HashMap<String, PepperApp> pepperApps = new HashMap<>();

    private boolean tryRepository = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "app started");
        QiSDK.register(this, this);

        // disable big speech bar
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.IMMERSIVE);

        // Creating cms app controller
        pepperCMS = new PepperCMSController(this);
        pepperCMS.addPepperAppInterfaceListener(this);
        Log.d(TAG, "cms controller created");

        // ---- UI LOGIC BEGIN ----

        // TODO: add ui locgic
        setContentView(R.layout.activity_main);

        // ---- UI LOGIC END ----

        // after loading ui, get available apps
        new Thread(() -> {
            toast( getString(R.string.toastLoadingApps) );
            pepperCMS.startCMS(true );
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(this.pepperCMS != null){
            this.pepperCMS.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.e(TAG, "cannot give activity result to app controller. null found!");
        }
    }

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    private void showAppsUi(HashMap<String, PepperApp> apps){
        runOnUiThread(() -> {

            // TODO: handle loadig apps into ui

            // simple example
            GridLayout layout = findViewById(R.id.gridApps);
            layout.removeAllViewsInLayout();
            for(String i : apps.keySet()){
                PepperApp app = apps.get(i);
                Button button = new Button(this);
                button.setText(app.getName());
                button.setOnClickListener(view -> {

                    // creating test user
                    // TODO: change
                    Switch swDefaultUser = findViewById(R.id.swDefaultUser);
                    User user = this.pepperCMS.getAuthenticatedUser();
                    if(swDefaultUser.isChecked()) {
                        Log.w(TAG, "Use default user for intent!");
                        user = this.pepperCMS.getDefaultUser();
                    }
                    pepperCMS.startPepperApp(app, user);

                });
                layout.addView(button);
            }

        });
    }
    /**
     * Callback, {@link PepperCMSControllerInterface} uses, if {@link PepperApp}s are loaded.
     *
     * @param apps List of loaded available {@link PepperApp}s
     */
    @Override
    public void onPepperAppsLoaded(HashMap<String, PepperApp> apps, boolean isRemote) {
        Log.i(TAG, "Apps loaded: " + apps.size());
        this.pepperApps = apps;

        if( apps.size() <= 0 ){

            toast( getString(R.string.toastNoApps) );
            Log.d(TAG, "\t> no apps, use repository: " + !tryRepository);
            if(!isRemote) {
                // TODO: add online source from user input
                addTestRepository();
            }

        } else {
            // show apps
            toast( getString(R.string.toastAppsLoaded) );
            showAppsUi(this.pepperApps);
        }
    }

    void addTestRepository(){
        if(!tryRepository) {
            try {

                Log.w(TAG, "\t> No apps found! Add new repository.");
                String repositoryUrl = "https://demo.repos.cms.robotikinderpflege.de";
                tryRepository = true;
                pepperCMS.addRepository(PepperCMSRepository.createSimpleRepository(repositoryUrl));
                Log.d(TAG, "\t> new repository size: " + pepperCMS.getRepositories().size());

                // restart cms
                new Thread(() -> {
                    Log.d(TAG, "\t> restarting CMS ...");
                    Log.i(TAG, "-------------------------------------------------");
                    pepperCMS.retstartCMS();
                }).start();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.w(TAG, "\t> No valid URL: " + e);
            }
        }
    }

    /**
     * Called, if app will be started
     *
     * @param app
     */
    @Override
    public void onAppStarted(PepperApp app) {toast(app.getName() + " started");}

    /**
     * Called if an update for a {@link PepperApp} is available
     *
     * @param app {@link PepperApp} update is available for.
     */
    @Override
    public void onAppUpdateAvailable(PepperApp app) {toast("ready for update: " + app.getName());}

    /**
     * Called if app update starts
     *
     * @param app {@link PepperApp} app updated is started for.
     */
    @Override
    public void onAppUpdate(PepperApp app) {toast("updating: " + app.getName());}

    /**
     * Called if {@link PepperApp} is updated.
     *
     * @param app {@link PepperApp} that is updated.
     */
    @Override
    public void onAppUpdated(PepperApp app) {toast("updated: " + app.getName());}

    /**
     * Called when focus is gained
     *
     * @param qiContext the robot context
     */
    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Log.d(TAG, "Robot focus gained.");
        runOnUiThread(() -> {
            findViewById(R.id.layoutActivityMain).invalidate();
        }); // redraw whole layout if focus comes back
    }

    /**
     * Called when focus is lost
     */
    @Override
    public void onRobotFocusLost() {
        Log.e(TAG, "Robot focus lost!");
    }

    /**
     * Called when focus is refused
     *
     * @param reason the reason
     */
    @Override
    public void onRobotFocusRefused(String reason) {
        Log.w(TAG, "Robot focus refused!");
    }

    /**
     * Called to show a toast.
     * @param text
     */
    private void toast(CharSequence text){
        runOnUiThread(() -> {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        });
    }
}