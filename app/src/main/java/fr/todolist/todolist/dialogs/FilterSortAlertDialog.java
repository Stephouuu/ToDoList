package fr.todolist.todolist.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.Spinner;

import fr.todolist.todolist.R;
import fr.todolist.todolist.interfaces.FilterSortDialogInterface;
import fr.todolist.todolist.utils.Preferences;
import fr.todolist.todolist.utils.SortingInfo;
import fr.todolist.todolist.utils.TodoItemFilter;

/**
 * Created by Stephane on 25/01/2017.
 */

/**
 * Manage the filter and sorting dialog
 */
public class FilterSortAlertDialog extends AlertDialog.Builder {

    private Activity activity;

    private TodoItemFilter filter;
    private SortingInfo sorting;

    private CheckedTextView inProgress;
    private CheckedTextView ok;
    private CheckedTextView expired;

    private Spinner dateSpinner;

    private FilterSortDialogInterface listener;

    /**
     * Public constructor
     * @param activity The activity
     * @param baseFilter The default filter
     * @param baseSorting The default sorting
     * @param listener The listener
     */
    public FilterSortAlertDialog(Activity activity, TodoItemFilter baseFilter, SortingInfo baseSorting,
                                 FilterSortDialogInterface listener) {
        super(activity);
        this.activity = activity;
        this.filter = baseFilter;
        this.sorting = baseSorting;
        this.listener = listener;

        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(activity).inflate(R.layout.sorting_filter_dialog, null);

        initFilterView(view);
        initSortingView(view);

        setView(view);
    }

    private void initFilterView(View view) {
        inProgress = (CheckedTextView)view.findViewById(R.id.checkbox_inprogress);
        ok = (CheckedTextView)view.findViewById(R.id.checkbox_ok);
        expired = (CheckedTextView)view.findViewById(R.id.checkbox_expired);

        if (filter.hasFlags(TodoItemFilter.STATUS_TODO)) {
            inProgress.toggle();
        }
        if (filter.hasFlags(TodoItemFilter.STATUS_OK)) {
            ok.toggle();
        }
        if (filter.hasFlags(TodoItemFilter.STATUS_EXPIRED)) {
            expired.toggle();
        }

        inProgress.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                inProgress.toggle();
                if (inProgress.isChecked()) {
                    filter.addFlags(TodoItemFilter.STATUS_TODO);
                } else {
                    filter.deleteFlags(TodoItemFilter.STATUS_TODO);
                }
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ok.toggle();
                if (ok.isChecked()) {
                    filter.addFlags(TodoItemFilter.STATUS_OK);
                } else {
                    filter.deleteFlags(TodoItemFilter.STATUS_OK);
                }
            }
        });

        expired.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                expired.toggle();
                if (expired.isChecked()) {
                    filter.addFlags(TodoItemFilter.STATUS_EXPIRED);
                } else {
                    filter.deleteFlags(TodoItemFilter.STATUS_EXPIRED);
                }
            }
        });
    }

    private void initSortingView(View view) {
        dateSpinner = (Spinner)view.findViewById(R.id.sorting_date_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity, R.array.sorting_order, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(adapter);
        dateSpinner.setSelection(sorting.date.ordinal());
    }

    public AlertDialog.Builder setButtons() {
        setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int date = dateSpinner.getSelectedItemPosition();

                if (date == SortingInfo.Type.Ascendant.ordinal()) {
                    sorting.date = SortingInfo.Type.Ascendant;
                } else {
                    sorting.date = SortingInfo.Type.Descendant;
                }

                Preferences.setHomePageSorting(activity, sorting);
                Preferences.setHomePageFilter(activity, filter);
                listener.onPositiveButtonClick(filter, sorting);
            }
        });

        setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        listener.onDismiss();
                    }
        });

        return this;
    }

    /*public TodoItemFilter getFilter() {
        return (filter);
    }

    public SortingInfo getSortingInfo() {
        return (sorting);
    }*/
}
