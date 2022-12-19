package com.example.myapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class OptionInfo implements Parcelable {
    private String option;
    private List<Response> responses;

    public OptionInfo(String option, ArrayList<Response> responses) {
        this.option = option;
        this.responses = responses;
    }

    private OptionInfo(Parcel in) {
        option = in.readString();
        responses = in.readArrayList(Response.class.getClassLoader());
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Option: " + option + " with " + responses.size() + " responses";
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(option);
        out.writeList(responses);
    }

    public static final Parcelable.Creator<OptionInfo> CREATOR = new Parcelable.Creator<OptionInfo>() {
        public OptionInfo createFromParcel(Parcel in) {
            return new OptionInfo(in);
        }

        public OptionInfo[] newArray(int size) {
            return new OptionInfo[size];
        }
    };
}
