package de.wm.planungskalender;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class DBHandlerBackup extends SQLiteOpenHelper {

    private static final String COLUMN_AUTOID = "KEY_ID";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EVENTNAME = "eventname";
    private static final String COLUMN_USERS = "users";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_CREATOR = "creator";
    private static final String COLUMN_SIGNEDIN = "signedIn";
    private static final String COLUMN_SIGNEDOUT = "signedOut";
    private static final String COLUMN_INFO = "info";
    private static final String COLUMN_ISSIGNEDIN = "isSignedIn";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "eventsBackupDB.db";
    private static final String TABLE_EVENTS = "events";
    /*
      private int id;
        private String _eventname;
        private int _users;
    private String _date;
    private String _time;
    private String _creator;
    private List<String> _signedIn;
    private List<String> _signedOut;
    private String _info;
    private String _isSignedIn;

    10 VALUES

     */

    public DBHandlerBackup(Context context, String name,
                           SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_EVENTS + "(" + COLUMN_ID + " INTEGER ," + COLUMN_EVENTNAME
                + " TEXT," + COLUMN_USERS + " INTEGER," + COLUMN_DATE + " TEXT," + COLUMN_TIME + " TEXT," + COLUMN_CREATOR + " TEXT," + COLUMN_SIGNEDIN + " TEXT," + COLUMN_SIGNEDOUT + " TEXT," + COLUMN_INFO + " TEXT," + COLUMN_ISSIGNEDIN + " TEXT," + COLUMN_AUTOID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);

    }

    public void fillDB(ArrayList<Event> arrayList){
        for (int i = 0; i < arrayList.size(); i++) {
            addEvent(arrayList.get(i));
        }
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion,
                            int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    public void addEvent(Event event) {

        ContentValues values = new ContentValues(9);

        values.put(COLUMN_ID, event.get_id());                                                                  //0
        values.put(COLUMN_EVENTNAME, event.get_eventname());                                                    //1
        values.put(COLUMN_USERS, event.get_users());                                                            //2
        values.put(COLUMN_DATE, event.get_date());                                                              //3
        values.put(COLUMN_TIME, event.get_time());                                                              //4
        values.put(COLUMN_CREATOR, event.get_creator());                                                        //5

        String sIn = Arrays.toString(event.get_signedIn().toArray());
        values.put(COLUMN_SIGNEDIN, sIn.replace("[", "").replace("]", ""));       //6

        String sOut = Arrays.toString(event.get_signedOut().toArray());
        values.put(COLUMN_SIGNEDOUT, sOut.replace("[", "").replace("]", ""));      //7
        values.put(COLUMN_INFO, event.get_info());                                                               //8
        values.put(COLUMN_ISSIGNEDIN, event.get_isSignedIn());                                                               //8


        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_EVENTS, null, values);
        db.close();
    }

    public Event findEvent(String eventname) {
        String query = "Select * FROM " + TABLE_EVENTS + " WHERE " + COLUMN_EVENTNAME + " =  \"" + eventname + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Event event = new Event();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            event.set_id(Integer.parseInt(cursor.getString(0)));
            event.set_eventname(cursor.getString(1));
            event.set_users(Integer.parseInt(cursor.getString(2)));
            event.set_date(cursor.getString(3));
            event.set_time(cursor.getString(4));
            event.set_creator(cursor.getString(5));

            ArrayList<String> signedInAList = new ArrayList<>(Arrays.asList(cursor.getString(6).split(",")));
            event.set_signedIn(signedInAList);

            ArrayList<String> signedOutAList = new ArrayList<>(Arrays.asList(cursor.getString(7).split(",")));
            event.set_signedOut(signedOutAList);

            event.set_info(cursor.getString(8));
            event.set_isSignedIn(cursor.getString(9));

            cursor.close();
        } else {
            event = null;
        }
        db.close();
        return event;
    }

    public Event findEventById(int autoid) {
        String query = "Select * FROM " + TABLE_EVENTS + " WHERE " + COLUMN_AUTOID + " =  " + autoid;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Event event = new Event();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            event.set_id(Integer.parseInt(cursor.getString(0)));
            event.set_eventname(cursor.getString(1));
            event.set_users(Integer.parseInt(cursor.getString(2)));
            event.set_date(cursor.getString(3));
            event.set_time(cursor.getString(4));
            event.set_creator(cursor.getString(5));

            ArrayList<String> signedInAList = new ArrayList<>(Arrays.asList(cursor.getString(6).split(",")));
            event.set_signedIn(signedInAList);

            ArrayList<String> signedOutAList = new ArrayList<>(Arrays.asList(cursor.getString(7).split(",")));
            event.set_signedOut(signedOutAList);

            event.set_info(cursor.getString(8));
            event.set_isSignedIn(cursor.getString(9));

            cursor.close();
        } else {
            cursor.close();
            event = null;
        }
        db.close();
        return event;
    }

    public Event findEventByRealId(int id) {
        String query = "Select * FROM " + TABLE_EVENTS + " WHERE " + COLUMN_ID + " =  \"" + id + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Event event = new Event();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            event.set_id(Integer.parseInt(cursor.getString(0)));
            event.set_eventname(cursor.getString(1));
            event.set_users(Integer.parseInt(cursor.getString(2)));
            event.set_date(cursor.getString(3));
            event.set_time(cursor.getString(4));
            event.set_creator(cursor.getString(5));

            ArrayList<String> signedInAList = new ArrayList<>(Arrays.asList(cursor.getString(6).split(",")));
            event.set_signedIn(signedInAList);

            ArrayList<String> signedOutAList = new ArrayList<>(Arrays.asList(cursor.getString(7).split(",")));
            event.set_signedOut(signedOutAList);

            event.set_info(cursor.getString(8));
            event.set_isSignedIn(cursor.getString(9));

            cursor.close();
        } else {
            event = null;
        }
        db.close();
        return event;
    }

    public void setSignedIn(String Eventname, String signedIn){


        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE "+ TABLE_EVENTS +
                " SET "+COLUMN_ISSIGNEDIN + " = '"+signedIn + "'"+
                " WHERE "+COLUMN_EVENTNAME+" = '"+Eventname+"';");

        db.close();
    }

    public boolean deleteEvent(String eventname) {

        boolean result = false;

        String query = "Select * FROM " + TABLE_EVENTS + " WHERE " + COLUMN_EVENTNAME + " =  \"" + eventname + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Event event = new Event();

        if (cursor.moveToFirst()) {
            event.set_id(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_EVENTS, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(event.get_id())});
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public void deleteEventByAutoId(int autoid) {


        SQLiteDatabase db = this.getWritableDatabase();

        String query = "DELETE FROM " + TABLE_EVENTS + " WHERE " + COLUMN_AUTOID + " = " + autoid;
        db.rawQuery(query, null);


        db.close();
    }


    public void deleteDatabase() {

        SQLiteDatabase db = this.getWritableDatabase();
        if (db.getVersion() >= 2) {
            onDowngrade(db, db.getVersion(), 1);
        } else {
            onUpgrade(db, db.getVersion(), 2);
        }

    }

}