package fr.todolist.todolist.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fr.todolist.todolist.R;
import fr.todolist.todolist.interfaces.TodoListInterface;
import fr.todolist.todolist.recyclers.TodoListRecyclerHeaderViewHolder;
import fr.todolist.todolist.recyclers.TodoListRecyclerItemViewHolder;
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
    private String lastDateCategory;

    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;

    public TodoListRecyclerAdapter(Activity activity, boolean header, TodoListInterface listener) {
        this.list = new ArrayList<>();
        this.activity = activity;
        this.header = header;
        this.listener = listener;
        this.lastDateCategory = "null";
    }

    public void clear() {
        //TodoListRecyclerItemViewHolder.init();
        lastDateCategory = "null";
        TodoListRecyclerItemViewHolder.reset();
        this.list.clear();
    }

    public void addList(List<TodoItemInfo> items) {
        this.list.addAll(items);
        notifyDataSetChanged();
    }

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
            lastDateCategory = ((TodoListRecyclerItemViewHolder)holder).refreshCategory(item, isFirstOfThisStatus(item), lastDateCategory);
            ((TodoListRecyclerItemViewHolder)holder).refreshTitle(item.title);
            ((TodoListRecyclerItemViewHolder)holder).refreshDate(item);

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

    public int getBasicItemCount() {
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

    private boolean isFirstOfThisStatus(TodoItemInfo item) {
        for (TodoItemInfo it : list) {
            if (it.status == item.status) {
                return (it == item);
            }
        }
        return (false);
    }
}
