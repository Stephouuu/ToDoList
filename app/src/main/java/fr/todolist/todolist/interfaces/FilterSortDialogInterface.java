package fr.todolist.todolist.interfaces;

import fr.todolist.todolist.utils.SortingInfo;
import fr.todolist.todolist.utils.TodoItemFilter;

/**
 * Created by Stephane on 25/01/2017.
 */

/**
 * FilterSortDialog Interface
 */
public interface FilterSortDialogInterface {

    /**
     * When the user click on the positive button
     * @param filter The filter to apply
     * @param sortingInfo The sorting information to apply
     */
    void onPositiveButtonClick(TodoItemFilter filter, SortingInfo sortingInfo);

    /**
     * When the dialog is closed
     */
    void onDismiss();

}
