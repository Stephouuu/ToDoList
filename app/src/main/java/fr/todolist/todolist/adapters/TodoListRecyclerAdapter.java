package fr.todolist.todolist.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.todolist.todolist.R;
import fr.todolist.todolist.interfaces.TodoListInterface;
import fr.todolist.todolist.recyclers.TodoListRecyclerHeaderViewHolder;
import fr.todolist.todolist.recyclers.TodoListRecyclerItemViewHolder;
import fr.todolist.todolist.utils.DateTimeManager;
import fr.todolist.todolist.utils.StaticTools;
import fr.todolist.todolist.utils.TodoItemInfo;

/**
 * Created by Stephane on 16/01/2017.
 */

public class TodoListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TodoItemInfo> list;
    private Activity activity;
    private boolean header;
    private TodoListInterface listener;

    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;

    public TodoListRecyclerAdapter(Activity activity, boolean header, TodoListInterface listener) {
        this.list = new ArrayList<>();
        this.activity = activity;
        this.header = header;
        this.listener = listener;
    }

    public void clear() {
        this.list.clear();
    }

    public void addList(List<TodoItemInfo> items) {
        this.list.addAll(items);
        notifyDataSetChanged();
    }

    /*@Override
    public int getCount() {
        return (items.size());
    }

    @Override
    public TodoItemInfo getItem(int index) {
        return (items.get(index));
    }*/

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item_preview, parent, false);
            return (TodoListRecyclerItemViewHolder.newInstance(activity, view, listener));
        }
        else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item_header, parent, false);

            TypedValue tv = new TypedValue();
            if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                int toolbarSize = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
                int top = (int) StaticTools.dpToPx(activity, /*47.f +*/ 12.f);

                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams)view.getLayoutParams();
                lp.setMargins(0, toolbarSize + top, 0, 0);
                view.invalidate();
            }

            return (new TodoListRecyclerHeaderViewHolder(view));
        }
        return (null);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!isHeader(position)) {
            position -= ((header) ? 1 : 0);
            TodoItemInfo item = list.get(position);

            ((TodoListRecyclerItemViewHolder)holder).refreshView(item);
            ((TodoListRecyclerItemViewHolder)holder).refreshTitle(item.title);
            ((TodoListRecyclerItemViewHolder)holder).refreshContent(item.content);
            ((TodoListRecyclerItemViewHolder)holder).refreshDate(item);
            ((TodoListRecyclerItemViewHolder)holder).refreshStatus(item);

            if (position == 0 && position == getBasicItemCount() - 1) {
                ((TodoListRecyclerItemViewHolder)holder).rounded_border_top_bottom();
            } else  if (position == 0) {
                ((TodoListRecyclerItemViewHolder)holder).rounded_border_top();
            } else if (position == getBasicItemCount() - 1) {
                ((TodoListRecyclerItemViewHolder)holder).rounded_border_bottom();
            } else {
                ((TodoListRecyclerItemViewHolder)holder).no_rounded();
            }
        }
    }

    private int getBasicItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public int getItemCount() {
        return (getBasicItemCount() + (header?1:0));
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isHeader(int position) {
        return (position == 0 && header);
    }

    /*@Override
    public View getView(final int index, View old, ViewGroup parent) {
        TodoItemInfo item = getItem(index);

        View view = refreshView(index, old, parent, item);
        String date = DateTimeManager.getUserFriendlyDateTime(context, item.dateTime, item.year,
                item.month, item.day, item.hour, item.minute);

        ((TextView)view.findViewById(R.id.todo_item_title)).setText(item.title);
        ((TextView)view.findViewById(R.id.todo_item_content)).setText(item.content);

        if (item.status == TodoItemInfo.Status.InProgress) {
            ((TextView) view.findViewById(R.id.todo_item_datetime)).setText(date);
        }

        refreshStatus(view, item);

        return view;
    }*/

    /*private View refreshView(int index, View old, ViewGroup parent, final TodoItemInfo item) {
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

        if (items.size() == 1) {
            view.setBackgroundResource(R.drawable.rounded_border_top_bottom);
        } else if (index == 0) {
            view.setBackgroundResource(R.drawable.rounded_border_top);
        } else if (index == items.size() - 1) {
            view.setBackgroundResource(R.drawable.rounded_border_bottom);
        }

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
    }*/
}
