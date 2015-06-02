package com.maciejkozlowski.marketsfinder.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable{

    public static final String MARKET = "Biedronka";

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel parcel) {
            return new Place(parcel);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    public String address;
    public String hours;

    public double lat;
    public double lon;

    public Place(){

    }

    public Place(Parcel in){
        address = in.readString();
        hours = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(address);
        parcel.writeString(hours);
        parcel.writeDouble(lat);
        parcel.writeDouble(lon);
    }
}
