package de.fhkiel.pepper.cms_core.repository;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class GithubAPI {

    private final String baseUrl = "https://api.github.com";
    private final String acceptHeader = "application/vnd.github.v3+json";
    private String repoUrl = "";
    private static final String TAG = GithubAPI.class.getName();

    public GithubAPI() {
        this.repoUrl = this.requestRepoUrl();
    }

    public URL getRespositoryURL(URL url) throws RuntimeException {
        String path = url.getPath();

        if(path != null && path.contains("/")) {
            path = path.replace(".git", "");
            String[] parts = path.split("/");

            if(parts.length == 3){
                String owner = parts[1];
                String repo = parts[2];
                try {
                    path = this.repoUrl;
                    path = path.replace("{owner}", owner).replace("{repo}", repo);
                    return new URL(path);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new RuntimeException("No valid repository url: " + path);
    }

    public ArrayList<GithubRelease> getReleases(URL repoURL) throws AuthFailedException {
        String releasesUrl = requestReleasesUrl(repoURL).replace("{/id}", "");
        try {
            ArrayList<GithubRelease> releases = new ArrayList<>();
            JSONArray array = getArray(new URL(releasesUrl));
            for (int i=0; i<array.length(); i++) {
                try {

                    JSONObject json = array.getJSONObject(i);
                    releases.add(GithubRelease.fromJson(json));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return releases;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String requestReleasesUrl(URL repositoryURL) throws AuthFailedException {
       JSONObject json = getObject(repositoryURL);
       if(json != null && json.has("releases_url")){
           try {
               return json.getString("releases_url");
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }
       return null;
    }

    private String requestRepoUrl(){
        String url = "";
        try {

            JSONObject json = getObject(new URL(this.baseUrl));
            if (json != null && json.has("repository_url")) {
                return json.getString("repository_url");
            }

        } catch (MalformedURLException | AuthFailedException | JSONException e){
            e.printStackTrace();
        }

        return url;
    }

    private JSONObject getObject(URL url) throws AuthFailedException{
        return (JSONObject) get(url, false);
    }

    private JSONArray getArray(URL url) throws AuthFailedException{
        return (JSONArray) get(url, true);
    }

    private Object get(URL url, boolean isArray) throws AuthFailedException {
        try {

            Log.d(TAG, "Reading from " + url);

            URLConnection conn = url.openConnection();
            conn.addRequestProperty("Accept", this.acceptHeader);
            conn.connect();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            String response = "";
            String line = "";
            while ((line = in.readLine()) != null){
                response += line;
            }
            in.close();

            if (isArray){
                return new JSONArray(response);
            }
            return new JSONObject(response);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public class AuthFailedException extends Exception{}

}
