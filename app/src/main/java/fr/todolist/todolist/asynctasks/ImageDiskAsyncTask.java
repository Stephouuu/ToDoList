package fr.todolist.todolist.asynctasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import fr.todolist.todolist.R;
import fr.todolist.todolist.interfaces.ImageDiskAsyncTaskInterface;

/**
 * Created by Stephane on 26/01/2017.
 */

/**
 * Load an image in asynchronous
 */
public class ImageDiskAsyncTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private ImageDiskAsyncTaskInterface listener;
    private String image;
    private Bitmap bitmap;

    /**
     * Public constructor
     * @param context The context activity
     * @param image The path of the image to load
     * @param listener A listener
     */
    public ImageDiskAsyncTask(Context context, String image, ImageDiskAsyncTaskInterface listener) {
        this.context = context;
        this.image = image;
        this.listener = listener;
    }

    /**
     * Method called in other thread
     * @param params Void
     * @return Null
     */
    @Nullable
    @Override
    protected Void doInBackground(Void... params) {

        final int size = context.getResources().getDimensionPixelSize(R.dimen.photo_size);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image, opts);
        int scale = (int) Math.min(
                Math.max(0, Math.ceil(opts.outWidth / size)),
                Math.max(0, Math.ceil(opts.outHeight / size))
        );
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = scale;
        Bitmap maxi = BitmapFactory.decodeFile(image, opts);
        bitmap = ThumbnailUtils.extractThumbnail(maxi, size, size);
        maxi.recycle();

        return null;
    }

    /**
     * Execute this task in a threadpoll
     */
    public void execute() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Called on the main thread after doInBackground finished
     * @param result Void
     */
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        listener.onFinish(bitmap);
    }
}
