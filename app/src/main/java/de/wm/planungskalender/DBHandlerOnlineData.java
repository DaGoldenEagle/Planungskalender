package de.wm.planungskalender;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

public class DBHandlerOnlineData extends SQLiteOpenHelper {

    public static final String COLUMN_AUTOID = "KEY_ID";
    public static final String COLUMN_USERID = "userId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_RIGHTS = "rights"; //Admin/Mod/User
    public static final String COLUMN_COOKIES = "cookies";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "online.db";
    private static final String TABLE_DATA = "data";


    public DBHandlerOnlineData(Context context, String name,
                               SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ONLINE_TABLE = "CREATE TABLE " +
                TABLE_DATA + "(" + COLUMN_USERID + " INTEGER ," + COLUMN_NAME
                + " TEXT," + COLUMN_RIGHTS + " TEXT," + COLUMN_COOKIES + " TEXT," + COLUMN_AUTOID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ")";
        db.execSQL(CREATE_ONLINE_TABLE);

    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        onCreate(db);

    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion,
                            int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        onCreate(db);

    }

    public void addUser(User user) {

        ContentValues values = new ContentValues(9);

        values.put(COLUMN_USERID, user.get_userid());                                               //0
        values.put(COLUMN_NAME, user.get_name());                                                   //1
        values.put(COLUMN_RIGHTS, user.get_rights());                                               //2
        values.put(COLUMN_COOKIES, user.get_cookies().toString());                              //3

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_DATA, null, values);
        db.close();
    }

    public User getUser() {
        String query = "Select * FROM " + TABLE_DATA;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        User user = new User();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            user.set_userid(Integer.parseInt(cursor.getString(0)));
            user.set_name(cursor.getString(1));
            user.set_rights(cursor.getString(2));

            Map<String, String> cookies = StringToMap(cursor.getString(3));
            user.set_cookies(cookies);

            cursor.close();
        } else {
            user = null;
        }
        db.close();
        return user;
    }


    public void deleteUser() {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE * FROM " + TABLE_DATA;
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
        db.close();

    }

    private Map<String, String> StringToMap(String string) {
        Map<String, String> map = new HashMap<>();
        map.put("PHPSESSID", string.replace("{PHPSESSID=", "").replace(",", "").replace("}", ""));
        return map;


    }

}