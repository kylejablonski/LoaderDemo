package com.example.loader.loaderdemo.activities;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.loader.loaderdemo.R;
import com.example.loader.loaderdemo.adapters.ShapeAdapter;
import com.example.loader.loaderdemo.adapters.decoration.UnderlineDecoration;
import com.example.loader.loaderdemo.interfaces.ShapeClicked;
import com.example.loader.loaderdemo.models.Shape;
import com.example.loader.loaderdemo.provider.ShapeContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Activity which displays a list of {@link Shape} and allows editing or adding more shapes.
 * created by kyle.jablonski on 11/23/15
 */
public class ScrollingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, ShapeClicked {

    private static final String TAG = ScrollingActivity.class.getSimpleName();

    private static final int SHAPE_LIST_LOADER = 3;

    public static final String KEY_SHAPE_ID = "shape_id";

    private ShapeAdapter mAdapter;
    private List<Shape> mShapes;

    @Bind(R.id.rec_shapes) RecyclerView mRecShapes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        ButterKnife.bind(this);

        getLoaderManager().initLoader(SHAPE_LIST_LOADER, null, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(AddShapeListener);

        initDefaults();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        ShapeListLoader loader = new ShapeListLoader(ScrollingActivity.this);
        return loader.loadInBackground();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        displayData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        Log.d(TAG, "loader was rest...");
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Init
     */
    public void initDefaults(){

        Log.d(TAG, "initialized defaults...");

        mShapes = new ArrayList<>();
        mAdapter = new ShapeAdapter(this, mShapes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecShapes.addItemDecoration(new UnderlineDecoration(this));
        mRecShapes.setLayoutManager(layoutManager);
        mRecShapes.setAdapter(mAdapter);

    }

    /**
     * Displays the data from the cursor
     * @param data - the cursor to use
     */
    public void displayData(Cursor data){

        Log.d(TAG, "loading data...");

        mShapes.clear();
        if(data != null) {

            data.moveToFirst();
            while (!data.isAfterLast()) {

                int id = data.getInt(data.getColumnIndexOrThrow(ShapeContract._ID));
                String name = data.getString(data.getColumnIndexOrThrow(ShapeContract.COL_NAME));
                String color = data.getString(data.getColumnIndexOrThrow(ShapeContract.COL_COLOR));
                int numSides = data.getInt(data.getColumnIndexOrThrow(ShapeContract.COL_NUM_SIDES));

                Shape shape = new Shape(id, name, color, numSides);

                mShapes.add(shape);

                // move to the next position
                data.moveToNext();
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    private final View.OnClickListener AddShapeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            // Start the new intent for adding a shape
            Intent shapeDetailIntent = new Intent(ScrollingActivity.this, ShapeActivity.class);
            startActivity(shapeDetailIntent);
        }
    };

    @Override
    public void onShapeClicked(Shape shape) {
        // TODO: logic for handling an existing shape edit

        // Start the new intent for adding a shape
        Intent shapeDetailIntent = new Intent(ScrollingActivity.this, ShapeActivity.class);
        shapeDetailIntent.putExtra(KEY_SHAPE_ID, shape.id);
        startActivity(shapeDetailIntent);
    }

    /**
     * AsyncTaskLoader for loading a list of Shapes
     */
    private class ShapeListLoader extends AsyncTaskLoader<Loader<Cursor>> {

        public ShapeListLoader(Context context){
            super(context);
        }

        @Override
        public Loader<Cursor> loadInBackground() {
            return new CursorLoader(ScrollingActivity.this, ShapeContract.CONTENT_URI,
                    ShapeContract.PROJECTION_ALL, null, null, ShapeContract.SORT_ORDER_DEFAULT);
        }


    }
}
