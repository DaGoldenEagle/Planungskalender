package de.wm.planungskalender;

import java.util.ArrayList;

public class Event {

    private int _id;
    private String _eventname;
    private int _users;
    private String _date;
    private String _time;
    private String _creator;
    private ArrayList<String> _signedIn;
    private ArrayList<String> _signedOut;
    private String _info;
    private String _isSignedIn;


    public Event() {

    }


    public Event(int id, String eventname, int users, String date, String time, String creator, ArrayList<String> signedIn, ArrayList<String> signedOut, String info, String isSignedIn) {
        this._id = id;
        this._eventname = eventname;
        this._users = users;
        this._date = date;
        this._time = time;
        this._creator = creator;
        this._signedIn = signedIn;
        this._signedOut = signedOut;
        this._info = info;
        this._isSignedIn = isSignedIn;
    }

    public int get_id() {
        return this._id;
    }

    public void set_id(int id) {
        this._id = id;
    }

    public String get_eventname() {
        return this._eventname;
    }

    public void set_eventname(String eventname) {
        this._eventname = eventname;
    }

    public int get_users() {
        return this._users;
    }

    public void set_users(int users) {
        this._users = users;
    }

    public String get_date() {
        return this._date;
    }

    public void set_date(String date) {
        this._date = date;
    }

    public String get_time() {
        return this._time;
    }

    public void set_time(String time) {
        this._time = time;
    }

    public String get_creator() {
        return this._creator;
    }

    public void set_creator(String creator) {
        this._creator = creator;
    }

    public ArrayList<String> get_signedIn() {
        return this._signedIn;
    }

    public void set_signedIn(ArrayList<String> signedIn) {
        this._signedIn = signedIn;
    }

    public ArrayList<String> get_signedOut() {
        return this._signedOut;
    }

    public void set_signedOut(ArrayList<String> signedOut) {
        this._signedOut = signedOut;
    }

    public String get_info() {
        return this._info;
    }

    public void set_info(String info) {
        this._info = info;
    }

    public String get_isSignedIn() {
        return this._isSignedIn;
    }

    public void set_isSignedIn(String isSignedIn) {
        this._isSignedIn = isSignedIn;
    }
}


