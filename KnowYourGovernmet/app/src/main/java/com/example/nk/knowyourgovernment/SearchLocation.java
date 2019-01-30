package com.example.nk.knowyourgovernment;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchLocation implements Parcelable {
    private String city;
    private String zip;
    private String state;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.city);
        dest.writeString(this.zip);
        dest.writeString(this.state);
    }

    public SearchLocation() {
    }

    protected SearchLocation(Parcel in) {
        this.city = in.readString();
        this.zip = in.readString();
        this.state = in.readString();
    }

    public static final Parcelable.Creator<SearchLocation> CREATOR = new Parcelable.Creator<SearchLocation>() {
        @Override
        public SearchLocation createFromParcel(Parcel source) {
            return new SearchLocation(source);
        }

        @Override
        public SearchLocation[] newArray(int size) {
            return new SearchLocation[size];
        }
    };
}
