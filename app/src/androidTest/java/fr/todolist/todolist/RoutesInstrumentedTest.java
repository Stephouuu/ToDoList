package fr.todolist.todolist;

import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.todolist.todolist.utils.Routes;

import static org.junit.Assert.assertEquals;

/**
 * Created by Stephane on 27/01/2017.
 */

@RunWith(AndroidJUnit4.class)
public class RoutesInstrumentedTest {

    @Test
    public void Test_Routes() throws Exception {
        Routes.Load(Uri.parse("todolist://consultation/1234"));

        assertEquals("1234", Routes.GetConsultationID());
    }

}
