package com.example.loader.loaderdemo.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class for a {@link com.example.loader.loaderdemo.models.Shape}
 * Created by kyle.jablonski on 11/23/15.
 */
public final class ShapeContract implements BaseColumns{


    /**
     * Content Uri for the shapes
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(ShapeContentProvider.BASE_CONTENT_URI, "shapes");

    /**
     * Content type for a shape list
     */
    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE +
                    "/com.example.loader.loaderdemo_shapes";

    /**
     * Content type for a single shape
     */
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                    "/com.example.loader.loaderdemo_shapes";

    public static final String[] PROJECTION_ALL = {ShapeContract._ID, ShapeContract.COL_NAME,
            ShapeContract.COL_COLOR, ShapeContract.COL_NUM_SIDES};

    public static final String TABLE_NAME = "shapes";

    public static final String COL_NAME = "name";

    public static final String COL_COLOR = "color";

    public static final String COL_NUM_SIDES = "num_sides";

    public static final String CREATE_TABLE = "CREATE TABLE "+ TABLE_NAME + "("
                                                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                + COL_NAME + " TEXT NOT NULL, "
                                                + COL_COLOR + " TEXT NOT NULL, "
                                                + COL_NUM_SIDES + " INT DEFAULT 0" + ");";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS "+ TABLE_NAME;


    public static final String SORT_ORDER_DEFAULT = COL_NAME + " DESC";

}
