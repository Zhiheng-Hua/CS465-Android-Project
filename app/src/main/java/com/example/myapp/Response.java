package com.example.myapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Response implements Parcelable {
    private String emoji;
    private String owner;

    public Response(String emoji, String owner) {
        this.emoji = emoji;
        this.owner = owner;
    }

    private Response(Parcel in) {
        emoji = in.readString();
        owner = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Emoji: " + emoji + "; Owner: " + owner;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(emoji);
        out.writeString(owner);
    }

    public static final Parcelable.Creator<Response> CREATOR = new Parcelable.Creator<Response>() {
        public Response createFromParcel(Parcel in) {
            return new Response(in);
        }

        public Response[] newArray(int size) {
            return new Response[size];
        }
    };
}
