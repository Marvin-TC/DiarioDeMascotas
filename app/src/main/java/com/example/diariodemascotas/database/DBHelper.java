package com.example.diariodemascotas.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.diariodemascotas.utils.PasswordUtils;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "loging_demo.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "nombres TEXT UNIQUE NOT NULL,"+
                        "apellidos TEXT NOT NULL,"+
                        "user_name TEXT NOT NULL,"+
                        "pasword_hash TEXT NOT NULL,"+
                        "salt TEXT NOT NULL,"+
                        "created_at TEXT NOT NULL"+
                        ")"
        );

        db.execSQL("CREATE UNIQUE INDEX idx_users_username ON users (user_name)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public long insertUser(
            String nombres,
            String apellidos,
            String user_name,
            char[] pasword_hash) throws Exception{
        byte[] salt = PasswordUtils.generarSalt();
        byte[] hash = PasswordUtils.hash(pasword_hash, salt);

        ContentValues values = new ContentValues();
        values.put("nombres", nombres);
        values.put("apellidos", apellidos);
        values.put("user_name",user_name);
        values.put("pasword_hash", PasswordUtils.toBase64(hash));
        values.put("salt", PasswordUtils.toBase64(salt));
        values.put("created_at", System.currentTimeMillis());

        SQLiteDatabase db = getWritableDatabase();
        return db.insert("users", null, values);
    }

    public boolean userExiste(String user){
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.rawQuery(
                "SELECT 1 FROM users WHERE user_name = ? LIMIT 1",
                new String[]{user.trim()}
        )){
            return c.moveToFirst();
        }
    }

    public boolean checkLogin(String user_name, char[] password) throws Exception{
        SQLiteDatabase db = getReadableDatabase();
        try(Cursor c = db.rawQuery(
                "SELECT pasword_hash, salt FROM users WHERE user_name = ? LIMIT 1",
                new String[]{user_name.trim()})){
            if (!c.moveToFirst()) return false;
            byte[] hash = PasswordUtils.fromoBase64(c.getString(0));
            byte[] salt = PasswordUtils.fromoBase64(c.getString(1));
            return PasswordUtils.verify(password, salt, hash);
        }
    }
}
