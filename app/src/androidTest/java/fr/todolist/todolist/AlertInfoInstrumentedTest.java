package fr.todolist.todolist;

import android.os.Parcel;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fr.todolist.todolist.utils.AlertInfo;

import static org.junit.Assert.assertEquals;

/**
 * Created by Stephane on 27/01/2017.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class AlertInfoInstrumentedTest {

    private AlertInfo alert;

    @Before
    public void createTodoItemInfo() {
        alert = new AlertInfo();
    }

    @Test
    public void Test_ParcelableWriteRead() {
        alert.title = "A title";
        alert.content = "content";

        Parcel parcel = Parcel.obtain();
        alert.writeToParcel(parcel, alert.describeContents());

        parcel.setDataPosition(0);

        AlertInfo createdFromParcel = AlertInfo.CREATOR.createFromParcel(parcel);

        assertEquals(createdFromParcel.title, alert.title);
        assertEquals(createdFromParcel.content, alert.content);
    }

}
