package fr.todolist.todolist.interfaces;

import fr.todolist.todolist.utils.SortingInfo;
import fr.todolist.todolist.utils.TodoItemFilter;

/**
 * Created by Stephane on 25/01/2017.
 */

public interface FilterSortDialogInterface {

    void onPositiveButtonClick(TodoItemFilter filter, SortingInfo sortingInfo);
    void onDismiss();

}
