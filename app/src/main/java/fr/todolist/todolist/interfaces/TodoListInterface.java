package fr.todolist.todolist.interfaces;

import android.view.View;

import fr.todolist.todolist.utils.TodoItemInfo;

/**
 * Created by Stephane on 16/01/2017.
 */

public interface TodoListInterface {

    enum Mode {
        Normal,
        Selection
    }

    void onItemClick(TodoItemInfo item);
    void onItemLongClick(View view);

    void addSelection(TodoItemInfo item);
    void deleteSelection(TodoItemInfo item);

    boolean isInSelectionMode();

}
