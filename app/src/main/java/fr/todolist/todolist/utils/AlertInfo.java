package fr.todolist.todolist.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Stephane on 17/01/2017.
 */

public class AlertInfo implements Parcelable {

    @NonNull
    private static final AtomicInteger atomicInteger = new AtomicInteger(0);

    public int id;
    public String title;
    public String content;
    public long time;

    public AlertInfo() {
        id = atomicInteger.incrementAndGet();
        title = "Title";
        content = "Content";
        time = 0;
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
        out.writeLong(time);
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
        title = in.readString();
        content = in.readString();
        time = in.readLong();
    }
}
