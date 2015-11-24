package com.example.loader.loaderdemo.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO for a Shape which implements Parcelable for passing around if need be
 * Created by kyle.jablonski on 11/24/15.
 */
public class Shape implements Parcelable{

    public int id;
    public String name;
    public String color;
    public int num_sides;

    public Shape(int id, String name, String color, int num_sides){
        this.id = id;
        this.name = name;
        this.color = color;
        this.num_sides = num_sides;
    }

    public Shape(Parcel source){
        id = source.readInt();
        name = source.readString();
        color = source.readString();
        num_sides = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(color);
        dest.writeInt(num_sides);
    }

    public static final Parcelable.Creator<Shape> CREATOR = new Parcelable.Creator<Shape>(){
        @Override
        public Shape createFromParcel(Parcel source) {
            return new Shape(source);
        }

        @Override
        public Shape[] newArray(int size) {
            return new Shape[size];
        }
    };
}
