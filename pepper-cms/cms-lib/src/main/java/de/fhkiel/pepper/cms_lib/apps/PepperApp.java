package de.fhkiel.pepper.cms_lib.apps;

import android.util.Log;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.fhkiel.pepper.cms_lib.JSONObjectable;

/**
 * Class to store informations about different apps, available on Pepper robot.
 */
public class PepperApp implements JSONObjectable {

    private static final String TAG = PepperApp.class.getName();

    private String name;
    private String description;
    private String intentPackage;
    private String intentClass;
    private Integer currentVersion = 0;
    private Integer latestVersion = 0;
    private String tags = "";
    private String categoriesString = "";
    private String downloadURL = "";

    private static final String TAG_NAME = "name";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_INTENT_PACKAGE = "intentPackage";
    private static final String TAG_INTENT_CLASS = "intentClass";
    private static final String TAG_CURRENT_VERSION = "currentVersion";
    private static final String TAG_LATEST_VERSION = "latestVersion";
    private static final String TAG_TAGS = "tags";
    private static final String TAG_CATEGORIES_STRING = "categoriesString";
    private static final String TAG_DOWNLOAD_URL = "downloadURL";

    public PepperApp(String name){
        setName(name);
    }

    @SuppressWarnings(value = "unsued")
    public String getName() {
        return name;
    }

    @SuppressWarnings(value = "unsued")
    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings(value = "unsued")
    public String getDescription() {
        return description;
    }

    @SuppressWarnings(value = "unsued")
    public void setDescription(String description) {
        this.description = description;
    }

    @SuppressWarnings(value = "unsued")
    public Integer getCurrentVersion() {
        return currentVersion;
    }

    @SuppressWarnings(value = "unsued")
    public void setCurrentVersion(Integer currentVersion) {
        this.currentVersion = currentVersion;
    }

    @SuppressWarnings(value = "unsued")
    public Integer getLatestVersion() {
        return latestVersion;
    }

    @SuppressWarnings(value = "unsued")
    public void setLatestVersion(Integer latestVersion) {
        this.latestVersion = latestVersion;
    }

    @SuppressWarnings(value = "unsued")
    public String getIntentPackage() {
        return intentPackage;
    }

    @SuppressWarnings(value = "unsued")
    public void setIntentPackage(String intentPackage) {
        this.intentPackage = intentPackage;
    }

    @SuppressWarnings(value = "unsued")
    public String getIntentClass() {
        return intentClass;
    }

    @SuppressWarnings(value = "unsued")
    public void setIntentClass(String intentClass) {
        this.intentClass = intentClass;
    }

    @SuppressWarnings(value = "unsued")
    public String getTags() {
        return tags.trim();
    }

    @SuppressWarnings(value = "unsued")
    public void setTags(String tags) {
        this.tags = tags.trim();
    }

    @SuppressWarnings(value = "unsued")
    public String getCategoriesString() {
        return categoriesString;
    }

    @SuppressWarnings(value = "unsued")
    public ArrayList<String> getCategories(){
        ArrayList<String> categories = new ArrayList<>();
        for(String s : getCategoriesString().split("\\.") ){
            categories.add(s);
        }
        return categories;
    }

    @SuppressWarnings(value = "unsued")
    public void setCategoriesString(String categoriesString) {
        this.categoriesString = categoriesString;
    }

    @SuppressWarnings(value = "unsued")
    public String getDownloadURL() {
        return downloadURL;
    }

    @SuppressWarnings(value = "unsued")
    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public String getIdentifier(){
        return getIntentPackage() + "/" + getIntentClass();
    }

    public String getHashCode(){
        return new String(Hex.encodeHex(DigestUtils.sha1(getIdentifier())) );
    }

    public String toString(){
        return toJSONObject().toString();
    }

    /**
     * @return JSONObject
     */
    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        try{
            json.put(TAG_NAME, getName());
            json.put(TAG_INTENT_PACKAGE, getIntentPackage());
            json.put(TAG_INTENT_CLASS, getIntentClass());
            json.put(TAG_CURRENT_VERSION, getCurrentVersion());
            json.put(TAG_LATEST_VERSION, getLatestVersion());
            json.put(TAG_TAGS, getTags());
            json.put(TAG_DOWNLOAD_URL, getDownloadURL());
        } catch(JSONException e){
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Converts {@link JSONObject} to {@link PepperApp}
     * @param jsonObject {@link JSONObject} to parse
     * @return           {@link PepperApp}, null if json is not valid
     */
    public static PepperApp fromJson(JSONObject jsonObject){
        Log.d(TAG, "processing json to PepperApp..");
        try {
            if (jsonObject.has(TAG_NAME)
                    && jsonObject.has(TAG_INTENT_PACKAGE)
                    && jsonObject.has(TAG_INTENT_CLASS)
                    && jsonObject.has(TAG_LATEST_VERSION)) {

                Log.d(TAG, "\t> processing basic information");
                PepperApp app = new PepperApp(jsonObject.getString(TAG_NAME));
                app.setIntentPackage(jsonObject.getString(TAG_INTENT_PACKAGE));
                app.setIntentClass(jsonObject.getString(TAG_INTENT_CLASS));
                app.setLatestVersion(jsonObject.getInt(TAG_LATEST_VERSION));

                Log.d(TAG, "\t> processing additional informations");
                if( jsonObject.has(TAG_TAGS) ) app.setTags(jsonObject.getString(TAG_TAGS));
                if( jsonObject.has(TAG_CATEGORIES_STRING) ) app.setCategoriesString(jsonObject.getString(TAG_CATEGORIES_STRING));
                if( jsonObject.has(TAG_DOWNLOAD_URL) ) app.setDownloadURL(jsonObject.getString(TAG_DOWNLOAD_URL));
                if( jsonObject.has(TAG_CURRENT_VERSION )) app.setCurrentVersion(jsonObject.getInt(TAG_CURRENT_VERSION));

                return app;
            }
        } catch (JSONException e){
            e.printStackTrace();
            Log.w(TAG, "Cannot parse json object of app:\n" + jsonObject.toString());
        }
        return null;
    }
}
