package com.example.loader.loaderdemo.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.loader.loaderdemo.BuildConfig;

/**
 * The ContentProvider for the shape application
 * Created by kyle.jablonski on 11/23/15.
 */
public class ShapeContentProvider extends ContentProvider {

    private static final String TAG = ShapeContentProvider.class.getSimpleName();

    public static final String AUTHORITY = "com.example.loader.loaderdemo.shape";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static final int SHAPE_LIST = 1;

    private static final int SHAPE_ID = 2;

    private static final UriMatcher URI_MATCHER;

    private ShapeOpenHelper mShapeHelper;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(ShapeContentProvider.AUTHORITY, "shapes", SHAPE_LIST);
        URI_MATCHER.addURI(ShapeContentProvider.AUTHORITY, "shapes/#", SHAPE_ID);
    }

    @Override
    public boolean onCreate() {
        mShapeHelper = new ShapeOpenHelper(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mShapeHelper.getWritableDatabase();
        int delCount = 0;
        switch (URI_MATCHER.match(uri)) {
            case SHAPE_LIST:
                delCount = db.delete(ShapeContract.TABLE_NAME, selection, selectionArgs);
                break;
            case SHAPE_ID:
                String idStr = uri.getLastPathSegment();
                String where = ShapeContract._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(ShapeContract.TABLE_NAME, where, selectionArgs);
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return delCount;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mShapeHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch (URI_MATCHER.match(uri)) {
            case SHAPE_LIST:
                builder.setTables(ShapeContract.TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = ShapeContract.SORT_ORDER_DEFAULT;
                }
                break;
            case SHAPE_ID:
                builder.setTables(ShapeContract.TABLE_NAME);
                // limit query to one row at most:
                builder.appendWhere(ShapeContract._ID + " = " + uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor cursor = builder.query(db, projection, selection, selectionArgs,
                null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        Log.d(TAG, "Listening for changes at "+ uri.toString());

        /* Log the query for debugging */
        logQuery(builder, projection, selection, sortOrder);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        switch (URI_MATCHER.match(uri)) {

            case SHAPE_LIST:
                return ShapeContract.CONTENT_TYPE;

            case SHAPE_ID:
                return ShapeContract.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        if (URI_MATCHER.match(uri) != SHAPE_LIST) {
            throw new IllegalArgumentException(
                    "Unsupported URI for insertion: " + uri);
        }

        SQLiteDatabase db = mShapeHelper.getWritableDatabase();
        if (URI_MATCHER.match(uri) == SHAPE_LIST) {
            long id = db.insert(ShapeContract.TABLE_NAME, null, values);
            return getUriForId(id, uri);
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mShapeHelper.getWritableDatabase();
        int updateCount = 0;
        switch (URI_MATCHER.match(uri)) {
            case SHAPE_LIST:
                updateCount = db.update(ShapeContract.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case SHAPE_ID:
                String idStr = uri.getLastPathSegment();
                String where = ShapeContract._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(ShapeContract.TABLE_NAME, values, where, selectionArgs);
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return updateCount;
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);

            // notify all listeners of changes and return itemUri:
            getContext(). getContentResolver().notifyChange(itemUri, null);

            return itemUri;
        }
        return null;
    }

    private void logQuery(SQLiteQueryBuilder builder, String[] projection, String selection, String sortOrder) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "query: " + builder.buildQuery(projection, selection, null, null, sortOrder, null));
        }
    }
}
