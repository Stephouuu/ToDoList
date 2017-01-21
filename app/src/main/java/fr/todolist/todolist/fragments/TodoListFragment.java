package fr.todolist.todolist.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import fr.todolist.todolist.R;
import fr.todolist.todolist.activities.MainActivity;
import fr.todolist.todolist.adapters.TodoListRecyclerAdapter;
import fr.todolist.todolist.interfaces.SearchInterface;
import fr.todolist.todolist.interfaces.TodoListInterface;
import fr.todolist.todolist.utils.StaticTools;
import fr.todolist.todolist.utils.TodoItemFilter;
import fr.todolist.todolist.utils.TodoItemInfo;

public class TodoListFragment extends Fragment {

    public static final String EXTRA_MODE = "todolist.mode";
    public static final String EXTRA_SEARCH = "todolist.search.param";
    //public static final String EXTRA_FILTER = "todolist.filter";

    public enum Mode {
        DueDateASC,
        DueDateDESC,
        Title,
        Status,
        Content
    }

    private RecyclerView recyclerView;
    private TodoListRecyclerAdapter adapter;
    private LinearLayout noItemParent;
    private ImageView noItemDrawable;
    //private TodoItemFilter filter;
    private Mode mode;
    private String searchParameter;
    private boolean retractableToolbar;

    public TodoListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        Bundle args = getArguments();
        if (args != null) {
            //filter = args.getParcelable(EXTRA_FILTER);
            mode = Mode.valueOf(args.getString(EXTRA_MODE, String.valueOf(Mode.DueDateASC)));
            searchParameter = args.getString(EXTRA_SEARCH);
        } else {
            mode = Mode.DueDateASC;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_list_fragment, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.todo_recyclerview);
        noItemParent = (LinearLayout)view.findViewById(R.id.todo_fragment_parent_noitem);
        noItemDrawable = (ImageView)view.findViewById(R.id.check_list);

        retractableToolbar = getActivity() instanceof MainActivity;
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
        });
        recyclerView.setAdapter(adapter);

        if (!retractableToolbar) {
            noItemParent.setVisibility(View.GONE);
        }

        return (view);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    public void setSearchParameter(String parameter) {
        searchParameter = parameter;
    }

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
        List<TodoItemInfo> list = null;
        switch (mode) {
            case DueDateASC:
                list = ((SearchInterface) getActivity()).getItemsByDueDateASC();
                break;
            case DueDateDESC:
                list = ((SearchInterface) getActivity()).getItemsByDueDateDESC();
                break;
            case Title:
                list = ((SearchInterface) getActivity()).getItemsByTitle(searchParameter);
                break;
            case Status:
                list = ((SearchInterface) getActivity()).getItemsByStatus(TodoItemInfo.Status.valueOf(searchParameter));
                break;
            case Content:
                list = ((SearchInterface) getActivity()).getItemsByContent(searchParameter);
                break;
        }
        return (list);
    }

}
