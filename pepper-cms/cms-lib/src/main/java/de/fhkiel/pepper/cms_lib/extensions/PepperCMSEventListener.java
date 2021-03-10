package de.fhkiel.pepper.cms_lib.extensions;

import de.fhkiel.pepper.cms_lib.apps.PepperApp;
import de.fhkiel.pepper.cms_lib.apps.PepperAppInterface;
import de.fhkiel.pepper.cms_lib.users.User;

/**
 * Interface for extensions to implement to get event data from cms
 */
@SuppressWarnings("SameReturnValue")
public interface PepperCMSEventListener extends PepperAppInterface {

    /**
     * Request, if a app can start.
     * @param app   {@link PepperApp} to start.
     * @return  Should return true, to start the app. If false returned, app is not started.
     */
    default boolean onRequestAppStart(PepperApp app) {return true;}

    /**
     * Function to get restart delay fo an App.
     * Called, if app start request failed, this function will be called to get delay when to try a retry.
     * @param app   {@link PepperApp} to aks request delay for.
     * @return  {@link Integer} of how man many ms to delay app start
     */
    int getAppRestartDelay(PepperApp app);

    /**
     * Called if systems gains robot focus
     */
    void onRobotFocusGained();

    /**
     * Called if system looses robot system.
     */
    void onRobotFocusLost();

    /**
     * Called to request extension start.
     */
    void onStart();

    /**
     * Called to request extension stop.
     */
    void onStop();

    /**
     * called if autheticated user changed.
     * @param user  {@link User} of new user.
     */
    void onUserChanged(User user);
}
