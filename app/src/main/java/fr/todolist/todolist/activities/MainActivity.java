package fr.todolist.todolist.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.Toast;

import java.util.List;

import fr.todolist.todolist.R;
import fr.todolist.todolist.database.AppDatabase;
import fr.todolist.todolist.fragments.TodoListFragment;
import fr.todolist.todolist.interfaces.SearchInterface;
import fr.todolist.todolist.interfaces.TodoListInterface;
import fr.todolist.todolist.receivers.AlarmReceiver;
import fr.todolist.todolist.utils.Preferences;
import fr.todolist.todolist.utils.Routes;
import fr.todolist.todolist.utils.TodoItemFilter;
import fr.todolist.todolist.utils.TodoItemInfo;

public class MainActivity extends AppCompatActivity implements SearchInterface, TodoListInterface {

    private static final int REQUEST_ADD_ITEM = 1;

    private FloatingActionButton addFab;
    private FloatingActionButton delFab;
    private AppDatabase database;
    private TodoItemFilter filter;
    private TodoListFragment todoListFragment;

    private LongSparseArray<TodoItemInfo> selected;
    private Mode mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        database = new AppDatabase(getApplicationContext());
        database.open();

        filter = Preferences.getHomePageFilter(getApplicationContext());
        if (filter == null) {
            filter = new TodoItemFilter();
            filter.setFlags(TodoItemFilter.STATUS_TODO);
        }

        Routes.Load(getIntent().getData());

        selected = new LongSparseArray<>();
        mode = Mode.Normal;

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        addFab = (FloatingActionButton)findViewById(R.id.main_add_fab);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        delFab = (FloatingActionButton)findViewById(R.id.main_del_fab);
        delFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0 ; i < selected.size() ; ++i) {
                    TodoItemInfo item = selected.get(selected.keyAt(i));
                    database.deleteItem(item.id);
                    AlarmReceiver.deleteAlarm(getApplicationContext(), (int)item.id);
                    updateMode(Mode.Normal);
                    refreshFragment();
                }
            }
        });

        refreshFragment();
        refreshRoutes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);

        menu.add(getString(R.string.filter));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_menu_search) {
            Intent search = new Intent(this, SearchActivity.class);
            startActivity(search);
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            return true;
        } else if (item.getTitle().equals(getString(R.string.filter))) {
            createFilterDialogBox();
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        database.close();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_ITEM) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Item added with success", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemClick(TodoItemInfo item) {
        Intent intent = new Intent(getApplicationContext(), ConsultationActivity.class);
        ConsultationActivity.setExtraItem(intent, item);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    public void onItemLongClick(View view) {
        updateMode(Mode.Selection);
    }

    @Override
    public void addSelection(TodoItemInfo item) {
        selected.put(item.id, item);
    }

    @Override
    public void deleteSelection(TodoItemInfo item) {
        selected.delete(item.id);
    }

    @Override
    public boolean isInSelectionMode() {
        return (mode == Mode.Selection);
    }

    private void updateMode(Mode mode) {
        this.mode = mode;
        if (mode == Mode.Normal) {
            delFab.setVisibility(View.GONE);
            addFab.setVisibility(View.VISIBLE);
        }
        else if (mode == Mode.Selection) {
            addFab.setVisibility(View.GONE);
            delFab.setVisibility(View.VISIBLE);
        }
    }

    private void createFilterDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.filter_dialog_fragment, null);

        final CheckedTextView inProgress = (CheckedTextView)view.findViewById(R.id.checkbox_inprogress);
        final CheckedTextView ok = (CheckedTextView)view.findViewById(R.id.checkbox_ok);
        final CheckedTextView expired = (CheckedTextView)view.findViewById(R.id.checkbox_expired);

        builder.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Preferences.setHomePageFilter(getApplicationContext(), filter);
                        refreshFragment();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        refreshFragment();
                    }
                })
                .create()
                .show();

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

    private void refreshFragment() {
        if (todoListFragment == null) {
            todoListFragment = new TodoListFragment();
            Bundle args = new Bundle();
            todoListFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, todoListFragment).commit();
        } else {
            todoListFragment.onResume();
        }
    }

    private void refreshRoutes() {
        String itemID = Routes.GetConsultationID(getApplicationContext());
        if (itemID != null) {
            TodoItemInfo item = database.getItemByID(Integer.valueOf(itemID));
            if (item != null) {
                onItemClick(item);
            }
        }
    }

    private void addItem() {
        Intent intent = new Intent(getApplicationContext(), AddTodoItemActivity.class);
        startActivityForResult(intent, REQUEST_ADD_ITEM);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    public TodoItemFilter getFilter() {
        return (filter);
    }

    @Override
    public List<TodoItemInfo> getItemsByDueDateASC() {
        return (database.getItemsByDueDateASC());
    }

    @Override
    public List<TodoItemInfo> getItemsByDueDateDESC() {
        return (database.getItemsByDueDateDESC());
    }

    @Override
    public List<TodoItemInfo> getItemsByTitle(String toSearch) {
        return (database.getItemsByTitle(toSearch));
    }

    @Override
    public List<TodoItemInfo> getItemsByStatus(TodoItemInfo.Status toSearch) {
        return (database.getItemsByStatus(toSearch));
    }

    @Override
    public List<TodoItemInfo> getItemsByContent(String toSearch) {
        return (database.getItemsByContent(toSearch));
    }
}
