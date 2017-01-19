package fr.todolist.todolist.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stephane on 16/01/2017.
 */

public class TodoItemInfo implements Parcelable {

    public enum Status {
        InProgress,
        Expired,
        Ok
    }

    public long id;
    public String title;
    public String content;
    public String dateTime;
    public Status status;
    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;

    public TodoItemInfo() {
        id = 0;
        title = "Title";
        content = "Content";
        dateTime = "0000-00-00 00:00";
        status = Status.InProgress;
        year = 0;
        month = 0;
        day = 0;
        hour = 0;
        minute = 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(title);
        out.writeString(content);
        out.writeString(dateTime);
        out.writeString(String.valueOf(status));
        out.writeInt(year);
        out.writeInt(month);
        out.writeInt(day);
        out.writeInt(hour);
        out.writeInt(minute);
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
        id = in.readLong();
        title = in.readString();
        content = in.readString();
        dateTime = in.readString();
        status = Status.valueOf(in.readString());
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        hour = in.readInt();
        minute = in.readInt();
    }

}
