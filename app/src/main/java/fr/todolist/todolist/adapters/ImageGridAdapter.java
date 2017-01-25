package fr.todolist.todolist.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import fr.todolist.todolist.R;
import fr.todolist.todolist.interfaces.ImageGridInterface;
import fr.todolist.todolist.utils.StaticTools;

/**
 * Created by Stephane on 25/01/2017.
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
            String item = getItem(index);
            view.setFocusable(false);
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

    private void refreshPhotos(View view, String photo) {
        ImageView imageView = (ImageView) view.findViewById(R.id.preview_image);
        ImageView delete = (ImageView) view.findViewById(R.id.preview_delete);
        imageView.setVisibility(View.GONE);
        delete.setTag(photo);
        final int size = activity.getResources().getDimensionPixelSize(R.dimen.photo_size);

        view.setPadding(0, 0, 0, (int) activity.getResources().getDimension(R.dimen.photo_margin));

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photo, opts);
        int scale = (int) Math.min(
                Math.max(0, Math.ceil(opts.outWidth / size)),
                Math.max(0, Math.ceil(opts.outHeight / size))
        );
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = scale;
        Bitmap maxi = BitmapFactory.decodeFile(photo, opts);
        Bitmap mini = ThumbnailUtils.extractThumbnail(maxi, size, size);
        maxi.recycle();
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(mini);
        imageView.setTag(photo);

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
