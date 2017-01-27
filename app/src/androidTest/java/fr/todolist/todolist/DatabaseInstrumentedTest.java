package fr.todolist.todolist;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import fr.todolist.todolist.database.AppDatabase;
import fr.todolist.todolist.utils.AlertInfo;
import fr.todolist.todolist.utils.SortingInfo;
import fr.todolist.todolist.utils.TodoItemInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Stephane on 27/01/2017.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class DatabaseInstrumentedTest {

    private Context context;
    private AppDatabase database;

    @Before
    public void createTodoItemInfo() {
        context = InstrumentationRegistry.getTargetContext();
        database = new AppDatabase(context);

        assertNotEquals(null, database);

        database.open();
    }

    @After
    public void deleteDatabase() {
        database.close();
        context.deleteDatabase(AppDatabase.BDD_NAME);
    }

    @Test
    public void Test_GetTitle() throws Exception {
        TodoItemInfo item = new TodoItemInfo();

        item.title = "GetTitle";
        item.content = "A content";

        item = database.insertItem(item);

        TodoItemInfo get = database.getItemByID((int)item.id);

        assertNotEquals(null, get);
        if (get != null) {
            assertEquals(get.title, item.title);
            assertEquals(get.content, item.content);
        }

        List<TodoItemInfo> list = database.getItemsByTitle("tT", SortingInfo.Type.Ascendant);
        assertNotEquals(0, list.size());
    }

    @Test
    public void Test_GetContent() throws Exception {
        TodoItemInfo item = new TodoItemInfo();

        item.title = "GetTitle";
        item.content = "A different content";

        item = database.insertItem(item);

        TodoItemInfo get = database.getItemByID((int)item.id);

        assertNotEquals(null, get);
        if (get != null) {
            assertEquals(get.title, item.title);
            assertEquals(get.content, item.content);
        }

        List<TodoItemInfo> list = database.getItemsByContent("ff", SortingInfo.Type.Ascendant);
        assertNotEquals(0, list.size());
    }

    @Test
    public void Test_InsertTodoItem() throws Exception {
        TodoItemInfo item = new TodoItemInfo();

        item.title = "A title";
        item.content = "A content";

        item = database.insertItem(item);

        TodoItemInfo get = database.getItemByID((int)item.id);

        assertNotEquals(null, get);
        if (get != null) {
            assertEquals(get.title, item.title);
            assertEquals(get.content, item.content);
        }
    }

    @Test
    public void Test_InsertAlertInfo() throws Exception {
        AlertInfo alert = new AlertInfo();

        alert.title = "A title";
        alert.content = "A content";

        alert = database.insertItem(alert);

        AlertInfo get = database.getAlertByID(alert.id);

        assertNotEquals(null, get);
        if (get != null) {
            assertEquals(get.title, alert.title);
            assertEquals(get.content, alert.content);
        }
    }

    @Test
    public void Test_DeleteTodoItem() throws Exception {
        TodoItemInfo item = new TodoItemInfo();

        item.title = "A title";
        item.content = "A content";

        item = database.insertItem(item);

        TodoItemInfo get = database.getItemByID((int)item.id);

        assertNotEquals(null, get);
        if (get != null) {
            assertEquals(get.title, item.title);
            assertEquals(get.content, item.content);
        }

        database.deleteItem(item.id);
        get = database.getItemByID((int)item.id);
        assertEquals(null, get);
    }

    @Test
    public void Test_DeleteAlertItem() throws Exception {
        AlertInfo alert = new AlertInfo();

        alert.title = "A title";
        alert.content = "A content";

        alert = database.insertItem(alert);

        AlertInfo get = database.getAlertByID(alert.id);

        assertNotEquals(null, get);
        if (get != null) {
            assertEquals(get.title, alert.title);
            assertEquals(get.content, alert.content);
        }

        database.deleteAlert(alert.id);
        get = database.getAlertByID(alert.id);
        assertEquals(null, get);
    }

    @Test
    public void Test_UpdateTodoItemInfo() throws Exception {
        TodoItemInfo item = new TodoItemInfo();

        item.title = "A title";
        item.content = "A content";

        item = database.insertItem(item);

        item.title = "Title v2";
        item.content = "Content v2";

        database.updateItem(item);

        TodoItemInfo get = database.getItemByID((int)item.id);

        assertNotEquals(null, get);
        if (get != null) {
            assertEquals(get.title, item.title);
            assertEquals(get.content, item.content);
        }
    }

    @Test
    public void Test_UpdateAlert() throws Exception {
        AlertInfo alert = new AlertInfo();

        alert.title = "A title";
        alert.content = "A content";

        alert = database.insertItem(alert);

        alert.title = "Title v2";
        alert.content = "Content v2";

        database.updateItem(alert);

        AlertInfo get = database.getAlertByID(alert.id);

        assertNotEquals(null, get);
        if (get != null) {
            assertEquals(get.title, alert.title);
            assertEquals(get.content, alert.content);
        }
    }

}
