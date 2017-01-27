package fr.todolist.todolist.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import fr.todolist.todolist.R;
import fr.todolist.todolist.adapters.SearchViewPagerAdapter;
import fr.todolist.todolist.database.AppDatabase;
import fr.todolist.todolist.fragments.TodoListFragment;
import fr.todolist.todolist.interfaces.SearchInterface;
import fr.todolist.todolist.interfaces.TodoListInterface;
import fr.todolist.todolist.utils.SortingInfo;
import fr.todolist.todolist.utils.TodoItemFilter;
import fr.todolist.todolist.utils.TodoItemInfo;

/**
 * This class goal's is to manage the search section of the application.
 */
public class SearchActivity extends AppCompatActivity implements SearchInterface, TodoListInterface {

    private static final String EXTRA_SEARCH = "search.activity.search";

    public static String getSearch(Intent intent) {
        return intent.getStringExtra(EXTRA_SEARCH);
    }

    public static void setSearch(Intent intent, String value) {
        intent.putExtra(EXTRA_SEARCH, value);
    }

    private AppDatabase database;
    private TodoItemFilter filter;
    private SortingInfo sorting;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton sortFab;

    private RelativeLayout popUpSortParent;
    private LinearLayout popUpSort;
    private Button popUpSortOK;

    private TodoListFragment searchTitleFragment;
    private TodoListFragment searchContentFragment;

    private AlphaAnimation popUpBgAnimationIn;
    private AlphaAnimation popUpBgAnimationOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        database = new AppDatabase(getApplicationContext());
        database.open();

        filter = new TodoItemFilter();
        filter.setFlags(TodoItemFilter.STATUS_TODO | TodoItemFilter.STATUS_OK | TodoItemFilter.STATUS_EXPIRED);

        sorting = new SortingInfo();
        sorting.date = SortingInfo.Type.Ascendant;

        popUpBgAnimationIn = new AlphaAnimation(0.0f, 1.0f);
        popUpBgAnimationIn.setInterpolator(new LinearInterpolator());
        popUpBgAnimationIn.setDuration(400);
        popUpBgAnimationOut = new AlphaAnimation(1.0f, 0.0f);
        popUpBgAnimationOut.setInterpolator(new LinearInterpolator());
        popUpBgAnimationOut.setDuration(200);
        popUpBgAnimationOut.setAnimationListener(
                new Animation.AnimationListener() {
                     @Override
                     public void onAnimationStart(Animation animation) {
                     }

                     @Override
                     public void onAnimationEnd(Animation animation) {
                         popUpSortParent.setVisibility(View.GONE);
                     }

                     @Override
                     public void onAnimationRepeat(Animation animation) {
                     }
                 });

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewPager = (ViewPager) findViewById(R.id.search_view_pager);
        tabLayout = (TabLayout) findViewById(R.id.search_tab_layout);

        sortFab = (FloatingActionButton) findViewById(R.id.search_sort_fab);
        popUpSortParent = (RelativeLayout) findViewById(R.id.popup_sort_parent);
        popUpSort = (LinearLayout) findViewById(R.id.popup_sort);
        popUpSortOK = (Button) findViewById(R.id.popup_sort_ok);

        sortFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopUpSort();
            }
        });

        createFragments();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        initPopup();
    }

    /**
     * Init the filter popup
     */
    private void initPopup() {
        final CheckedTextView inProgress = (CheckedTextView)findViewById(R.id.checkbox_inprogress);
        final CheckedTextView ok = (CheckedTextView)findViewById(R.id.checkbox_ok);
        final CheckedTextView expired = (CheckedTextView)findViewById(R.id.checkbox_expired);
        final Spinner dateSpinner = (Spinner)findViewById(R.id.sorting_date_spinner);

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

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.sorting_order, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(adapter2);
        dateSpinner.setSelection(sorting.date.ordinal());

        popUpSortOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int date = dateSpinner.getSelectedItemPosition();

                if (date == SortingInfo.Type.Ascendant.ordinal()) {
                    sorting.date = SortingInfo.Type.Ascendant;
                } else {
                    sorting.date = SortingInfo.Type.Descendant;
                }

                closePopUpSort();

                searchTitleFragment.onResume();
                searchContentFragment.onResume();
            }
        });

        popUpSortParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int date = dateSpinner.getSelectedItemPosition();

                if (date == SortingInfo.Type.Ascendant.ordinal()) {
                    sorting.date = SortingInfo.Type.Ascendant;
                } else {
                    sorting.date = SortingInfo.Type.Descendant;
                }

                closePopUpSort();

                searchTitleFragment.onResume();
                searchContentFragment.onResume();
            }
        });
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

    /**
     * Create the ViewPager fragments
     */
    private void createFragments() {
        searchTitleFragment = new TodoListFragment();
        Bundle args1 = new Bundle();
        args1.putString(TodoListFragment.EXTRA_MODE, String.valueOf(TodoListFragment.Mode.Title));
        searchTitleFragment.setArguments(args1);

        searchContentFragment = new TodoListFragment();
        Bundle args2 = new Bundle();
        args2.putString(TodoListFragment.EXTRA_MODE, String.valueOf(TodoListFragment.Mode.Content));
        searchContentFragment.setArguments(args2);
    }

    /**
     * Init and Setup the ViewPager
     * @param viewPager The ViewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        SearchViewPagerAdapter adapter = new SearchViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(searchTitleFragment, getString(R.string.search_by_title));
        adapter.addFragment(searchContentFragment, getString(R.string.search_by_content));

        viewPager.setAdapter(adapter);
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

    }

    @Override
    public boolean isSelected(TodoItemInfo item) {
        return false;
    }

    @Override
    public void addSelection(TodoItemInfo item) {

    }

    @Override
    public void deleteSelection(TodoItemInfo item) {

    }

    @Override
    public boolean isInSelectionMode() {
        return false;
    }

    /**
     * Return the current filter
     * @return The filter
     */
    @Override
    public TodoItemFilter getFilter() {
        return (filter);
    }

    /**
     * Return the sorting information
     * @return The sorting information
     */
    @Override
    public SortingInfo getSortingInfo() {
        return (sorting);
    }

    /**
     * Get the item storred in the database order by the date
     * @param date
     * @return
     */
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

    private void openPopUpSort() {
        popUpSortParent.setVisibility(View.VISIBLE);
        popUpSort.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        popUpSortParent.startAnimation(popUpBgAnimationIn);
        popUpSort.startAnimation(animation);
    }

    private void closePopUpSort() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                popUpSortParent.startAnimation(popUpBgAnimationOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        popUpSort.post(new Runnable() {
            @Override
            public void run() {
                popUpSort.setVisibility(View.GONE);
            }
        });
        popUpSort.startAnimation(animation);
    }
}
