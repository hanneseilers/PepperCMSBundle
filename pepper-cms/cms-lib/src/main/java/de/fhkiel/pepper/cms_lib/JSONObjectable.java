package de.fhkiel.pepper.cms_lib;

import org.json.JSONObject;

/**
 * Interface of objects that can be transfered into {@link JSONObject}
 */
public interface JSONObjectable {

    /**
     * Function to receive {@link JSONObject} json representation of object.
     * @return JSONObject
     */
    JSONObject toJSONObject();
}
