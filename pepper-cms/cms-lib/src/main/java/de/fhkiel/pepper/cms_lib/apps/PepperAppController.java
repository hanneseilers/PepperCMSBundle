package de.fhkiel.pepper.cms_lib.apps;

import java.util.ArrayList;

import de.fhkiel.pepper.cms_lib.users.User;

/**
 * Interface of objects that can control the app handling.
 * Implementing functions to load and start {@link PepperApp}s.
 */
public interface PepperAppController {

    ArrayList<PepperAppInterface> pepperAppInterfaceListener = new ArrayList<>();

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

    default void addPepperAppInterfaceListener(PepperAppInterface listener){
        if(!pepperAppInterfaceListener.contains(listener)){
            pepperAppInterfaceListener.add(listener);
        }
    };

    default void removePepperAppInterfaceListener(PepperAppInterface listener){
        if(pepperAppInterfaceListener.contains(listener)){
            pepperAppInterfaceListener.remove(listener);
        }
    }

}
