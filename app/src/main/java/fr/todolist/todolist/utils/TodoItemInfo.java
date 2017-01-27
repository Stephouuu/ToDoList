package fr.todolist.todolist.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stephane on 16/01/2017.
 */

/**
 * To do item info structure
 */
public class TodoItemInfo implements Parcelable {

    public enum Status {
        None(0),
        ToDo(1),
        Done(2),
        Overdue(4);

        private int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public long id;
    public String title;
    public String content;
    public String dateTime;
    public Status status;
    public boolean remind;
    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;
    public int nbRecurrence;
    public int nbBaseRecurrence;
    public long intervalRecurrence;
    public String intervalType;
    public int priority;
    public String photos;

    public TodoItemInfo() {
        id = 0;
        title = "Title";
        content = "Content";
        dateTime = "0000-00-00 00:00";
        status = Status.ToDo;
        remind = false;
        year = 0;
        month = 0;
        day = 0;
        hour = 0;
        minute = 0;
        nbRecurrence = 0;
        nbBaseRecurrence = 0;
        intervalRecurrence = 0;
        intervalType = "Second(s)";
        priority = 0;
        photos = "";
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
        out.writeInt(remind?1:0);
        out.writeInt(year);
        out.writeInt(month);
        out.writeInt(day);
        out.writeInt(hour);
        out.writeInt(minute);
        out.writeInt(nbRecurrence);
        out.writeInt(nbBaseRecurrence);
        out.writeLong(intervalRecurrence);
        out.writeString(intervalType);
        out.writeInt(priority);
        out.writeString(photos);
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
        remind = in.readInt() == 1;
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        hour = in.readInt();
        minute = in.readInt();
        nbRecurrence = in.readInt();
        nbBaseRecurrence = in.readInt();
        intervalRecurrence = in.readLong();
        intervalType = in.readString();
        priority = in.readInt();
        photos = in.readString();
    }

}
