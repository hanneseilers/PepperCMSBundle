package de.fhkiel.pepper.lib;

import android.util.Log;

import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;

/**
 * Class for basic motion commands
 */
public class PepperMotion {

    private static final String TAG = PepperMotion.class.getName();

    private PepperLib pepperLib;

    /**
     * Constructor
     * @param pepperLib     Reference object to {@link PepperLib}.
     */
    public PepperMotion(PepperLib pepperLib) {
        this.pepperLib = pepperLib;
    }

    /**
     * Creates an animation for the robot.
     * @param animationRescource    Android rescource of the animation (*.qanim)
     * @return                      Object with {@link Animate} interface, to start the animation or attach listeners.
     */
    public Animate createAnimation(int animationRescource){
        if(pepperLib.hasContext() && pepperLib.hasQiContext()){
            String rescourceName = pepperLib.getContext().getResources().getResourceName(animationRescource);
            Log.d(TAG, "---- execute animation: " + rescourceName);

            Animation animation = AnimationBuilder.with(pepperLib.getQiContext())
                    .withResources(animationRescource)
                    .build();
            Animate animate = AnimateBuilder.with(pepperLib.getQiContext())
                    .withAnimation(animation)
                    .build();

            return animate;
        }

        return null;
    }

}
