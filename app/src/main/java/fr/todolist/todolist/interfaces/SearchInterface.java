package fr.todolist.todolist.interfaces;

import java.util.List;

import fr.todolist.todolist.utils.SortingInfo;
import fr.todolist.todolist.utils.TodoItemFilter;
import fr.todolist.todolist.utils.TodoItemInfo;

/**
 * Created by Stephane on 19/01/2017.
 */

/**
 * Search activity interface
 */
public interface SearchInterface {

    /**
     * Return the filter used
     * @return The filter
     */
    TodoItemFilter getFilter();

    /**
     * Return the sorting information used
     * @return The sorting information
     */
    SortingInfo getSortingInfo();

    /**
     * Return a to do item list order by the due date
     * @param date The date
     * @return The list of item
     */
    List<TodoItemInfo> getItemsByDueDate(SortingInfo.Type date);

    /**
     * Return a to do item list order by the due date
     * @param toSearch The title to search
     * @param date The date
     * @return The list of item
     */
    List<TodoItemInfo> getItemsByTitle(String toSearch, SortingInfo.Type date);

    /**
     * Return a to do item list order by the due date
     * @param toSearch The content to search
     * @param date The date
     * @return The list of item
     */
    List<TodoItemInfo> getItemsByContent(String toSearch, SortingInfo.Type date);

}
