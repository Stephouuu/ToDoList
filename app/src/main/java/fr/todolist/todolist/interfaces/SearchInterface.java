package fr.todolist.todolist.interfaces;

import java.util.List;

import fr.todolist.todolist.utils.SortingInfo;
import fr.todolist.todolist.utils.TodoItemFilter;
import fr.todolist.todolist.utils.TodoItemInfo;

/**
 * Created by Stephane on 19/01/2017.
 */

public interface SearchInterface {

    TodoItemFilter getFilter();
    SortingInfo getSortingInfo();
    List<TodoItemInfo> getItemsByDueDate(SortingInfo.Type date);
    List<TodoItemInfo> getItemsByTitle(String toSearch);
    List<TodoItemInfo> getItemsByContent(String toSearch);

}
