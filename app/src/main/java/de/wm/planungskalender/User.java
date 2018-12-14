package de.wm.planungskalender;

import java.util.Map;

public class User {

    private int _userid;
    private String _name;
    private String _rights;
    private Map<String, String> _cookies;


    public User() {

    }


    public User(int userid, String name, String rights) {
        this._userid = userid;
        this._name = name;
        this._rights = rights;
    }

    public User(int userid, String name, String rights, Map<String, String> cookies) {
        this._userid = userid;
        this._name = name;
        this._rights = rights;
        this._cookies = cookies;
    }

    public int get_userid() {
        return this._userid;
    }

    public void set_userid(int userid) {
        this._userid = userid;
    }

    public String get_name() {
        return this._name;
    }

    public void set_name(String name) {
        this._name = name;
    }

    public String get_rights() {
        return this._rights;
    }

    public void set_rights(String rights) {
        this._rights = rights;
    }

    public Map<String, String> get_cookies() {
        return this._cookies;
    }

    public void set_cookies(Map<String, String> cookies) {
        this._cookies = cookies;
    }
}



