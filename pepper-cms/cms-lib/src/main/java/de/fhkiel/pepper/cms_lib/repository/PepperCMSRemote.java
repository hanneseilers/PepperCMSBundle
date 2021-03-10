package de.fhkiel.pepper.cms_lib.repository;

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

public class PepperCMSRemote {

    private static final String TAG = PepperCMSRemote.class.getName();
    private static final String acceptHeader = "\"application/vnd.github.v3+json\"";

    public static JSONObject getObject(URL url) throws AuthFailedException{
        return (JSONObject) get(url, false);
    }

    public static JSONArray getArray(URL url) throws AuthFailedException{
        return (JSONArray) get(url, true);
    }

    public static String getString(URL url) throws  AuthFailedException{
        try {

            Log.d(TAG, "\t> reading from " + url);

            // TODO: Authenificiation only via Https

            URLConnection conn = url.openConnection();
            conn.addRequestProperty("Accept", acceptHeader);
            conn.connect();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line = "";
            while ((line = in.readLine()) != null){
                response.append(line);
            }
            in.close();
            return response.toString();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    private static Object get(URL url, boolean isArray) throws AuthFailedException {
        try {

            String response = getString(url);

            if( response != null ) {
                if (isArray) {
                    return new JSONArray(response);
                }
                return new JSONObject(response);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    /**
     * Converts a string to an {@link URL} object.
     * @param urlString     String of URL
     * @return              {@link URL} object
     * @throws MalformedURLException if url is not valid
     */
    public static URL toUrl(String urlString) throws MalformedURLException {
        return new URL(urlString);
    }

    public static class AuthFailedException extends Exception{}

}
