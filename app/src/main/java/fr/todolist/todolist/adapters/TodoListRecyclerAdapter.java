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
import fr.todolist.todolist.activities.SearchActivity;
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

    private List<TodoItemInfo> listOverdue;
    private List<TodoItemInfo> listDone;
    private List<TodoItemInfo> listTodo;
    private List<List<TodoItemInfo>> listInProgress;

    private Activity activity;
    private boolean header;
    private TodoListInterface listener;

    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;

    public TodoListRecyclerAdapter(Activity activity, boolean header, TodoListInterface listener) {
        this.listOverdue = new ArrayList<>();
        this.listDone = new ArrayList<>();
        this.listTodo = new ArrayList<>();
        this.listInProgress = new ArrayList<>();

        this.activity = activity;
        this.header = header;
        this.listener = listener;
    }

    public void clear() {
        this.listOverdue.clear();
        this.listDone.clear();
        this.listTodo.clear();
        for (List<TodoItemInfo> items : listInProgress) {
            items.clear();
        }
        listInProgress.clear();
    }

    public void addList(List<TodoItemInfo> items) {
        clear();
        List<TodoItemInfo> currList = new ArrayList<>();
        String prevDate = "null";
        String currDate = "null";

        for (TodoItemInfo item : items) {
            if (item.status == TodoItemInfo.Status.Overdue) {
                listOverdue.add(item);
            } else if (item.status == TodoItemInfo.Status.Done) {
                listDone.add(item);
            } else {
                listTodo.add(item);
                currDate = DateTimeManager.getDay(item.day) + " " + DateTimeManager.getMonth(item.month) + " " + DateTimeManager.getYear(item.year);

                if (currList.size() > 0 && !currDate.equals(prevDate)) {
                    listInProgress.add(currList);
                    currList = new ArrayList<>();
                    currList.add(item);
                } else {
                    currList.add(item);
                }
                prevDate = currDate;
            }
        }
        if (currList.size() > 0) {
            listInProgress.add(currList);
        }
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
                int top = 0;
                if (activity instanceof SearchActivity) {
                    top = (int) StaticTools.dpToPx(activity, 47.f + 0.f);
                }

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
            TodoItemInfo item = getItem(position);

            ((TodoListRecyclerItemViewHolder)holder).refreshView(item);
            ((TodoListRecyclerItemViewHolder)holder).refreshTitle(item.title);
            ((TodoListRecyclerItemViewHolder)holder).refreshDate(item);
            ((TodoListRecyclerItemViewHolder)holder).refreshCategory(item, isFirstStatus(item), isFirstDate(item));
            ((TodoListRecyclerItemViewHolder)holder).refreshImage(item);

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

    private TodoItemInfo getItem(int index) {
        TodoItemInfo item = null;

        if (index < listOverdue.size()) {
            item = listOverdue.get(index);
        } else if (index - listOverdue.size() < listDone.size()) {
            item = listDone.get(index - listOverdue.size());
        } else if (index - (listOverdue.size() + listDone.size()) < listTodo.size()) {
            item = listTodo.get(index - (listOverdue.size() + listDone.size()));
        }

        return (item);
    }

    private boolean isFirstStatus(TodoItemInfo item) {
        boolean ret = false;

        if (listOverdue.size() > 0) {
            ret = item == listOverdue.get(0);
        }
        if (listTodo.size() > 0) {
            ret = ret || item == listTodo.get(0);
        }
        if (listDone.size() > 0) {
            ret = ret || item == listDone.get(0);
        }
        return (ret);
    }

    private boolean isFirstDate(TodoItemInfo item) {
        for (List<TodoItemInfo> items : listInProgress) {
            if (items.size() > 0 && items.get(0) == item) {
                return (true);
            }
        }
        return (false);
    }

    public int getBasicItemCount() {
        return (listTodo.size() + listOverdue.size() + listDone.size());
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
}
