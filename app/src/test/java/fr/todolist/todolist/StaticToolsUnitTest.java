package fr.todolist.todolist;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import fr.todolist.todolist.utils.StaticTools;

import static org.junit.Assert.assertEquals;

/**
 * Created by Stephane on 27/01/2017.
 */

public class StaticToolsUnitTest {

    @Test
    public void Test_DeserializeFiles() throws Exception {
        String serialized = "1;2;3;4;5";
        String[] deserialized = StaticTools.deserializeFiles(serialized, ";");

        assertEquals("5", String.valueOf(deserialized.length));
        assertEquals("1", deserialized[0]);
        assertEquals("2", deserialized[1]);
        assertEquals("3", deserialized[2]);
        assertEquals("4", deserialized[3]);
        assertEquals("5", deserialized[4]);
    }

    @Test
    public void Test_SerializeFiles() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        String serialized = StaticTools.serializeFile(list);

        assertEquals("1;2;3;4;5", serialized);
    }

}
