package com.example.myapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class QuestionInfo implements Parcelable {
    private String question;
    private List<OptionInfo> options;

    public QuestionInfo(String question, ArrayList<OptionInfo> options) {
        this.question = question;
        this.options = options;
    }

    private QuestionInfo(Parcel in) {
        question = in.readString();
        options = in.readArrayList(OptionInfo.class.getClassLoader());
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Question: " + question + " with " + options.size() + " options.";
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(question);
        out.writeList(options);
    }

    public static final Creator<QuestionInfo> CREATOR = new Creator<QuestionInfo>() {
        public QuestionInfo createFromParcel(Parcel in) {
            return new QuestionInfo(in);
        }

        public QuestionInfo[] newArray(int size) {
            return new QuestionInfo[size];
        }
    };
}
