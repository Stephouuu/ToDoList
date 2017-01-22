package fr.todolist.todolist.recyclers;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import fr.todolist.todolist.R;
import fr.todolist.todolist.interfaces.TodoListInterface;
import fr.todolist.todolist.utils.DateTimeManager;
import fr.todolist.todolist.utils.TodoItemInfo;

/**
 * Created by Stephane on 20/01/2017.
 */

public class TodoListRecyclerItemViewHolder extends RecyclerView.ViewHolder {

    private Activity activity;
    private View parent;
    private TodoListInterface listener;

    private TodoListRecyclerItemViewHolder(Activity activity, View parent, TodoListInterface listener) {
        super(parent);
        this.activity = activity;
        this.parent = parent;
        this.listener = listener;
    }

    public static TodoListRecyclerItemViewHolder newInstance(Activity activity, View parent, TodoListInterface listener) {
        return (new TodoListRecyclerItemViewHolder(activity, parent, listener));
    }

    public void refreshView(final TodoItemInfo item) {
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item);
            }
        });

        parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onItemLongClick(v);
                return true;
            }
        });
    }

    public void refreshTitle(String title) {
        ((TextView)parent.findViewById(R.id.todo_item_title)).setText(title);
    }

    public void refreshDate(TodoItemInfo item) {
        TextView dateTextView = (TextView)parent.findViewById(R.id.todo_item_datetime);
        String date = DateTimeManager.getUserFriendlyDateTime(activity, item.dateTime, item.year,
                item.month, item.day, item.hour, item.minute);

        if (item.status == TodoItemInfo.Status.ToDo) {
            dateTextView.setText(date);
            dateTextView.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary100));
        } else {
            if (item.status == TodoItemInfo.Status.Done) {
                dateTextView.setTextColor(ContextCompat.getColor(activity, R.color.green));
            } else if (item.status == TodoItemInfo.Status.Expired) {
                dateTextView.setTextColor(ContextCompat.getColor(activity, R.color.red));
            }
            dateTextView.setText(item.status.toString());
        }
    }

    public void rounded_border_top_bottom() {
        parent.findViewById(R.id.todo_preview_parent).setBackgroundResource(R.drawable.rounded_border_top_bottom);
        parent.findViewById(R.id.todo_item_divider).setVisibility(View.GONE);
        parent.findViewById(R.id.footer).setVisibility(View.VISIBLE);
    }

    public void rounded_border_top() {
        parent.findViewById(R.id.todo_preview_parent).setBackgroundResource(R.drawable.rounded_border_top);
        parent.findViewById(R.id.todo_item_divider).setVisibility(View.GONE);
        parent.findViewById(R.id.footer).setVisibility(View.GONE);
    }

    public void rounded_border_bottom() {
        parent.findViewById(R.id.todo_preview_parent).setBackgroundResource(R.drawable.rounded_border_bottom);
        parent.findViewById(R.id.todo_item_divider).setVisibility(View.VISIBLE);
        parent.findViewById(R.id.footer).setVisibility(View.VISIBLE);
    }

    public void no_rounded() {
        parent.findViewById(R.id.todo_preview_parent).setBackgroundColor(Color.parseColor("#FFFFFF"));
        parent.findViewById(R.id.todo_item_divider).setVisibility(View.VISIBLE);
        parent.findViewById(R.id.footer).setVisibility(View.GONE);
    }
}
