package fr.todolist.todolist.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stephane on 17/01/2017.
 */

/**
 * Structure of a Alert Info
 */
public class AlertInfo implements Parcelable {

    public int id;
    public int idTodoItem;
    public String title;
    public String content;
    //public long time;

    public AlertInfo() {
        id = 0;
        idTodoItem = 0;
        title = "Title";
        content = "Content";
        //time = 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeInt(idTodoItem);
        out.writeString(title);
        out.writeString(content);
        //out.writeLong(time);
    }

    public static final Parcelable.Creator<AlertInfo> CREATOR = new Parcelable.Creator<AlertInfo>() {

        @Override
        public AlertInfo createFromParcel(Parcel in) {
            return new AlertInfo(in);
        }

        @Override
        public AlertInfo[] newArray(int size) {
            return new AlertInfo[size];
        }
    };

    private AlertInfo(Parcel in) {
        id = in.readInt();
        idTodoItem = in.readInt();
        title = in.readString();
        content = in.readString();
        //time = in.readLong();
    }
}
