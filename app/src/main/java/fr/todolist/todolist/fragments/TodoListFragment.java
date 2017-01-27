package fr.todolist.todolist.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import fr.todolist.todolist.R;
import fr.todolist.todolist.activities.MainActivity;
import fr.todolist.todolist.activities.SearchActivity;
import fr.todolist.todolist.adapters.TodoListRecyclerAdapter;
import fr.todolist.todolist.interfaces.SearchInterface;
import fr.todolist.todolist.interfaces.TodoListInterface;
import fr.todolist.todolist.utils.HidingScrollListener;
import fr.todolist.todolist.utils.SortingInfo;
import fr.todolist.todolist.utils.StaticTools;
import fr.todolist.todolist.utils.TodoItemFilter;
import fr.todolist.todolist.utils.TodoItemInfo;

/**
 * Manage the main to do list fragment
 */
public class TodoListFragment extends Fragment {

    public static final String EXTRA_MODE = "todolist.mode";
    public static final String EXTRA_SEARCH = "todolist.search.param";

    /**
     * Mode of the TodoList
     */
    public enum Mode {
        DueDate,
        Title,
        Content
    }

    private RecyclerView recyclerView;
    private TodoListRecyclerAdapter adapter;
    private LinearLayout noItemParent;
    private ImageView noItemDrawable;
    private Mode mode;
    private String searchParameter;
    private boolean retractableToolbar;

    @Override
    public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        Bundle args = getArguments();
        if (args != null) {
            mode = Mode.valueOf(args.getString(EXTRA_MODE, String.valueOf(Mode.DueDate)));
            searchParameter = args.getString(EXTRA_SEARCH);
        } else {
            mode = Mode.DueDate;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_list_fragment, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.todo_recyclerview);
        noItemParent = (LinearLayout)view.findViewById(R.id.todo_fragment_parent_noitem);
        noItemDrawable = (ImageView)view.findViewById(R.id.check_list);

        retractableToolbar = getActivity() instanceof MainActivity || getActivity() instanceof SearchActivity;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TodoListRecyclerAdapter(getActivity(), retractableToolbar, new TodoListInterface() {
            @Override
            public void onItemClick(TodoItemInfo item) {
                ((TodoListInterface) getActivity()).onItemClick(item);
            }

            @Override
            public void onItemLongClick(View view) {
                ((TodoListInterface) getActivity()).onItemLongClick(view);
            }

            @Override
            public boolean isSelected(TodoItemInfo item) {
                return ((TodoListInterface) getActivity()).isSelected(item);
            }

            @Override
            public void addSelection(TodoItemInfo item) {
                ((TodoListInterface) getActivity()).addSelection(item);
            }

            @Override
            public void deleteSelection(TodoItemInfo item) {
                ((TodoListInterface) getActivity()).deleteSelection(item);
            }

            @Override
            public boolean isInSelectionMode() {
                return ((TodoListInterface) getActivity()).isInSelectionMode();
            }
        });

        recyclerView.setAdapter(adapter);
        if (retractableToolbar) {
            recyclerView.addOnScrollListener(new HidingScrollListener() {
                @Override
                public void onHide() {
                    if (getActivity() instanceof  MainActivity) {

                        AppBarLayout toolbar = (AppBarLayout) getActivity().findViewById(R.id.appBarLayout);
                        RelativeLayout fab = (RelativeLayout) getActivity().findViewById(R.id.main_fab_parent);

                        toolbar.animate().setDuration(200).translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));

                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fab.getLayoutParams();
                        int fabBottomMargin = lp.bottomMargin;
                        fab.animate().translationY(fab.getHeight() + fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();

                    } else {
                        AppBarLayout toolbar = (AppBarLayout)getActivity().findViewById(R.id.appBarLayout);
                        FloatingActionButton fab = (FloatingActionButton)getActivity().findViewById(R.id.search_sort_fab);

                        toolbar.animate().setDuration(200).translationY(-toolbar.getHeight() / 2).setInterpolator(new AccelerateInterpolator(2));

                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) fab.getLayoutParams();
                        int fabBottomMargin = lp.bottomMargin;
                        fab.animate().translationY(fab.getHeight() + fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
                    }
                }

                @Override
                public void onShow() {
                    if (getActivity() instanceof MainActivity) {
                        AppBarLayout toolbar = (AppBarLayout) getActivity().findViewById(R.id.appBarLayout);
                        RelativeLayout fab = (RelativeLayout) getActivity().findViewById(R.id.main_fab_parent);

                        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
                        fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                    } else {
                        AppBarLayout toolbar = (AppBarLayout) getActivity().findViewById(R.id.appBarLayout);
                        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.search_sort_fab);

                        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
                        fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                    }
                }
            });
        }

        if (getActivity() instanceof SearchActivity) {
            noItemParent.setVisibility(View.GONE);
        }

        return (view);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    /**
     * Set the search parameter for the title and the content
     * @param parameter
     */
    public void setSearchParameter(String parameter) {
        searchParameter = parameter;
    }

    /**
     * Refresh the to do list
     */
    public void refreshList() {
        List<TodoItemInfo> list = getListItem();
        TodoItemFilter filter = ((SearchInterface)getActivity()).getFilter();
        if (filter != null) {
            list = StaticTools.applyFilter(list, filter);
        }
        if (list != null) {
            if (list.isEmpty()) {
                noItemParent.setVisibility(View.VISIBLE);
            } else {
                noItemParent.setVisibility(View.GONE);
            }
            adapter.clear();
            adapter.addList(list);
        }
    }

    @Nullable
    private List<TodoItemInfo> getListItem() {
        SortingInfo sorting = ((SearchInterface) getActivity()).getSortingInfo();
        List<TodoItemInfo> list = null;
        switch (mode) {
            case DueDate:
                list = ((SearchInterface) getActivity()).getItemsByDueDate(sorting.date);
                break;
            case Title:
                list = ((SearchInterface) getActivity()).getItemsByTitle(searchParameter, sorting.date);
                break;
            case Content:
                list = ((SearchInterface) getActivity()).getItemsByContent(searchParameter, sorting.date);
                break;
        }
        return (list);
    }

}
