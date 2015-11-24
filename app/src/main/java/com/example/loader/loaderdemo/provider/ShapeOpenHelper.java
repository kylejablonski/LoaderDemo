package com.example.loader.loaderdemo.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.loader.loaderdemo.provider.ShapeContentProvider;
import com.example.loader.loaderdemo.provider.ShapeContract;

/**
 * SQLiteOpenHelper for the {@link ShapeContract#TABLE_NAME} which stores of data
 * from the {@link  ShapeContentProvider} to back the data up in a db.
 * Created by kyle.jablonski on 11/23/15.
 */
public class ShapeOpenHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    private static final String DB_NAME = "demo.db";

    public ShapeOpenHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(ShapeContract.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ShapeContract.DROP_TABLE);
        onCreate(db);
    }
}
