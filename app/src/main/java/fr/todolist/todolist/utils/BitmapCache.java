package fr.todolist.todolist.utils;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

/**
 * Created by Stephane on 26/01/2017.
 */

/**
 * Manage the bitmap of the application with a LRU Cache
 */
public class BitmapCache {

    private static final float MAX_RATIO = .40f;
    private static final int MAX_SIZE = getMaxSize(MAX_RATIO);
    private static final LruCache<String, Bitmap> CACHE = new LruCache<String, Bitmap>(MAX_SIZE) {
        @Override
        protected int sizeOf(String string, Bitmap bitmap) {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    };

    public static int getMaxSize(float maxRatio) {
        long maxMemory = Runtime.getRuntime().maxMemory();
        long longSize = (long) (maxMemory * maxRatio);
        return (int) Math.min((long) Integer.MAX_VALUE, longSize);
    }

    /**
     * Delete the cache
     */
    public static void deleteAllCache() {
        synchronized (CACHE) {
            CACHE.evictAll();
        }
    }

    /**
     * Delete element in a cache
     * @param key The key corresponding of the item to delete
     */
    public static void deleteInCache(String key) {
        synchronized (CACHE) {
            CACHE.remove(key);
        }
    }

    /**
     * Add a new element in the cache
     * @param key The key
     * @param value The bitmap
     */
    public static void putInCache(String key, Bitmap value) {
        synchronized (CACHE) {
            CACHE.put(key, value);
        }
    }

    /**
     * Get an element storred in the cache
     * @param key The key
     * @return The bitmap found
     */
    @Nullable
    public static Bitmap getInCache(String key) {
        synchronized (CACHE) {
            return CACHE.get(key);
        }
    }

}
