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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.object.conversation.BodyLanguageOption;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.locale.Language;
import com.aldebaran.qi.sdk.object.locale.Region;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.fhkiel.pepper.lib.PepperLibActivity;
import de.fhkiel.pepper.lib.modules.PepperSpeech;

public class FragmentSay extends Fragment {

    private static final String TAG = FragmentListen.class.getName();
    private PepperLibActivity pepperLibActivity;
    private View rootLayout;
    private PepperSpeech pepperSpeech;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        rootLayout = inflater.inflate(R.layout.fragment_say, container, false);

        // populate spinner locale options
        // get widgets
        Spinner spinLanguage = rootLayout.findViewById(R.id.spinSayLanguage);
        Spinner spinRegion = rootLayout.findViewById(R.id.spinSayRegion);
        Spinner spinBodyLanguage = rootLayout.findViewById(R.id.spinSayBodyLanguage);

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
        // say
        rootLayout.findViewById(R.id.btnSay).setOnClickListener(v -> {
            String sentence = ((EditText) rootLayout.findViewById(R.id.txtSaySentence)).getText()
                    .toString()
                    .trim();
            if(sentence.length() > 0){
                new Thread(() -> {
                    Log.i(TAG, "say sentence / word: " + sentence);
                    Say say = getPepperSpeech().say(sentence);
                    say.run();
                }).start();
            } else {
                Toast.makeText(getActivity(), R.string.msgSayNoSentenceFound, Toast.LENGTH_LONG).show();
            }
        });

        // set options
        rootLayout.findViewById(R.id.btnSayOptionSet).setOnClickListener(v -> {
            new Thread(() -> {
                String strLanguage = (String) spinLanguage.getSelectedItem();
                String strRegion = (String) spinRegion.getSelectedItem();
                String strBodyLanguageOption = (String) spinBodyLanguage.getSelectedItem();
                getPepperSpeech().setSayLanguage( Language.valueOf(strLanguage) );
                getPepperSpeech().setSayRegion( Region.valueOf(strRegion) );
                getPepperSpeech().setSayBodyLanguage( BodyLanguageOption.valueOf(strBodyLanguageOption) );
                //noinspection CodeBlock2Expr
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), R.string.msgSayOptionSet, Toast.LENGTH_LONG).show();
                });
                Log.i(TAG, "set say options " + strLanguage + " : " + strRegion + " : " + strBodyLanguageOption);
            }).start();
        });

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
