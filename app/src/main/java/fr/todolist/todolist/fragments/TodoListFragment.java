package fr.todolist.todolist.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import fr.todolist.todolist.R;
import fr.todolist.todolist.adapters.TodoListAdapter;
import fr.todolist.todolist.database.TodoItemDatabase;
import fr.todolist.todolist.interfaces.HomePageInterface;
import fr.todolist.todolist.interfaces.TodoListInterface;
import fr.todolist.todolist.utils.TodoItemInfo;

public class TodoListFragment extends Fragment {

    private FloatingActionButton addFab;
    private ListView list;
    private TodoListAdapter adapter;
    private TextView noItemTextView;

    private TodoItemDatabase database;

    public TodoListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_list_fragment, container, false);

        database = new TodoItemDatabase(getContext());
        database.open();

        addFab = (FloatingActionButton)view.findViewById(R.id.todo_fragment_fab);
        list = (ListView)view.findViewById(R.id.todo_fragment_list);
        noItemTextView = (TextView)view.findViewById(R.id.todo_fragment_noitem);

        adapter = new TodoListAdapter(getContext(), new TodoListInterface() {
            @Override
            public void onItemClick(TodoItemInfo item) {
                ((TodoListInterface) getActivity()).onItemClick(item);
            }
        });
        list.setAdapter(adapter);

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomePageInterface) getActivity()).addItem();
            }
        });

        return (view);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        List<TodoItemInfo> list = database.getItemsOrderByDueDate();
        if (list.isEmpty()) {
            noItemTextView.setVisibility(View.VISIBLE);
        } else {
            noItemTextView.setVisibility(View.GONE);
        }
        adapter.clear();
        adapter.addList(list);
    }

}
