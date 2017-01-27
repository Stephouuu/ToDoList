package fr.todolist.todolist.interfaces;

import android.view.View;

import fr.todolist.todolist.utils.TodoItemInfo;

/**
 * Created by Stephane on 16/01/2017.
 */

/**
 * The to do list fragment interface
 */
public interface TodoListInterface {

    /**
     * The mode of the to do list fragment
     */
    enum Mode {
        Normal,
        Selection
    }

    /**
     * When the user click on a item
     * @param item The item
     */
    void onItemClick(TodoItemInfo item);

    /**
     * When the user long click on a item
     * @param view The view
     */
    void onItemLongClick(View view);

    /**
     * Return if the item is selected
     * @param item The item to check
     * @return True or False
     */
    boolean isSelected(TodoItemInfo item);

    /**
     * Add item in selection
     * @param item The item to add
     */
    void addSelection(TodoItemInfo item);

    /**
     * Delete item in a selection
     * @param item The itemt o delete
     */
    void deleteSelection(TodoItemInfo item);

    /**
     * Check if the fragment is in selection mode
     * @return
     */
    boolean isInSelectionMode();

}
