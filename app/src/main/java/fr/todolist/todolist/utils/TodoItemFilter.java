package fr.todolist.todolist.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stephane on 20/01/2017.
 */

/**
 * Filter structure
 */
public class TodoItemFilter implements Parcelable {

    public static final int STATUS_TODO = 1;
    public static final int STATUS_OK = 2;
    public static final int STATUS_EXPIRED = 4;

    //public boolean expired;
    public int status;

    public TodoItemFilter() {
        //expired = true;
        status = 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(status);
    }

    public static final Parcelable.Creator<TodoItemFilter> CREATOR = new Parcelable.Creator<TodoItemFilter>() {

        @Override
        public TodoItemFilter createFromParcel(Parcel in) {
            return new TodoItemFilter(in);
        }

        @Override
        public TodoItemFilter[] newArray(int size) {
            return new TodoItemFilter[size];
        }
    };

    private TodoItemFilter(Parcel in) {
        status = in.readInt();
    }

    public void setFlags(int flags) {
        status = 0;
        status = flags;
    }

    public void addFlags(int flags) {
        status |= flags;
    }

    public void deleteFlags(int flags) {
        status = (status ^ flags);
    }

    public boolean hasFlags(int flags) {
        return (status & flags) != 0;
    }
}
