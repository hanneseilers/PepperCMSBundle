package de.fhkiel.pepper.cms.demo.speech;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;

import java.util.ArrayList;
import java.util.List;

import de.fhkiel.pepper.lib.PepperLibActivity;
import de.fhkiel.pepper.lib.modules.PepperSpeech;

public class FragmentListen extends Fragment {

    private static final String TAG = FragmentListen.class.getName();
    private PepperLibActivity pepperLibActivity;
    private View layout;

    private ArrayList<String> listPhrases = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
         layout = inflater.inflate( R.layout.fragment_listen, container,false );

         // attach listeners
        layout.findViewById(R.id.btnListenPhraseAdd).setOnClickListener(v -> {
            String phrase = ((EditText) layout.findViewById(R.id.txtListenPhrase)).getText().toString();
        });
        layout.findViewById(R.id.btnListenPhraseClear).setOnClickListener(v -> {
            this.listPhrases.clear();
            updatePhrasesList(this.listPhrases);
        });
        layout.findViewById(R.id.btnListenStart).setOnClickListener(v -> {
            getActivity().runOnUiThread(() -> {
                String heardWord = listen(this.listPhrases);
                TextView txtHeardWord = layout.findViewById(R.id.txtListenHeardWord);
                txtHeardWord.setText(heardWord);
                txtHeardWord.invalidate();
            });
        });

         return layout;
    }

    /**
     * Updates list of phrases.
     * @param phrases   {@link List} of phrases to listen to.
     */
    private void updatePhrasesList(List<String> phrases){
        getActivity().runOnUiThread(() -> {
            TextView txtList = layout.findViewById(R.id.txtListenListPhrases);
            String text = "";
            for(String phrase : phrases){
                if(text.length() > 0){
                    text += "\n" + phrase;
                }
            }
            txtList.setText(text);
            txtList.invalidate();
        });
    }

    /**
     * Listens to list of phrases.
     * @param list     {@link List} of phrases to listen to
     * @return         String of heard word
     */
    private String listen(List<String> list){
        PepperSpeech speech = new PepperSpeech(pepperLibActivity.getPepperLib());
        List<PhraseSet> phrases = new ArrayList<>();

        // extract comma seperated list as list of PhraseSets
        for(String strPhrases : list){
            PhraseSet phraseSet = speech.createPhraseSet( strPhrases.split(",") );
            phrases.add(phraseSet);
        }

        // create and run listen
        Listen listen = speech.listen(phrases);
        ListenResult listenResult = listen.run();
        return listenResult.getHeardPhrase().getText();
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
