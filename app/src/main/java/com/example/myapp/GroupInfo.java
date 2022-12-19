package com.example.myapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class GroupInfo implements Parcelable {
    private String name;
    private String owner;
    private ArrayList<QuestionInfo> questions;
    private int id;

    public GroupInfo(String name, String owner, ArrayList<QuestionInfo> questions, int id) {
        this.name = name;
        this.owner = owner;
        this.questions = questions;
        this.id = id;
    }

    private GroupInfo(Parcel in) {
        name = in.readString();
        owner = in.readString();
        questions = in.readArrayList(QuestionInfo.class.getClassLoader());
        id = in.readInt();
    }
    public void updateGroup(GroupInfo info) {
        this.id = info.getId();
        this.name = info.getName();
        this.questions = info.getQuestions();
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Group Name: " + name + "; Owned by: " + owner + " with " + questions.size() + " questions";
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(owner);
        out.writeList(questions);
        out.writeInt(id);
    }

    public static final Creator<GroupInfo> CREATOR = new Creator<GroupInfo>() {
        public GroupInfo createFromParcel(Parcel in) {
            return new GroupInfo(in);
        }

        public GroupInfo[] newArray(int size) {
            return new GroupInfo[size];
        }
    };
    public int getId() { return id; }
    public String getName() { return name; }
    public ArrayList<QuestionInfo> getQuestions() { return questions;}
    public void setId(int id) {this.id = id;}
    public void setName(String name) {this.name = name;}
}
