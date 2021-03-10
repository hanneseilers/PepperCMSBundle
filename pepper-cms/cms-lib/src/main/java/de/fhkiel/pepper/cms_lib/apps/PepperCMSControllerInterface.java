package de.fhkiel.pepper.cms_lib.apps;

import android.content.Intent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.fhkiel.pepper.cms_lib.apps.PepperApp;
import de.fhkiel.pepper.cms_lib.apps.PepperAppInterface;
import de.fhkiel.pepper.cms_lib.repository.PepperCMSRepository;
import de.fhkiel.pepper.cms_lib.users.User;

/**
 * Interface of objects that can control the app handling.
 * Implementing functions to load and start {@link PepperApp}s.
 */
public interface PepperCMSControllerInterface {

    ArrayList<PepperAppInterface> pepperAppInterfaceListener = new ArrayList<>();

    /**
     * Function to start the CMS.
     * @param useOnline     Use true to check online rescources (if available), otherwise false.
     * @return              Return true, if start was successfull, false otherwise.
     */
    boolean startCMS(boolean useOnline, boolean isRestart);

    default boolean startCMS(boolean useOnline){
        return startCMS(useOnline, false);
    }

    /**
     * Restarts the CMS ith the same config, as on first start
     * @return      Return true, if start was successfull, false otherwise.
     */
    boolean retstartCMS();

    /**
     * Starts a {@link PepperApp} via {@link android.content.Intent}.
     *
     * @param app {@link PepperApp} to start.
     * @param user {@link User} authenticated, null if none found.
     * @return true if successful, false otherwise.
     */
    boolean startPepperApp(PepperApp app, User user);

    /**
     * Starts a {@link PepperApp} via {@link android.content.Intent}.
     *
     * @param app {@link PepperApp} to start.
     * @return true if successful, false otherwise.
     */
    default boolean startPepperApp(PepperApp app){
        return startPepperApp(app, null);
    }

    /**
     * Loads {@link PepperApp}s from local file rescources.
     */
    void loadLocalPepperApps();

    /**
     * Loads {@link PepperApp}s from online source
     */
    void loadRemotePepperApps() throws IOException;

    /**
     * Function to load available {@link PepperApp}
     * Notifies all listeners, if apps loaded.
     * @param load  If set to true, apps are also loaded from online rescource
     */
    void getPepperApps(boolean load);

    /**
     * Gets list of {@link PepperApp}s, ready for update.
     * @return  {@link HashMap} of {@link PepperApp}s.
     */
    HashMap<Integer, PepperApp> getUpdateablePepperApps();

    default void addPepperAppInterfaceListener(PepperAppInterface listener){
        if(!pepperAppInterfaceListener.contains(listener)){
            pepperAppInterfaceListener.add(listener);
        }
    }

    default void removePepperAppInterfaceListener(PepperAppInterface listener){
        pepperAppInterfaceListener.remove(listener);
    }

    /**
     * Needs to be called, if intent return result. Handles whatever needs to be done with the result of an app.
     * @param requestCode   {@link Integer} of returns request code
     * @param resultCode    {@link Integer} of returns result code (for example {@link android.app.Activity}.RESULT_OK [-1] if app exited gracefully)
     * @param data          {@link String} of data to handle (for example store) for an app.
     */
    void onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * Sets path where to store game files.     *
     * @param path  {@link String} of path
     */
    void setStoragePath(String path);

    /**
     * Gets the currently authenticated user
     * @return  {@link User}, null if no one authenticated
     */
    User getAuthenticatedUser();

    /**
     * Gets a default user object. Can be used to get a valid {@link User} object, if no user is authenticated.
     * @return  {@link User} object of default user.
     */
    User getDefaultUser();

    /**
     * Sets the current authenticated {@link User}. Use null, if no one is authenticated or user authentification got lost.
     * @param user {@link User} to set as autheticated one.
     */
    void setAuthenticatedUser(User user);

    /**
     * Gets list of available app repositories
     * @return  {@link ArrayList} of available {@link PepperCMSRepository}<
     */
    ArrayList<PepperCMSRepository> getRepositories();

    /**
     * Adds a new {@link PepperCMSRepository} to repository list.
     * @param repo      {@link PepperCMSRepository} to add. It will only be added, if repository is not already in list.
     * @return          {@link ArrayList} of available {@link PepperCMSRepository}s
     */
    ArrayList<PepperCMSRepository> addRepository(PepperCMSRepository repo);

    /**
     * Removes one {@link PepperCMSRepository} from repository list.
     * @param repo  {@link PepperCMSRepository} to remove. It will only be removed, if repository is already in list.
     * @return      {@link ArrayList} of available {@link PepperCMSRepository}s
     */
    ArrayList<PepperCMSRepository> removeRepository(PepperCMSRepository repo);

    /**
     * Clears all repository data.
     * @return  true, if successfull, false otherwise.
     */
    boolean clearRepositories();

}
