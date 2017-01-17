package fr.todolist.todolist.interfaces;

/**
 * Created by Stephane on 17/01/2017.
 */

public interface AddTodoItemInterface {

    void onDateSet(int year, int month, int day);
    void onTimeSet(int hour, int minutes);
    void onValid();

}
