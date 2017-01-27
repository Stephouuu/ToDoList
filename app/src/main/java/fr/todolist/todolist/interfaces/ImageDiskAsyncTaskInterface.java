package fr.todolist.todolist.interfaces;

import android.graphics.Bitmap;

/**
 * Created by Stephane on 26/01/2017.
 */

/**
 * Image loader asynchronous interface
 */
public interface ImageDiskAsyncTaskInterface {

    /**
     * Called after the thread finished his work
     * @param bitmap The bitmap loaded
     */
    void onFinish(Bitmap bitmap);

}
