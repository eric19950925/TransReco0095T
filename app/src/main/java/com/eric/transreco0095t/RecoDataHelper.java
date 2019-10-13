package com.eric.transreco0095t;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecoDataHelper extends SQLiteOpenHelper {
    public RecoDataHelper(Context context){
        this(context,"RecoData",null,1);
    }
    private RecoDataHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE RECODATA(" +
                "COL_id INTEGER PRIMARY KEY autoincrement,"+
                "COL_NAME VARCHAR NOT NULL,"+
                "COL_NAME2 VARCHAR NOT NULL,"+
                "COL_HOUR INTEGER NOT NULL,"+
                "COL_MIN INTEGER NOT NULL,"+
                "COL_YEAR INTEGER NOT NULL,"+
                "COL_MONTH INTEGER NOT NULL,"+
                "COL_DATE INTEGER NOT NULL,"+
                "COL_TEXT VARCHAR)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
