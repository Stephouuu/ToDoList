package fr.todolist.todolist.recyclers;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import fr.todolist.todolist.R;
import fr.todolist.todolist.interfaces.TodoListInterface;
import fr.todolist.todolist.utils.DateTimeManager;
import fr.todolist.todolist.utils.TodoItemInfo;

/**
 * Created by Stephane on 20/01/2017.
 */

public class TodoListRecyclerItemViewHolder extends RecyclerView.ViewHolder {

    //static private TodoItemInfo.Status lastStatus = TodoItemInfo.Status.None;

    private Activity activity;
    private View parent;
    private TodoListInterface listener;

    private TodoListRecyclerItemViewHolder(Activity activity, View parent, TodoListInterface listener) {
        super(parent);
        this.activity = activity;
        this.parent = parent;
        this.listener = listener;

        //init();
    }

    public static TodoListRecyclerItemViewHolder newInstance(Activity activity, View parent, TodoListInterface listener) {
        return (new TodoListRecyclerItemViewHolder(activity, parent, listener));
    }

    static public void reset() {
        //lastDate = "null";
        //lastStatus = TodoItemInfo.Status.None;
    }

    /*static public void init() {
        lastStatus = TodoItemInfo.Status.None;
    }*/

    public void refreshView(final TodoItemInfo item) {
        final CheckBox selectCheckBox = (CheckBox)parent.findViewById(R.id.todo_item_checkbox);

        selectCheckBox.setChecked(listener.isSelected(item));

        if (listener.isInSelectionMode()) {
            parent.findViewById(R.id.todo_item_checkbox_parent).setVisibility(View.VISIBLE);
        } else {
            parent.findViewById(R.id.todo_item_checkbox_parent).setVisibility(View.GONE);
        }

        selectCheckBox.setTag(true);
        selectCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = (boolean)selectCheckBox.getTag();
                if (isChecked) {
                    listener.addSelection(item);
                } else {
                    listener.deleteSelection(item);
                }
                selectCheckBox.setTag(!isChecked);
            }
        });

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener.isInSelectionMode()) {
                    selectCheckBox.performClick();
                }
                else {
                    listener.onItemClick(item);
                }
            }
        });

        parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //selectedItem = item.id;
                //selectCheckBox.performClick();
                listener.onItemLongClick(v);
                //selectCheckBox.setTag(true);
                return true;
            }
        });
    }

    //public String refreshCategory(TodoItemInfo item, boolean isFirst, String lastDateCategory) {
    public void refreshCategory(TodoItemInfo item, boolean isFirstStatus, boolean isFirstDate) {
        TextView textView = (TextView) parent.findViewById(R.id.todo_preview_category);
        String text = DateTimeManager.getDay(item.day) + " " + DateTimeManager.getMonth(item.month)
                + " " + DateTimeManager.getYear(item.year);

        if (isFirstStatus) {
            textView.setVisibility(View.VISIBLE);

            if (item.status == TodoItemInfo.Status.Overdue) {
                text = activity.getString(R.string.expired);
                textView.setTextColor(ContextCompat.getColor(activity, R.color.red));
            } else if (item.status == TodoItemInfo.Status.Done) {
                text = activity.getString(R.string.done);
                textView.setTextColor(ContextCompat.getColor(activity, R.color.green));
            } else {
                if (DateTimeManager.isToday(item.year, item.month, item.day)) {
                    text = activity.getString(R.string.today);
                } else if (DateTimeManager.isTomorrow(item.year, item.month, item.day)) {
                    text = activity.getString(R.string.tomorrow);
                }
                textView.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary100));
            }
            textView.setText(text);
        } else if (isFirstDate) {
            textView.setVisibility(View.VISIBLE);
            if (DateTimeManager.isToday(item.year, item.month, item.day)) {
                text = activity.getString(R.string.today);
            } else if (DateTimeManager.isTomorrow(item.year, item.month, item.day)) {
                text = activity.getString(R.string.tomorrow);
            }
            textView.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary100));
            textView.setText(text);
        } else {
            textView.setVisibility(View.GONE);
        }

        /*if (lastStatus != TodoItemInfo.Status.ToDo && (item.status == TodoItemInfo.Status.Overdue || item.status == TodoItemInfo.Status.Done)) {
            if (isFirst) {
                lastStatus = item.status;
                textView.setVisibility(View.VISIBLE);
                textView.setText(text);
            } else {
                textView.setVisibility(View.GONE);
            }
        } else if (!lastDateCategory.equals(text)) {
            lastDateCategory = text;
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        } else {
            textView.setVisibility(View.GONE);
        }

        return lastDateCategory;*/
    }

    public void refreshTitle(String title) {
        ((TextView)parent.findViewById(R.id.todo_item_title)).setText(title);
    }

    public void refreshDate(TodoItemInfo item) {
        TextView dateTextView = (TextView)parent.findViewById(R.id.todo_item_datetime);
        String date = DateTimeManager.getUserFriendlyDateTime(activity, item.dateTime, item.year,
                item.month, item.day, item.hour, item.minute);

        dateTextView.setText(date);
        if (item.status == TodoItemInfo.Status.ToDo) {
            dateTextView.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary100));
        } else {
            if (item.status == TodoItemInfo.Status.Done) {
                dateTextView.setTextColor(ContextCompat.getColor(activity, R.color.green));
            } else if (item.status == TodoItemInfo.Status.Overdue) {
                dateTextView.setTextColor(ContextCompat.getColor(activity, R.color.red));
            }
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
        if (parent.findViewById(R.id.todo_preview_category).getVisibility() == View.GONE) {
            parent.findViewById(R.id.todo_item_divider).setVisibility(View.VISIBLE);
        } else {
            parent.findViewById(R.id.todo_item_divider).setVisibility(View.GONE);
        }
        parent.findViewById(R.id.footer).setVisibility(View.VISIBLE);
    }

    public void no_rounded() {
        parent.findViewById(R.id.todo_preview_parent).setBackgroundColor(Color.parseColor("#FFFFFF"));
        if (parent.findViewById(R.id.todo_preview_category).getVisibility() == View.GONE) {
            parent.findViewById(R.id.todo_item_divider).setVisibility(View.VISIBLE);
        } else {
            parent.findViewById(R.id.todo_item_divider).setVisibility(View.GONE);
        }
        parent.findViewById(R.id.footer).setVisibility(View.GONE);
    }
}
