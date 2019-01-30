package com.example.nk.multinotesapp.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MultiNote implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MultiNote createFromParcel(Parcel in) {
            return new MultiNote(in);
        }

        public MultiNote[] newArray(int size) {
            return new MultiNote[size];
        }
    };

    private String noteTitle;
    private String noteDateTime;
    private String noteDescription;

    public MultiNote() {

    }

    public MultiNote(String noteTitle, String noteDateTime, String noteDescription) {
        this.noteTitle = noteTitle;
        this.noteDateTime = noteDateTime;
        this.noteDescription = noteDescription;
    }


    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteDateTime() {
        return noteDateTime;
    }

    public void setNoteDateTime(String noteDateTime) {
        this.noteDateTime = noteDateTime;
    }

    public String getNoteDescription() {
        return noteDescription;
    }

    public void setNoteDescription(String noteDescription) {
        this.noteDescription = noteDescription;
    }

    public MultiNote(Parcel in) {
        try {
            this.noteTitle = in.readString();
            this.noteDateTime = in.readString();
            this.noteDescription = in.readString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.noteTitle);
        dest.writeString(this.noteDateTime);
        dest.writeString(this.noteDescription);
    }

    @Override
    public String toString() {
        return "Multinote{" +
                "title='" + noteTitle + '\'' +
                ", date_time='" + noteDateTime + '\'' +
                ", description='" + noteDescription + '\'' +
                '}';
    }
}
