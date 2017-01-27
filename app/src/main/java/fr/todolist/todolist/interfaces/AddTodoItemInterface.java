package fr.todolist.todolist.interfaces;

/**
 * Created by Stephane on 17/01/2017.
 */

/**
 * Add Activity Interface
 */
public interface AddTodoItemInterface {

    /**
     * When the new date is set
     * @param year The year
     * @param month The month
     * @param day The day
     */
    void onDateSet(int year, int month, int day);

    /**
     * When the new time is set
     * @param hour The hour
     * @param minutes The minutes
     */
    void onTimeSet(int hour, int minutes);

    /**
     * When the user want to add the new item
     */
    void onValid();

}
