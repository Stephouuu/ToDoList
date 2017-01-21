package fr.todolist.todolist.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.Toast;

import java.util.List;

import fr.todolist.todolist.R;
import fr.todolist.todolist.database.TodoItemDatabase;
import fr.todolist.todolist.fragments.TodoListFragment;
import fr.todolist.todolist.interfaces.SearchInterface;
import fr.todolist.todolist.interfaces.TodoListInterface;
import fr.todolist.todolist.utils.Routes;
import fr.todolist.todolist.utils.TodoItemFilter;
import fr.todolist.todolist.utils.TodoItemInfo;

public class MainActivity extends AppCompatActivity implements SearchInterface, TodoListInterface {

    private static final int REQUEST_ADD_ITEM = 1;

    private FloatingActionButton addFab;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private TodoItemDatabase database;
    private TodoItemFilter filter;
    private MenuItem searchItem;
    private TodoListFragment todoListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        database = new TodoItemDatabase(getApplicationContext());
        database.open();

        filter = new TodoItemFilter();
        filter.setFlags(TodoItemFilter.STATUS_TODO | TodoItemFilter.STATUS_OK);

        Routes.Load(getIntent().getData());

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        addFab = (FloatingActionButton)findViewById(R.id.main_add_fab);

        drawer = (DrawerLayout)findViewById(R.id.main_drawer);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.mipmap.hamburger);
        toggle.syncState();

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout)findViewById(R.id.main_drawer);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        refreshFragment();
        refreshRoutes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);

        searchItem = menu.findItem(R.id.main_menu_search);

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

    }

    private void createFilterDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.filter_dialog_fragment, null);

        final CheckedTextView inProgress = (CheckedTextView)view.findViewById(R.id.checkbox_inprogress);
        final CheckedTextView ok = (CheckedTextView)view.findViewById(R.id.checkbox_ok);
        final CheckedTextView expired = (CheckedTextView)view.findViewById(R.id.checkbox_expired);

        builder.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refreshFragment();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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
            TodoListFragment fragment = new TodoListFragment();
            //TodoItemFilter filter = new TodoItemFilter();
            //filter.expired = false;
            //filter.setFlags(TodoItemFilter.STATUS_TODO | TodoItemFilter.STATUS_OK);
            Bundle args = new Bundle();
            //args.putParcelable(TodoListFragment.EXTRA_FILTER, filter);
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment).commit();
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
