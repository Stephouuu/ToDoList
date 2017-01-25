package fr.todolist.todolist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.LongSparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import fr.todolist.todolist.R;
import fr.todolist.todolist.database.AppDatabase;
import fr.todolist.todolist.dialogs.FilterSortAlertDialog;
import fr.todolist.todolist.fragments.TodoListFragment;
import fr.todolist.todolist.interfaces.FilterSortDialogInterface;
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
                for (int i = 0 ; i < selected.size() ; ++i) {
                    TodoItemInfo item = selected.get(selected.keyAt(i));
                    item.status = TodoItemInfo.Status.Done;
                    database.updateItem(item);
                    AlarmReceiver.deleteAlarm(getApplicationContext(), (int)item.id);
                }
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

        //menu.add(getString(R.string.filter));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_menu_search) {
            Intent search = new Intent(this, SearchActivity.class);
            startActivity(search);
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            return true;
        /*} else if (item.getTitle().equals(getString(R.string.filter))) {
            createFilterDialogBox();
            return true;*/
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

    private void createSortingDialogBox() {
        final FilterSortAlertDialog dialog = new FilterSortAlertDialog(this, filter, sorting,
                new FilterSortDialogInterface() {
                    @Override
                    public void onPositiveButtonClick(TodoItemFilter filter, SortingInfo sortingInfo) {
                        MainActivity.this.filter = filter;
                        sorting = sortingInfo;
                        refreshFragment();
                    }

                    @Override
                    public void onDismiss() {
                        refreshFragment();
                    }
                });
        dialog.setButtons().create().show();
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
    public List<TodoItemInfo> getItemsByTitle(String toSearch, SortingInfo.Type date) {
        return (database.getItemsByTitle(toSearch, date));
    }

    @Override
    public List<TodoItemInfo> getItemsByContent(String toSearch, SortingInfo.Type date) {
        return (database.getItemsByContent(toSearch, date));
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
