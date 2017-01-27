package fr.todolist.todolist.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.todolist.todolist.R;
import fr.todolist.todolist.asynctasks.ImageDiskAsyncTask;
import fr.todolist.todolist.interfaces.ImageDiskAsyncTaskInterface;
import fr.todolist.todolist.interfaces.ImageGridInterface;
import fr.todolist.todolist.utils.BitmapCache;
import fr.todolist.todolist.utils.StaticTools;

/**
 * Created by Stephane on 25/01/2017.
 */

/**
 * Manage the illustrations in the GridView
 */
public class ImageGridAdapter extends BaseAdapter {

    private final static int PHOTO_MAX_COUNT = 30;

    private Activity activity;
    private ImageGridInterface listener;
    private List<String> data;

    public ImageGridAdapter(Activity activity, ArrayList<String> photos, ImageGridInterface listener) {
        this.activity = activity;
        this.listener = listener;

        data = photos;
    }

    /**
     * Set the datas
     * @param data An array corresponding to the path of the photos
     */
    public void setAll(ArrayList<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size() + 1;
    }

    @Override
    public String getItem(int index) {
        if (index < data.size())
            return data.get(index);
        return "button";
    }

    @Override
    public long getItemId(int index) {
        if (index < data.size())
            return index;
        return -1;
    }

    @Override
    public View getView(int index, View view, ViewGroup parent) {
        if (index < data.size()) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.preview_image, null);
            final String item = getItem(index);
            view.setFocusable(false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
            refreshPhotos(view, item);
        } else {
            ImageButton button = new ImageButton(activity);
            int x = (int)StaticTools.dpToPx(activity, 100);
            int y = (int)StaticTools.dpToPx(activity, 100);
            AbsListView.LayoutParams layoutParamsBg = new AbsListView.LayoutParams(x, y);
            button.setLayoutParams(layoutParamsBg);
            button.setBackgroundResource(R.drawable.add_picture);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onAddButtonClick();
                    }
                }
            });
            if (data.size() > PHOTO_MAX_COUNT - 1)
                button.setVisibility(View.GONE);
            return button;
        }
        return view;
    }

    private void refreshPhotos(final View view, final String photo) {
        final ImageView imageView = (ImageView) view.findViewById(R.id.preview_image);
        final ImageView delete = (ImageView) view.findViewById(R.id.preview_delete);
        delete.setTag(photo);
        Bitmap bitmap = BitmapCache.getInCache(photo);
        if (bitmap == null) {
            File exist = new File(photo);
            if (exist.exists()) {
                imageView.setImageResource(R.drawable.placeholder);
                new ImageDiskAsyncTask(activity, photo, new ImageDiskAsyncTaskInterface() {
                    @Override
                    public void onFinish(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                        imageView.setTag(photo);
                        BitmapCache.putInCache(photo, bitmap);
                    }
                }).execute();

            } else {
                imageView.setBackgroundColor(ContextCompat.getColor(activity, R.color.black100));
            }
        } else {
            imageView.setImageBitmap(bitmap);
        }
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todelete = (String) v.getTag();
                if (listener != null)
                    listener.onDeleteClick(todelete);
            }
        });
    }
}
