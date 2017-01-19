package fr.todolist.todolist.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.todolist.todolist.R;
import fr.todolist.todolist.interfaces.TodoListInterface;
import fr.todolist.todolist.utils.DateTimeManager;
import fr.todolist.todolist.utils.TodoItemInfo;

/**
 * Created by Stephane on 16/01/2017.
 */

public class TodoListAdapter extends BaseAdapter {

    private Context context;
    private List<TodoItemInfo> items;
    private TodoListInterface listener;

    public TodoListAdapter(Context context, TodoListInterface listener) {
        this.context = context;
        this.items = new ArrayList<>();
        this.listener = listener;
    }

    public void clear() {
        this.items.clear();
    }

    public void addList(List<TodoItemInfo> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (items.size());
    }

    @Override
    public TodoItemInfo getItem(int index) {
        return (items.get(index));
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(final int index, View old, ViewGroup parent) {
        TodoItemInfo item = getItem(index);

        View view = refreshView(old, parent, item);
        String date = DateTimeManager.getUserFriendlyDateTime(context, item.dateTime, item.year,
                item.month, item.day, item.hour, item.minute);

        ((TextView)view.findViewById(R.id.todo_item_title)).setText(item.title);
        ((TextView)view.findViewById(R.id.todo_item_content)).setText(item.content);

        if (item.status == TodoItemInfo.Status.InProgress) {
            ((TextView) view.findViewById(R.id.todo_item_datetime)).setText(date);
        }

        refreshStatus(view, item);

        return view;
    }

    private View refreshView(View old, ViewGroup parent, final TodoItemInfo item) {
        View view;

        if (old != null) {
            view = old;
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.todo_list_item, parent, false);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onItemLongClick(v);
                return true;
            }
        });

        return (view);
    }

    private void refreshStatus(View view, TodoItemInfo info) {
        TextView status = (TextView)view.findViewById(R.id.todo_item_status);
        String[] array = context.getResources().getStringArray(R.array.todo_status);

        if (info.status == TodoItemInfo.Status.Ok) {
            status.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else if (info.status == TodoItemInfo.Status.Expired) {
            status.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else if (info.status == TodoItemInfo.Status.InProgress) {
            status.setTextColor(ContextCompat.getColor(context, R.color.yellow));
        }

        status.setText(array[info.status.ordinal()]);
    }
}
