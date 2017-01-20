package fr.todolist.todolist.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stephane on 20/01/2017.
 */

public class TodoItemFilter implements Parcelable {

    public boolean expired;

    public TodoItemFilter() {
        expired = true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(expired?1:0);
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
        expired = in.readInt() == 1;
    }
}
