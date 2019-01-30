package com.example.nk.knowyourgovernment;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Official implements Parcelable {
    private String name;
    private String lineAddress;
    private String city;
    private String state;
    private String zip;
    private String party;
    private String phone;
    private String url;
    private String email;
    private String photoURL;
    private HashMap<String, String> channels;
    private String officeName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLineAddress() {
        return lineAddress;
    }

    public void setLineAddress(String lineAddress) {
        this.lineAddress = lineAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public HashMap<String, String> getChannels() {
        return channels;
    }

    public void setChannels(HashMap<String, String> channels) {
        this.channels = channels;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(lineAddress);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(zip);
        dest.writeString(party);
        dest.writeString(phone);
        dest.writeString(url);
        dest.writeString(email);
        dest.writeString(photoURL);
        dest.writeString(officeName);
        dest.writeSerializable(channels);

    }

    public static final Parcelable.Creator<Official> CREATOR
            = new Parcelable.Creator<Official>() {
        public Official createFromParcel(Parcel in) {
            return new Official(in);
        }

        public Official[] newArray(int size) {
            return new Official[size];
        }
    };

    public Official() {

    }

    private Official(Parcel in) {
        name = in.readString();
        lineAddress = in.readString();
        city = in.readString();
        state = in.readString();
        zip = in.readString();
        party = in.readString();
        phone = in.readString();
        url = in.readString();
        email = in.readString();
        photoURL = in.readString();
        officeName = in.readString();
        channels = (HashMap<String, String>) in.readSerializable();
    }
}
