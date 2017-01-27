package fr.todolist.todolist;

import android.os.Parcel;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fr.todolist.todolist.utils.TodoItemInfo;

import static org.junit.Assert.assertEquals;

/**
 * Created by Stephane on 27/01/2017.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TodoItemInfoInstrumentedTest {

    private TodoItemInfo item;

    @Before
    public void createTodoItemInfo() {
        item = new TodoItemInfo();
    }

    @Test
    public void Test_ParcelableWriteRead() {
        item.title = "A title";
        item.content = "content";

        Parcel parcel = Parcel.obtain();
        item.writeToParcel(parcel, item.describeContents());

        parcel.setDataPosition(0);

        TodoItemInfo createdFromParcel = TodoItemInfo.CREATOR.createFromParcel(parcel);

        assertEquals(createdFromParcel.title, item.title);
        assertEquals(createdFromParcel.content, item.content);
    }

}
