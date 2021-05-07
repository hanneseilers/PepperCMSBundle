package de.fhkiel.pepper.cms.demo.speech;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;

import java.util.ArrayList;
import java.util.List;

import de.fhkiel.pepper.lib.PepperLibActivity;
import de.fhkiel.pepper.lib.modules.PepperSpeech;

/**
 * Fragment for robots listen function
 */
public class FragmentListen extends Fragment {

    private static final String TAG = FragmentListen.class.getName();
    private PepperLibActivity pepperLibActivity;
    private View rootLayout;

    private ArrayList<String> listPhrases = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
         rootLayout = inflater.inflate( R.layout.fragment_listen, container,false );

         // attach listeners
        // adds a phrase to list
        rootLayout.findViewById(R.id.btnListenPhraseAdd).setOnClickListener(v -> {
            String phrase = ((EditText) rootLayout.findViewById(R.id.txtListenPhrase)).getText().toString();
            Log.i(TAG, "Adding phrase: " + phrase);
            listPhrases.add(phrase);
            updatePhrasesList(listPhrases);
        });

        // clears the list
        rootLayout.findViewById(R.id.btnListenPhraseClear).setOnClickListener(v -> {
            Log.i(TAG, "CLear phrases list");
            listPhrases.clear();
            updatePhrasesList(listPhrases);
        });

        // start listening
        rootLayout.findViewById(R.id.btnListenStart).setOnClickListener(v -> {

            // execute listen only with list of phrases to understand
            if(listPhrases.size() > 0) {
                Log.i(TAG, "Starting listen");
                // disable button
                TextView txtHeardWord = rootLayout.findViewById(R.id.txtListenHeardWord);
                rootLayout.findViewById(R.id.btnListenStart).setEnabled(false);

                // run listen aside UI thread
                new Thread(() -> {
                    String heardWord = listen(listPhrases).getHeardPhrase().getText();
                    Log.i(TAG, "Listen done");

                    // execute UI commands again on UI thread
                    getActivity().runOnUiThread(() -> {
                        txtHeardWord.setText(heardWord);
                        txtHeardWord.invalidate();

                        // enable button
                        rootLayout.findViewById(R.id.btnListenStart).setEnabled(true);

                        // try to disable speechbar
                        pepperLibActivity.setSpeechBarStrategy(SpeechBarDisplayStrategy.IMMERSIVE, SpeechBarDisplayPosition.TOP);
                    });
                }).start();
            } else {
                Toast.makeText(getActivity(), R.string.msgListenNoPhrasesFound, Toast.LENGTH_LONG).show();
            }

        });

         return rootLayout;
    }

    /**
     * Updates list of phrases.
     * @param phrases   {@link List} of phrases to listen to.
     */
    private void updatePhrasesList(List<String> phrases){
        getActivity().runOnUiThread(() -> {
            TextView txtList = rootLayout.findViewById(R.id.txtListenListPhrases);
            String text = "";
            for(String phrase : phrases){
                Log.d(TAG, "adding phrase " + phrase + " to TextView");
                if(text.length() > 0){
                    text += "\n";
                }
                text += phrase;
            }
            txtList.setText(text);
            txtList.invalidate();
            Log.d(TAG, "Updated TextView");
        });
    }

    /**
     * Listens to list of phrases.
     * @param list     {@link List} of phrases to listen to
     * @return         {@link ListenResult}
     */
    private ListenResult listen(List<String> list){
        PepperSpeech speech = new PepperSpeech(pepperLibActivity.getPepperLib());
        List<PhraseSet> phrases = new ArrayList<>();

        // extract comma seperated list as list of PhraseSets
        Log.d(TAG, "building list of Phrases from String list.");
        for(String strPhrases : list){
            PhraseSet phraseSet = speech.createPhraseSet( strPhrases.split(",") );
            phrases.add(phraseSet);
        }

        // create and run listen
        Log.d(TAG, "create and run Listen");
        Listen listen = speech.listen(phrases);
        return listen.run();
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
