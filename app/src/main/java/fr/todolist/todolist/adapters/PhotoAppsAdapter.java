package fr.todolist.todolist.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import fr.todolist.todolist.R;

/**
 * Manage the choice when the user want to add a photo
 */
public class PhotoAppsAdapter extends BaseAdapter {

    /**
     * Contexte de l'activité
     */
    private final Context context;
    private final int captureCode;
    private final int importCode;

    private final File captureFile;
    private final Intent captureIntent;
    private final Intent importIntent;

    private final List<ResolveInfo> importList;
    private final PackageManager packageManager;
    private int page;

    public PhotoAppsAdapter(Context context, File captureFile, int captureCode, int importCode) {
        page = 0;
        this.context = context;
        this.captureFile = captureFile;
        this.captureCode = captureCode;
        this.importCode = importCode;
        packageManager = context.getPackageManager();

        captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(captureFile));

        importIntent = new Intent(Intent.ACTION_GET_CONTENT);
        importIntent.setType("image/jpg");
        importList = packageManager.queryIntentActivities(importIntent, PackageManager.MATCH_DEFAULT_ONLY);
    }

    @Override
    public int getCount() {
        if (page == 0) {
            return 2;
        } else if (page == 1) {
            return importList.size();
        }
        return 0;
    }

    @Override
    @Nullable
    public String getItem(int itemIndex) {
        if (page == 0) {
            if (itemIndex == 0) {
                return context.getString(R.string.get_photo);
            }
            return context.getString(R.string.get_galerie);
        } else if (page == 1) {
            return importList.get(itemIndex).loadLabel(packageManager).toString();
        }
        return null;
    }

    @Nullable
    public Intent getIntent(int itemIndex) {
        if (page == 0) {
            if (itemIndex == 0) {
                return captureIntent;
            }
        } else if (page == 1) {
            ResolveInfo info = importList.get(itemIndex);
            String packageName = info.activityInfo.applicationInfo.packageName;
            return importIntent.setPackage(packageName);
        }
        return null;
    }

    @Override
    public long getItemId(int itemIndex) {
        return itemIndex;
    }

    public int getRequestCode(int itemIndex) {
        if (page == 0) {
            if (itemIndex == 0) {
                return captureCode;
            }
        } else if (page == 1) {
            return importCode;
        }
        return 0;
    }

    public Drawable getIcon(int itemIndex) {
        if (page == 1) {
            return importList.get(itemIndex).loadIcon(packageManager);
        }
        return null;
    }

    public void setPage(int page) {
        this.page = page;
        notifyDataSetChanged();
    }

    public void nextPage() {
        this.page++;
        notifyDataSetChanged();
    }

    public void previousPage() {
        this.page--;
        notifyDataSetChanged();
    }

    public int getPage() {
        return this.page;
    }

    @Override
    @NonNull
    public View getView(int itemIndex, View itemView, ViewGroup parentView) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.listitem_image, parentView, false);
        }
        ImageView itemImage = (ImageView) itemView.findViewById(R.id.listitem_image);
        itemImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        if (page == 1)
            itemImage.setImageDrawable(getIcon(itemIndex));
        else if (page == 0){
            if (itemIndex == 0) {
                itemImage.setImageResource(R.mipmap.ic_camera);
            }
            else if (itemIndex == 1) {
                itemImage.setImageResource(R.mipmap.ic_photo);
            }
        } else if (page == 2) {
                itemImage.setImageDrawable(getIcon(itemIndex));
        }
        TextView itemLabel = (TextView) itemView.findViewById(R.id.listitem_label);
        itemLabel.setText(getItem(itemIndex));
        itemLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary100));
        itemLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        return itemView;
    }
}

