package de.fhkiel.pepper.cms_lib.users;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import de.fhkiel.pepper.cms_lib.JSONObjectable;

/**
 * User representation
 */
public class User implements JSONObjectable {

    private String salut = "";
    private String prename = "";
    private String lastname = "";
    private String nickname = "";
    private Date birthday = new Date(0);

    public User(){}

    public String getUsername(){
        return getPrename().replace(" ", "") + "." + getLastname().replace(" ", "");
    }

    public String getSalut() {
        return salut;
    }

    public String getPrename() {
        return prename;
    }

    public String getLastname() {
        return lastname;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setSalut(String salut) {
        this.salut = salut;
    }

    public void setPrename(String prename) {
        this.prename = prename;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
        try {
            json.put("salut", getSalut());
            json.put("prename", getPrename());
            json.put("lastname", getLastname());
            json.put("username", getUsername());
            json.put("nickname", getNickname());
            json.put("birthday", getBirthday().getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Creates user from json.
     * @param json  {@link JSONObject} to parse
     * @return      {@link User} parsed from {@link JSONObject}.
     */
    public static User fromJSONObject(JSONObject json){
        User user = new User();

        try {
            if (json.has("salut")) {
                user.setSalut(json.getString("salut"));
            }
            if (json.has("prename")){
                user.setPrename(json.getString("prename"));
            }
            if (json.has("lastname")){
                user.setLastname(json.getString("lastname"));
            }
            if (json.has("nickname")){
                user.setNickname(json.getString("nickname"));
            }
            if (json.has("birthday")){
                user.setBirthday( new Date(json.getLong("birthday")) );
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        return user;
    }

    public String getUserFilePathName(){
        return getPrename().replace(" ", "")
                + "_" + getLastname().replace(" ", "")
                + "_" + getNickname().replace(" ", "")
                + "_" + getBirthday().getTime();
    }
}
