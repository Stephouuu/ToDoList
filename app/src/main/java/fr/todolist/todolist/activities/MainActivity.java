package fr.todolist.todolist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import fr.todolist.todolist.R;
import fr.todolist.todolist.fragments.TodoListFragment;
import fr.todolist.todolist.interfaces.HomePageInterface;
import fr.todolist.todolist.interfaces.TodoListInterface;
import fr.todolist.todolist.utils.TodoItemInfo;

public class MainActivity extends AppCompatActivity implements HomePageInterface, TodoListInterface {

    private static final int REQUEST_ADD_ITEM = 1;

    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;

    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

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

        refreshFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);

        searchItem = menu.findItem(R.id.main_menu_search);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_menu_search) {
            Intent search = new Intent(this, SearchActivity.class);
            startActivity(search);
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_ITEM) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Item added with success", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void refreshFragment() {
        TodoListFragment fragment = new TodoListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment).commit();
    }

    @Override
    public void onItemClick(TodoItemInfo item) {
        Log.i("main", "item click: " + item.title);
    }

    @Override
    public void addItem() {
        Intent intent = new Intent(getApplicationContext(), AddTodoItemActivity.class);
        startActivityForResult(intent, REQUEST_ADD_ITEM);
    }

}
