package fr.todolist.todolist.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import fr.todolist.todolist.utils.SortingInfo;
import fr.todolist.todolist.utils.TodoItemFilter;
import fr.todolist.todolist.utils.TodoItemInfo;

public class MainActivity extends AppCompatActivity implements SearchInterface, TodoListInterface {

    enum MainFabMode {
        Add,
        Cancel
    }

    private static final int REQUEST_ADD_ITEM = 1;

    private RelativeLayout menuFabs;
    private boolean menuFabsOpen;
    private FloatingActionButton validFab;
    private FloatingActionButton delFab;
    private FloatingActionButton addFab;

    private AppDatabase database;
    private TodoItemFilter filter;
    private SortingInfo sorting;
    private TodoListFragment todoListFragment;

    private LongSparseArray<TodoItemInfo> selected;
    private MainFabMode fabMode;
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

        sorting = Preferences.getHomePageSorting(getApplicationContext());

        selected = new LongSparseArray<>();
        fabMode = MainFabMode.Add;
        mode = Mode.Normal;
        menuFabsOpen = false;

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        menuFabs = (RelativeLayout)findViewById(R.id.main_fab_menu);

        validFab = (FloatingActionButton)findViewById(R.id.main_valid_fab);
        validFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("selected", "size: " + selected.size());
                for (int i = 0 ; i < selected.size() ; ++i) {
                    TodoItemInfo item = selected.get(selected.keyAt(i));
                    item.status = TodoItemInfo.Status.Done;
                    database.updateItem(item);
                    AlarmReceiver.deleteAlarm(getApplicationContext(), (int)item.id);
                }
                //selected.clear();
                //refreshFragment();
                updateMode(Mode.Normal);
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
                }
                //selected.clear();
                //refreshFragment();
                updateMode(Mode.Normal);
            }
        });

        addFab = (FloatingActionButton)findViewById(R.id.main_add_fab);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabMode == MainFabMode.Add) {
                    addItem();
                } else {
                    updateMode(Mode.Normal);
                    refreshFragment();
                }
            }
        });

        refreshFragment();
        refreshRoutes();
    }

    @Override
    public void onBackPressed() {
        if (mode != Mode.Normal) {
            updateMode(Mode.Normal);
        }
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
        } else if (item.getTitle().equals(getString(R.string.sorting))) {
            createSortingDialogBox();
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
        Intent intent = new Intent(getApplicationContext(), AddTodoItemActivity.class);
        AddTodoItemActivity.setExtraMode(intent, AddTodoItemActivity.Mode.Consultation);
        AddTodoItemActivity.setExtraItem(intent, item);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    public void onItemLongClick(View view) {
        updateMode(Mode.Selection);
    }

    @Override
    public boolean isSelected(TodoItemInfo item) {
        return selected.get(item.id) != null;
    }

    @Override
    public void addSelection(TodoItemInfo item) {
        Log.i("selected", "put id " + item.id);
        selected.put(item.id, item);
    }

    @Override
    public void deleteSelection(TodoItemInfo item) {
        Log.i("selected", "delete id " + item.id);
        selected.delete(item.id);
    }



    @Override
    public boolean isInSelectionMode() {
        return (mode == Mode.Selection);
    }

    private void updateMode(Mode mode) {
        if (mode == Mode.Selection && this.mode != Mode.Selection) {
            setMainFabMode(MainFabMode.Cancel);
        }
        else if (mode == Mode.Normal && this.mode != Mode.Normal) {
            setMainFabMode(MainFabMode.Add);
        }

        if (mode == Mode.Normal) {
            selected.clear();
            closeFabMenu();
        }
        else if (mode == Mode.Selection) {
            openFabMenu();
        }
        todoListFragment.refreshList();
        this.mode = mode;
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

    private void createSortingDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.sorting_dialog_fragment, null);

        final Spinner dateSpinner = (Spinner)view.findViewById(R.id.sorting_date_spinner);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.sorting_order, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(adapter2);
        dateSpinner.setSelection(sorting.date.ordinal());

        builder.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int date = dateSpinner.getSelectedItemPosition();

                        if (date == SortingInfo.Type.Ascendant.ordinal()) {
                            sorting.date = SortingInfo.Type.Ascendant;
                        } else {
                            sorting.date = SortingInfo.Type.Descendant;
                        }

                        Preferences.setHomePageSorting(getApplicationContext(), sorting);
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
    public SortingInfo getSortingInfo() {
        return (sorting);
    }

    @Override
    public List<TodoItemInfo> getItemsByDueDate(SortingInfo.Type date) {
        return (database.getItemsByDueDate(date));
    }

    @Override
    public List<TodoItemInfo> getItemsByTitle(String toSearch) {
        return (database.getItemsByTitle(toSearch));
    }

    @Override
    public List<TodoItemInfo> getItemsByContent(String toSearch) {
        return (database.getItemsByContent(toSearch));
    }

    private void setMainFabMode(MainFabMode fabMode) {
        if (fabMode == MainFabMode.Cancel) {
            addFab.animate().rotation(45).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(400);
        }
        else {
            addFab.animate().rotation(0).setInterpolator(new LinearInterpolator()).setDuration(200);
        }
        this.fabMode = fabMode;
    }

    private void openFabMenu() {
        if (!menuFabsOpen) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    menuFabs.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            menuFabs.startAnimation(animation);
            menuFabsOpen = true;
        }
    }

    private void closeFabMenu() {
        if (menuFabsOpen) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    menuFabs.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            menuFabs.startAnimation(animation);
            menuFabsOpen = false;
        }
    }
}
