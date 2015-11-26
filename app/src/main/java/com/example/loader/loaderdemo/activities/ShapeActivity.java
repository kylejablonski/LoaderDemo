package com.example.loader.loaderdemo.activities;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.loader.loaderdemo.R;
import com.example.loader.loaderdemo.Utils;
import com.example.loader.loaderdemo.models.Shape;
import com.example.loader.loaderdemo.provider.ShapeContentProvider;
import com.example.loader.loaderdemo.provider.ShapeContract;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Activity which allows editing and existing shape, or creating a new shape
 * Created by kyle.jablonski on 11/23/15.
 */
public class ShapeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = ShapeActivity.class.getSimpleName();

    private static final int LOADER_ID = 2;

    private String [] colors ;

    @Bind(R.id.et_shape_name) AppCompatEditText mEtShapeName;
    @Bind(R.id.et_shape_num_sides) AppCompatEditText mEtShapeNumSides;
    @Bind(R.id.sp_shape_color) AppCompatSpinner mSpShapeColor;

    @Bind(R.id.btn_cancel) AppCompatButton mBtnCancel;
    @Bind(R.id.btn_save) AppCompatButton mBtnSave;
    @Bind(R.id.btn_delete) AppCompatButton mBtnDelete;

    // Shape we will eventually use to display or create new items
    private Shape mShape;

    // item id
    private int mItemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shape_detail);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent() != null){
            mItemId = getIntent().getIntExtra(ScrollingActivity.KEY_SHAPE_ID, -1);
        }

        // Check to make sure we got an item id, otherwise there is no need to initialize the loader
        if(mItemId != -1) {
            // Initialize the loader
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }

        mBtnCancel.setOnClickListener(CancelListener);
        mBtnSave.setOnClickListener(SaveListener);
        mBtnDelete.setOnClickListener(DeleteListener);
        colors = getResources().getStringArray(R.array.colors);
        mSpShapeColor.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, colors));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch(itemId){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        BackgroundShapeLoader loader = new BackgroundShapeLoader(ShapeActivity.this);
        return loader.loadInBackground();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            int id = data.getInt(data.getColumnIndexOrThrow(ShapeContract._ID));
            String name = data.getString(data.getColumnIndexOrThrow(ShapeContract.COL_NAME));
            int numSides = data.getInt(data.getColumnIndexOrThrow(ShapeContract.COL_NUM_SIDES));
            String color = data.getString(data.getColumnIndexOrThrow(ShapeContract.COL_COLOR));

            // Set the current shape
            mShape = new Shape(id, name, color, numSides);

            // Update the View
            mEtShapeName.setText(mShape.name);
            mEtShapeNumSides.setText(Integer.toString(mShape.num_sides));
            int position = lookupColor(mShape.color);
            mSpShapeColor.setSelection(position);

            mBtnDelete.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mShape = null;
    }

    /**
     * Compare the color found with the list of colors
     * @param color - the color to search for
     * @return - index of the color in the array
     */
    private int lookupColor(String color){
        Log.d(TAG, "lookupColor() called with: " + "color = [" + color + "]");
        int position = 0;

        for (int i = 0; i < colors.length; i++) {
            if(color.equalsIgnoreCase(colors[i])){
                position = i;
            }
        }
        return position;
    }

    /**
     * Saves the shape to the data store via the {@link ShapeContentProvider}
     * @param name - the shape name
     * @param color - the shape color
     * @param numSides - the number of sides
     * @return - true/false if the shape saved
     */
    private boolean saveShape(String name, String color, int numSides){
        Log.d(TAG, "saveShape() called with: " + "name = [" + name + "], color = [" + color + "], numSides = [" + numSides + "]");
        ContentValues values = new ContentValues();
        values.put(ShapeContract.COL_NAME, name);
        values.put(ShapeContract.COL_COLOR, color);
        values.put(ShapeContract.COL_NUM_SIDES, numSides);
        ContentResolver resolver = getContentResolver();
        Uri result = resolver.insert(ShapeContract.CONTENT_URI, values);

        return result != null;
    }

    /**
     * Deletes the current shape from the db through the content provider
     */
    private int deleteShape(){
        Log.d(TAG, "deleteShape() called with: " + "");
        int id = 0;
        if(mShape != null){

            Uri deleteUri = ContentUris.withAppendedId(ShapeContract.CONTENT_URI, mShape.id);
            ContentResolver contentResolver = getContentResolver();
            id = contentResolver.delete(deleteUri, null, null);
        }

        return id;
    }

    /**
     * Helper function to update the shape
     * @param shape - the current shape to update
     * @return - the row id of the updated shape
     */
    private int updateShape(Shape shape){
        Log.d(TAG, "updateShape() called with: " + "shape = [" + shape + "]");

        Uri updateUri = ContentUris.withAppendedId(ShapeContract.CONTENT_URI, shape.id);
        ContentResolver contentResolver = getContentResolver();

        ContentValues values = new ContentValues();
        values.put(ShapeContract.COL_NAME, shape.name);
        values.put(ShapeContract.COL_COLOR, shape.color);
        values.put(ShapeContract.COL_NUM_SIDES, shape.num_sides);

        return contentResolver.update(updateUri, values, null, null);
    }

    private final View.OnClickListener DeleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO: add some functionality here for removing items from db and content provider

            Utils.hideKeyboard(ShapeActivity.this, mEtShapeNumSides.getWindowToken());

            int id = deleteShape();

            if(id != -1){
                Snackbar.make(v, "Deleted "+ Integer.toString(id), Snackbar.LENGTH_SHORT).setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        finish();
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                        mBtnCancel.setEnabled(false);
                        mBtnSave.setEnabled(false);
                        mBtnDelete.setEnabled(false);
                    }
                }).show();
            }
        }
    };

    private final View.OnClickListener SaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            boolean clearToProceed = false;
            String shapeName = mEtShapeName.getText().toString();
            String color =  mSpShapeColor.getSelectedItem().toString();
            String num_sides = mEtShapeNumSides.getText().toString();

            if(TextUtils.isEmpty(shapeName)){
                Snackbar.make(v, "Please fill in a name", Snackbar.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(color)){
                Snackbar.make(v, "Please select a color", Snackbar.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(num_sides)){
                Snackbar.make(v, "Please select a color", Snackbar.LENGTH_SHORT).show();
            }else{
                clearToProceed = true;
            }

            if(clearToProceed){

                Utils.hideKeyboard(ShapeActivity.this, mEtShapeNumSides.getWindowToken());

                if(mShape == null) {
                    // save off the item in the provider
                    boolean savedShape = saveShape(shapeName, color, Integer.parseInt(num_sides));

                    if (savedShape) {
                        // todo: Add action, to remove shape ?
                        Snackbar.make(v, "Saved " + shapeName, Snackbar.LENGTH_SHORT).setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                finish();
                            }

                            @Override
                            public void onShown(Snackbar snackbar) {
                                mBtnCancel.setEnabled(false);
                                mBtnSave.setEnabled(false);
                            }
                        }).show();
                    }
                }else{

                    Shape updatedShape = new Shape(mItemId, shapeName, color, Integer.parseInt(num_sides));

                    int updatedId = updateShape(updatedShape);

                    Snackbar.make(v, "Updated shape at " + updatedId, Snackbar.LENGTH_SHORT).setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            super.onDismissed(snackbar, event);
                            finish();
                        }

                        @Override
                        public void onShown(Snackbar snackbar) {
                            mBtnCancel.setEnabled(false);
                            mBtnSave.setEnabled(false);
                            mBtnDelete.setEnabled(false);
                        }
                    }).show();
                }
            }
        }
    };

    private final View.OnClickListener CancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Utils.hideKeyboard(ShapeActivity.this, mEtShapeNumSides.getWindowToken());
            finish();
        }
    };


    /**
     * ShapeLoader for loading shapes off the UI
     */
    private class BackgroundShapeLoader extends AsyncTaskLoader<Loader<Cursor>>{

        public BackgroundShapeLoader(Context context){
            super(context);
        }

        @Override
        public Loader<Cursor> loadInBackground() {
            Uri loaderUri = ContentUris.withAppendedId(ShapeContract.CONTENT_URI, mItemId);
            return new CursorLoader(
                    ShapeActivity.this,
                    loaderUri,
                    ShapeContract.PROJECTION_ALL,
                    null,
                    null,
                    null);
        }
    }
}
