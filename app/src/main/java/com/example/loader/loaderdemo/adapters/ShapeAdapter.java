package com.example.loader.loaderdemo.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.loader.loaderdemo.R;
import com.example.loader.loaderdemo.interfaces.ShapeClicked;
import com.example.loader.loaderdemo.models.Shape;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter for displaying the items returned from the Content Provider
 * Created by kyle.jablonski on 11/24/15.
 */
public class ShapeAdapter extends RecyclerView.Adapter<ShapeAdapter.ShapeViewHolder> {

    private static final String TAG = ShapeAdapter.class.getSimpleName();

    private Context mContext;
    private List<Shape> mShapes;
    private String formatString;

    private ShapeClicked mSClickCallback;

    public ShapeAdapter(Context context, List<Shape> shapes){
        this.mContext = context;
        this.mShapes = shapes;

        try {
            mSClickCallback = (ShapeClicked) mContext;
        }catch(ClassCastException ex){
            Log.e(TAG, "implement the interface ShapeClicked in the parent", ex);
        }
    }

    @Override
    public int getItemCount() {
        return mShapes.size();
    }

    @Override
    public ShapeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_shape, parent, false);
        return new ShapeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShapeViewHolder holder, int position) {

        final Shape shape = mShapes.get(position);
        if(shape != null){

            holder.mTvName.setText(shape.name);
            holder.mTvColor.setText(shape.color);
            String formatString = mContext.getResources().getQuantityString(R.plurals.sides, shape.num_sides, shape.num_sides);
            holder.mTvNumSides.setText(formatString);

            holder.mRlShape.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    mSClickCallback.onShapeClicked(shape);
                }
            });

        }

    }

    public class ShapeViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.rl_shape) RelativeLayout mRlShape;
        @Bind(R.id.tv_name) AppCompatTextView mTvName;
        @Bind(R.id.tv_color) AppCompatTextView mTvColor;
        @Bind(R.id.tv_num_sides) AppCompatTextView mTvNumSides;


        public ShapeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
