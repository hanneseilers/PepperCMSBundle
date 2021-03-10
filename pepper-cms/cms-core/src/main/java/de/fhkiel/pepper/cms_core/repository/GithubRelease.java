package de.fhkiel.pepper.cms_core.repository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GithubRelease {

    private int id;
    private String url;
    private String tagName;
    private boolean prelease;
    private String body = "";
    private Date published;
    private JSONArray assets;

    private static final String TAG = GithubRelease.class.getName();

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getTagName() {
        return tagName;
    }

    public boolean isPrelease() {
        return prelease;
    }

    public String getBody() {
        return body;
    }

    public Date getPublished() {
        return published;
    }

    public JSONArray getAssets() {
        return assets;
    }

    public String toString(){
        return this.getTagName() + (this.prelease ? " [prelease]" : "")  + ":\n"
                + this.getPublished() + "\n"
                + this.getBody() + "\n"
                + this.getUrl() + "\n";
    }

    public static GithubRelease fromJson(JSONObject json){
        GithubRelease repo = new GithubRelease();

        try {

            if (json.has("id")) repo.id = json.getInt("id");
            if (json.has("url")) repo.url = json.getString("url");
            if (json.has("tag_name")) repo.tagName = json.getString("tag_name");
            if (json.has("prerelease")) repo.prelease = json.getBoolean("prerelease");
            if (json.has("body")) repo.body = json.getString("body");
            if (json.has("assets")) repo.assets = json.getJSONArray("assets");
            if (json.has("published_at")){
                String dateString = json.getString("published_at");
                SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                repo.published = parser.parse(dateString);
            }

        } catch (JSONException | ParseException e){
            e.printStackTrace();
        }


        return repo;
    }

}
