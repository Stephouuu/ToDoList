package fr.todolist.todolist.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import fr.todolist.todolist.R;
import fr.todolist.todolist.adapters.SearchViewPagerAdapter;
import fr.todolist.todolist.database.AppDatabase;
import fr.todolist.todolist.fragments.TodoListFragment;
import fr.todolist.todolist.interfaces.SearchInterface;
import fr.todolist.todolist.interfaces.TodoListInterface;
import fr.todolist.todolist.utils.TodoItemFilter;
import fr.todolist.todolist.utils.TodoItemInfo;

public class SearchActivity extends AppCompatActivity implements SearchInterface, TodoListInterface {

    private static final String EXTRA_SEARCH = "search.activity.search";

    public static String getSearch(Intent intent) {
        return intent.getStringExtra(EXTRA_SEARCH);
    }

    public static void setSearch(Intent intent, String value) {
        intent.putExtra(EXTRA_SEARCH, value);
    }

    private RelativeLayout introContainer;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AppDatabase database;
    private TodoItemFilter filter;

    private TodoListFragment searchTitleFragment;
    private TodoListFragment searchContentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        database = new AppDatabase(getApplicationContext());
        database.open();

        filter = new TodoItemFilter();
        filter.setFlags(TodoItemFilter.STATUS_TODO | TodoItemFilter.STATUS_OK | TodoItemFilter.STATUS_EXPIRED);

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        introContainer = (RelativeLayout) findViewById(R.id.search_intro_container);
        viewPager = (ViewPager) findViewById(R.id.search_view_pager);

        tabLayout = (TabLayout) findViewById(R.id.search_tab_layout);

        createFragments();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onDestroy() {
        database.close();
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_activity_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setIconified(false);
        searchView.setQueryHint(getResources().getString(R.string.search));
        searchView.setBackgroundColor(Color.WHITE);

        String search = getSearch(getIntent());
        if (search != null && !search.isEmpty()) {
            MenuItemCompat.expandActionView(searchItem);
            searchView.setQuery(search, true);
            searchView.clearFocus();
        }

        // Définit l'action lors de la recherche d'un terme
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                if (search != null && search.length() > 0) {
                    introContainer.setVisibility(View.GONE);
                    setSearch(getIntent(), search);
                    searchTitleFragment.setSearchParameter(search);
                    searchContentFragment.setSearchParameter(search);

                    searchTitleFragment.refreshList();
                    searchContentFragment.refreshList();

                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                } else {
                    Toast.makeText(SearchActivity.this, R.string.error_search_at_leat_three_character, Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                return false;
            }
        });

        // Définit l'action lors de la fermeture de la barre de recherche
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                String search = getSearch(getIntent());
                if (search != null && !search.isEmpty() && search.length() > 2) {

                }
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }
        });

        return true;
    }

    private void createFragments() {
        {
            searchTitleFragment = new TodoListFragment();
            Bundle args1 = new Bundle();
            args1.putString(TodoListFragment.EXTRA_MODE, String.valueOf(TodoListFragment.Mode.Title));
            searchTitleFragment.setArguments(args1);
        }
        {
            searchContentFragment = new TodoListFragment();
            Bundle args2 = new Bundle();
            args2.putString(TodoListFragment.EXTRA_MODE, String.valueOf(TodoListFragment.Mode.Content));
            searchContentFragment.setArguments(args2);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        SearchViewPagerAdapter adapter = new SearchViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(searchTitleFragment, getString(R.string.search_by_title));
        adapter.addFragment(searchContentFragment, getString(R.string.search_by_content));

        viewPager.setAdapter(adapter);
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
