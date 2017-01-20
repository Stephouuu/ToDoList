package fr.todolist.todolist.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fr.todolist.todolist.R;
import fr.todolist.todolist.activities.MainActivity;
import fr.todolist.todolist.adapters.TodoListRecyclerAdapter;
import fr.todolist.todolist.interfaces.SearchInterface;
import fr.todolist.todolist.interfaces.TodoListInterface;
import fr.todolist.todolist.utils.TodoItemInfo;

public class TodoListFragment extends Fragment {

    public static final String EXTRA_MODE = "todolist.mode";
    public static final String EXTRA_SEARCH = "todolist.search.param";

    public enum Mode {
        DueDateASC,
        DueDateDESC,
        Title,
        Status,
        Content
    }

    //private ListView list;
    //private TodoListAdapter adapter;
    private RecyclerView recyclerView;
    private TodoListRecyclerAdapter adapter;
    private TextView noItemTextView;
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
            mode = Mode.valueOf(args.getString(EXTRA_MODE, String.valueOf(Mode.DueDateASC)));
            searchParameter = args.getString(EXTRA_SEARCH);
        } else {
            mode = Mode.DueDateASC;
        }
        Log.i("mode onCreate", "" + mode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_list_fragment, container, false);

        //list = (ListView)view.findViewById(R.id.todo_fragment_list);
        recyclerView = (RecyclerView)view.findViewById(R.id.todo_recyclerview);
        noItemTextView = (TextView)view.findViewById(R.id.todo_fragment_noitem);

        /*adapter = new TodoListAdapter(getContext(), new TodoListInterface() {
            @Override
            public void onItemClick(TodoItemInfo item) {
                ((TodoListInterface) getActivity()).onItemClick(item);
            }

            @Override
            public void onItemLongClick(View view) {
                ((TodoListInterface) getActivity()).onItemLongClick(view);
            }
        });
        list.setAdapter(adapter);*/
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

        return (view);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
        if (mode != Mode.DueDateASC) {
            noItemTextView.setVisibility(View.GONE);
        }
    }

    public void setSearchParameter(String parameter) {
        searchParameter = parameter;
    }

    public void refreshList() {
        List<TodoItemInfo> list = getListItem();
        if (list != null) {
            if (list.isEmpty()) {
                noItemTextView.setVisibility(View.VISIBLE);
            } else {
                noItemTextView.setVisibility(View.GONE);
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
