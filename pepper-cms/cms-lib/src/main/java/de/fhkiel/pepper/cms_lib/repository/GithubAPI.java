package de.fhkiel.pepper.cms_lib.repository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GithubAPI {

    @SuppressWarnings("FieldCanBeLocal")
    private final String baseUrl = "https://api.github.com";
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

    public ArrayList<GithubRelease> getReleases(URL repoURL) throws PepperCMSRemote.AuthFailedException {
        String releasesUrl = requestReleasesUrl(repoURL).replace("{/id}", "");
        try {
            ArrayList<GithubRelease> releases = new ArrayList<>();
            JSONArray array = PepperCMSRemote.getArray(new URL(releasesUrl));
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

    private String requestReleasesUrl(URL repositoryURL) throws PepperCMSRemote.AuthFailedException {
       JSONObject json = PepperCMSRemote.getObject(repositoryURL);
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

            JSONObject json = PepperCMSRemote.getObject(new URL(this.baseUrl));
            if (json != null && json.has("repository_url")) {
                return json.getString("repository_url");
            }

        } catch (MalformedURLException | PepperCMSRemote.AuthFailedException | JSONException e){
            e.printStackTrace();
        }

        return url;
    }



}
