package fr.todolist.todolist.interfaces;

import java.util.List;

import fr.todolist.todolist.utils.TodoItemInfo;

/**
 * Created by Stephane on 19/01/2017.
 */

public interface SearchInterface {

    List<TodoItemInfo> getItemsByDueDateASC();
    List<TodoItemInfo> getItemsByDueDateDESC();
    List<TodoItemInfo> getItemsByTitle(String toSearch);
    List<TodoItemInfo> getItemsByStatus(TodoItemInfo.Status toSearch);
    List<TodoItemInfo> getItemsByContent(String toSearch);

}
