package de.fhkiel.pepper.cms.demo.speech;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import de.fhkiel.pepper.lib.PepperLibActivity;

public class FramentListen extends Fragment {

    private static final String TAG = FramentListen.class.getName();
    private PepperLibActivity pepperLibActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_listen, container,false );
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof PepperLibActivity){
            this.pepperLibActivity = (PepperLibActivity) context;
        } else {
            throw new ClassCastException(context.toString() +
                    " must implement PepperLibActivity interface!");
        }
    }
}
