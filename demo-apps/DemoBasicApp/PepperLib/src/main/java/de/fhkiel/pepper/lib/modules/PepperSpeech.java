package de.fhkiel.pepper.lib.modules;

import com.aldebaran.qi.sdk.builder.ListenBuilder;
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.BodyLanguageOption;
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.conversation.Say;
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
@SuppressWarnings("unused")
public class PepperSpeech extends PepperLibModule {

    // Listen options and their defaults
    private BodyLanguageOption listenBodyLanguage = BodyLanguageOption.NEUTRAL;
    private Language listenLanguage = Language.ENGLISH;
    private Region listenRegion = Region.UNITED_STATES;

    // Say options and their defaults
    private BodyLanguageOption sayBodyLanguage = BodyLanguageOption.NEUTRAL;
    private Language sayLanguage = Language.ENGLISH;
    private Region sayRegion = Region.UNITED_STATES;

    /**
     * Constructor
     * @param pepperLib     Reference to {@link PepperLib} object.
     */
    public PepperSpeech(PepperLib pepperLib) {
        super(pepperLib);
    }

    public BodyLanguageOption getListenBodyLanguage() {
        return listenBodyLanguage;
    }

    public void setListenBodyLanguage(BodyLanguageOption listenBodyLanguage) {
        this.listenBodyLanguage = listenBodyLanguage;
    }

    public Language getListenLanguage() {
        return listenLanguage;
    }

    public void setListenLanguage(Language listenLanguage) {
        this.listenLanguage = listenLanguage;
    }

    public Region getListenRegion() {
        return listenRegion;
    }

    public void setListenRegion(Region listenRegion) {
        this.listenRegion = listenRegion;
    }

    public BodyLanguageOption getSayBodyLanguage() {
        return sayBodyLanguage;
    }

    public void setSayBodyLanguage(BodyLanguageOption sayBodyLanguage) {
        this.sayBodyLanguage = sayBodyLanguage;
    }

    public Language getSayLanguage() {
        return sayLanguage;
    }

    public void setSayLanguage(Language sayLanguage) {
        this.sayLanguage = sayLanguage;
    }

    public Region getSayRegion() {
        return sayRegion;
    }

    public void setSayRegion(Region sayRegion) {
        this.sayRegion = sayRegion;
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

        errorNoQiContext();
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

        errorNoQiContext();
        return null;
    }

    /**
     * Creates an object of interface {@link Listen} to let the robot listen to some {@link PhraseSet}s
     * Uses the set body language option, as well as language and region settings.
     * @param phrases   Array of {@link PhraseSet}s the robot should listen to.
     * @return          Object of {@link Listen} interface, or null if failed.
     */
    public Listen listen(PhraseSet[] phrases){
        return listen( new ArrayList<>(Arrays.asList(phrases)) );
    }

    /**
     * Let the robot say a text using the set options.
     * @param phrase    {@link Phrase} to say.
     * @return          Object of {@link Say} interface.
     */
    public Say say(Phrase phrase){
        if(pepperLib.hasQiContext()){
            return SayBuilder.with(pepperLib.getQiContext())
                    .withPhrase(phrase)
                    .withBodyLanguageOption(sayBodyLanguage)
                    .withLocale( new Locale(sayLanguage, sayRegion) )
                    .build();
        }
        return null;
    }

    public Say say(String text){
        return say(new Phrase(text));
    }

    //TODO: Chatbot
}
