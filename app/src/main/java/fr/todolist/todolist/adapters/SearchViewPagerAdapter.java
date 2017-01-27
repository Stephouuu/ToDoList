package fr.todolist.todolist.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stephane on 19/01/2017.
 */

/**
 * Manage the view pager of the search activity
 */
public class SearchViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();

    /**
     * Public constructor
     * @param manager The fragment manager
     */
    public SearchViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    /**
     * Get the fragment corresponding to the position
     * @param position The position
     * @return The fragment
     */
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    /**
     * Add a new fragment in the adapter list
     * @param fragment The fragment
     * @param title The title of the view pager
     */
    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }
}
