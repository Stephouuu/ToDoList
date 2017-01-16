package fr.todolist.todolist.utils;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stephane on 16/01/2017.
 */

public class TodoItemInfo implements Parcelable {

    public int id;
    public String title;
    public String content;

    public TodoItemInfo() {
        id = 0;
        title = "Title";
        content = "Content";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(title);
        out.writeString(content);
    }

    public static final Parcelable.Creator<TodoItemInfo> CREATOR = new Parcelable.Creator<TodoItemInfo>() {

        @Override
        public TodoItemInfo createFromParcel(Parcel in) {
            return new TodoItemInfo(in);
        }

        @Override
        public TodoItemInfo[] newArray(int size) {
            return new TodoItemInfo[size];
        }
    };

    private TodoItemInfo(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
    }

}
