package de.fhkiel.pepper.cms_core.repository;

import android.util.Log;

import java.net.URL;
import java.util.ArrayList;

public class Repository {
    private URL githubRepositoryURL;
    private URL simpleRepositoryURL;

    private final static String TAG = Repository.class.getName();

    private URL getRepositoryURL(){
        if(githubRepositoryURL != null) return githubRepositoryURL;
        if(simpleRepositoryURL != null) return simpleRepositoryURL;
        return null;
    }

    private boolean isGitHubRepository(){
        if(githubRepositoryURL != null) return true;
        return false;
    }

    private boolean isSimpleRepository(){
        if(simpleRepositoryURL != null) return true;
        return false;
    }

    private String getLatestVersion(){
        if(isGitHubRepository()){

            try {

                GithubAPI github = new GithubAPI();
                URL repoUrl = github.getRespositoryURL(getRepositoryURL());
                ArrayList<GithubRelease> releases = github.getReleases(repoUrl);
                GithubRelease lastRelease = null;
                for(GithubRelease release : releases){
                    if(lastRelease == null) lastRelease = release;
                    if(release.getPublished().after(lastRelease.getPublished()))  lastRelease = release;
                    Log.d(TAG, release.toString());
                }

                return lastRelease.getTagName();

            } catch (GithubAPI.AuthFailedException e){
                e.printStackTrace();
            }

        } else if (isSimpleRepository()){

        }

        return null;
    }
}
