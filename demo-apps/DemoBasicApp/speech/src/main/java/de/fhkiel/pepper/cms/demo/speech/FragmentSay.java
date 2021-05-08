package de.fhkiel.pepper.cms.demo.speech;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import de.fhkiel.pepper.lib.PepperLibActivity;

public class FragmentSay extends Fragment {

    private static final String TAG = FragmentListen.class.getName();
    private PepperLibActivity pepperLibActivity;
    private View rootLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        rootLayout = inflater.inflate(R.layout.fragment_say, container, false);
        return rootLayout;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if(context instanceof PepperLibActivity){
            this.pepperLibActivity = (PepperLibActivity) context;
        } else {
            throw new ClassCastException(context.toString() +
                    " must implement PepperLibActivity interface!");
        }
    }

}
