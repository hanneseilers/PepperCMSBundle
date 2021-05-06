package de.fhkiel.pepper.lib.modules;

import android.util.Log;

import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;

import de.fhkiel.pepper.lib.PepperLib;

/**
 * Class for basic motion commands
 */
public class PepperMotion extends PepperLibModule {

    /**
     * Constructor
     * @param pepperLib     Reference to {@link PepperLib} object.
     */
    public PepperMotion(PepperLib pepperLib) {
        super(pepperLib);
    }

    /**
     * Creates an animation for the robot.
     * @param animationResource    Android resource of the animation file.
     * @return                     Object with {@link Animate} interface, to start the animation or attach listeners.
     */
    public Animate createAnimation(int animationResource){
        if(pepperLib.hasContext() && pepperLib.hasQiContext()){
            String resourceName = pepperLib.getContext().getResources().getResourceName(animationResource);
            Log.d(TAG, "---- execute animation: " + resourceName);

            Animation animation = AnimationBuilder.with(pepperLib.getQiContext())
                    .withResources(animationResource)
                    .build();

            return AnimateBuilder.with(pepperLib.getQiContext())
                    .withAnimation(animation)
                    .build();
        }

        errorNoQiContext();
        return null;
    }

}
