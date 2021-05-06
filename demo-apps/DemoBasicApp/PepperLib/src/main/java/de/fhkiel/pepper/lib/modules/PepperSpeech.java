package de.fhkiel.pepper.lib.modules;

import com.aldebaran.qi.sdk.builder.ListenBuilder;
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder;
import com.aldebaran.qi.sdk.object.conversation.BodyLanguageOption;
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.locale.Language;
import com.aldebaran.qi.sdk.object.locale.Locale;
import com.aldebaran.qi.sdk.object.locale.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fhkiel.pepper.lib.PepperLib;

/**
 * Class for robot conversation (speaking and listening)
 */
public class PepperSpeech extends PepperLibModule {

    // Listen options and their defaults
    private BodyLanguageOption listenBodyLanguage = BodyLanguageOption.NEUTRAL;
    private Language listenLanguage = Language.ENGLISH;
    private Region listenRegion = Region.UNITED_KINGDOM;

    /**
     * Constructor
     * @param pepperLib     Reference to {@link PepperLib} object.
     */
    public PepperSpeech(PepperLib pepperLib) {
        super(pepperLib);
    }

    /**
     * Build a set of phrases to make the robot listen to.
     * @param phrases   {@link List} of {@link String}s that belong to one {@link PhraseSet} (context).
     * @return          {@link PhraseSet}, or null, if failed.
     */
    public PhraseSet createPhraseSet(List<String> phrases){
        if(pepperLib.hasQiContext()) {
            ArrayList<Phrase> phraseArrayList = new ArrayList<>();
            for(String text : phrases){
                phraseArrayList.add( new Phrase(text) );
            }
            return PhraseSetBuilder.with( pepperLib.getQiContext() )
                    .withPhrases(phraseArrayList).build();
        }
        return null;
    }

    /**
     * Build a set of phrases to make the robot listen to.
     * @param phrases   Array of {@link String}s that belong to one {@link PhraseSet} (context).
     * @return          {@link PhraseSet}, or null, if failed.
     */
    public PhraseSet createPhraseSet(String[] phrases){
        return createPhraseSet( new ArrayList<>(Arrays.asList(phrases)) );
    }

    /**
     * Build a set of phrases to make the robot listen to.
     * @param texts     Unlimited number of {@link String} arguments that belong to one {@link PhraseSet} (context).
     * @return          {@link PhraseSet}, or null, if failed.
     */
    public PhraseSet creatPhraseSet(String... texts){
        List<String> list = new ArrayList<>();
        for(String text : texts){
            list.add(text);
        }
        return createPhraseSet(list);
    }

    /**
     * Creates an object of interface {@link Listen} to let the robot listen to some {@link PhraseSet}s
     * Uses the set body language option, as well as language and region settings.
     * @param phrases   {@link List} of {@link PhraseSet}s the robot should listen to.
     * @return          Object of {@link Listen} interface, or null if failed.
     */
    public Listen listen(List<PhraseSet> phrases){
        if(pepperLib.hasQiContext()) {
            return ListenBuilder.with(pepperLib.getQiContext())
                    .withPhraseSets(phrases)
                    .withBodyLanguageOption(listenBodyLanguage)
                    .withLocale(new Locale(listenLanguage, listenRegion))
                    .build();
        }
        return null;
    }
}
