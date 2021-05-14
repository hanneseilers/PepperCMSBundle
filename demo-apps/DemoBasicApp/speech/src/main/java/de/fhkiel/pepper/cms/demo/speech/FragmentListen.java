package de.fhkiel.pepper.cms.demo.speech;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.conversation.BodyLanguageOption;
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.locale.Language;
import com.aldebaran.qi.sdk.object.locale.Region;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.fhkiel.pepper.lib.PepperLibActivity;
import de.fhkiel.pepper.lib.modules.PepperSpeech;

/**
 * Fragment for robots listen function
 */
@SuppressWarnings("ConstantConditions")
public class FragmentListen extends Fragment {

    private static final String TAG = FragmentListen.class.getName();
    private PepperLibActivity pepperLibActivity;
    private View rootLayout;

    private PepperSpeech pepperSpeech;
    private final ArrayList<String> listPhrases = new ArrayList<>();

    @SuppressWarnings("CodeBlock2Expr")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
         rootLayout = inflater.inflate( R.layout.fragment_listen, container,false );

         // populate spinner locale options
        // get widgets
        Spinner spinLanguage = rootLayout.findViewById(R.id.spinListenLanguage);
        Spinner spinRegion = rootLayout.findViewById(R.id.spinListenRegion);
        Spinner spinBodyLanguage = rootLayout.findViewById(R.id.spinListenBodyLanguage);

        // convert option enums to string lists
        List<String> languages = new ArrayList<>();
        for(Language language : Language.values()){
            languages.add( language.name() );
        }
        List<String> regions = new ArrayList<>();
        for(Region region : Region.values()){
            regions.add( region.name() );
        }
        List<String> bodyLanguageOptions = new ArrayList<>();
        for(BodyLanguageOption bodyLanguageOption : BodyLanguageOption.values()){
            bodyLanguageOptions.add( bodyLanguageOption.name() );
        }

        // populate spinners
        ArrayAdapter<String> spinnerAdapterLanguage = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, languages);
        ArrayAdapter<String> spinnerAdapterRegion = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, regions);
        ArrayAdapter<String> spinnerAdapterBodyLanguageOption = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, bodyLanguageOptions);
        spinLanguage.setAdapter( spinnerAdapterLanguage );
        spinRegion.setAdapter( spinnerAdapterRegion );
        spinBodyLanguage.setAdapter( spinnerAdapterBodyLanguageOption );

        // set defaults
        int nLanguage = spinnerAdapterLanguage.getPosition( getPepperSpeech().getListenLanguage().name() );
        int nRegion = spinnerAdapterRegion.getPosition( getPepperSpeech().getListenRegion().name() );
        int nBoyLanguageOption = spinnerAdapterBodyLanguageOption.getPosition( getPepperSpeech().getListenBodyLanguage().name() );
        spinLanguage.setSelection(nLanguage);
        spinRegion.setSelection(nRegion);
        spinBodyLanguage.setSelection(nBoyLanguageOption);

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

        // set options
        rootLayout.findViewById(R.id.btnListenOptionsSet).setOnClickListener(v -> {
            new Thread(() -> {
                String strLanguage = (String) spinLanguage.getSelectedItem();
                String strRegion = (String) spinRegion.getSelectedItem();
                String strBodyLanguageOption = (String) spinBodyLanguage.getSelectedItem();
                getPepperSpeech().setListenLanguage( Language.valueOf(strLanguage) );
                getPepperSpeech().setListenRegion( Region.valueOf(strRegion) );
                getPepperSpeech().setListenBodyLanguage( BodyLanguageOption.valueOf(strBodyLanguageOption) );
                //noinspection CodeBlock2Expr
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), R.string.msgListenOptionSet, Toast.LENGTH_LONG).show();
                });
                Log.i(TAG, "set listen options " + strLanguage + " : " + strRegion + " : " + strBodyLanguageOption);
            }).start();
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
            StringBuilder text = new StringBuilder();
            for(String phrase : phrases){
                Log.d(TAG, "adding phrase " + phrase + " to TextView");
                if(text.length() > 0){
                    text.append("\n");
                }
                text.append(phrase);
            }
            txtList.setText(text.toString());
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

        List<PhraseSet> phrases = new ArrayList<>();

        // extract comma separated list as list of PhraseSets
        Log.d(TAG, "building list of Phrases from String list.");
        for(String strPhrases : list){
            PhraseSet phraseSet = getPepperSpeech().createPhraseSet( strPhrases.split(",") );
            phrases.add(phraseSet);
        }

        // create and run listen
        Log.d(TAG, "create and run Listen");
        Listen listen = getPepperSpeech().listen(phrases);
        return listen.run();
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

    /**
     * Gets the {@link PepperSpeech} object, if not set the function will create one.
     * @return      {@link PepperSpeech} object.
     */
    private PepperSpeech getPepperSpeech(){
        if(this.pepperSpeech == null && pepperLibActivity != null){
            this.pepperSpeech = new PepperSpeech(pepperLibActivity.getPepperLib());
        }

        return this.pepperSpeech;
    }

}
