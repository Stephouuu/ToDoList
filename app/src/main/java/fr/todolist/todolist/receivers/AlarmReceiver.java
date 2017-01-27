package fr.todolist.todolist.receivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.concurrent.atomic.AtomicInteger;

import fr.todolist.todolist.R;
import fr.todolist.todolist.activities.SplashScreenActivity;
import fr.todolist.todolist.database.AppDatabase;
import fr.todolist.todolist.utils.AlertInfo;
import fr.todolist.todolist.utils.DateTimeManager;
import fr.todolist.todolist.utils.TodoItemInfo;

/**
 * Created by Stephane on 17/01/2017.
 */

/**
 * Manage the alarm of the application
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    private final static String EXTRA_ALARM = "extra.receiver.alert";
    private final static String EXTRA_ITEM = "extra.receiver.item";

    @NonNull
    private static final AtomicInteger id = new AtomicInteger(0);

    /**
     * Receive the alarm information
     * @param context The context
     * @param intent The intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        AlertInfo alert = intent.getParcelableExtra(EXTRA_ALARM);
        TodoItemInfo item = intent.getParcelableExtra(EXTRA_ITEM);

        if (item != null) {
            deleteAlarm(context, (int)item.id);
            if (alert != null) {
                alarm(context, item, String.format(context.getString(R.string.alert_title), alert.title), alert.content);
            } else {
                alarm(context, item, "Wake up!", "You have to do something");
            }
        }
    }

    private void alarm(Context context, TodoItemInfo item, String title, String content) {
        AppDatabase database = new AppDatabase(context);
        database.open();

        if (item.remind) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                    .setSmallIcon(R.drawable.todo_icon)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setPriority(item.priority)
                    .setTicker(content);

            Intent intent = new Intent(context, SplashScreenActivity.class);
            intent.setData(Uri.parse("todolist://consultation/" + item.id));

            builder.setContentIntent(PendingIntent.getActivity(context, id.get(), intent, PendingIntent.FLAG_ONE_SHOT));
            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            manager.notify(id.incrementAndGet(), notification);

            if (item.nbRecurrence > 0) {
                item.nbRecurrence--;
                long ms = DateTimeManager.castDateTimeToUnixTime(item.dateTime) + (item.intervalRecurrence * (item.nbBaseRecurrence - item.nbRecurrence));
                item = DateTimeManager.retrieveDataTime(item, ms);
                addAlarm(context, item,  ms);
            } else {
                item.status = TodoItemInfo.Status.Overdue;
            }

        } else {
            item.status = TodoItemInfo.Status.Overdue;
        }
        database.updateItem(item);
    }

    /**
     * Add a new alarm
     * @param context The context
     * @param item The item
     * @param ms The delay
     */
    public static void addAlarm(Context context, TodoItemInfo item, long ms) {
        AlertInfo alert = new AlertInfo();
        alert.idTodoItem = (int)item.id;
        alert.title = item.title;
        alert.content = item.content;

        AppDatabase database = new AppDatabase(context);
        database.open();
        alert = database.insertItem(alert);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(EXTRA_ALARM, alert);
        intent.putExtra(EXTRA_ITEM, item);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, alert.id, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, ms, pIntent);
    }

    /**
     * Delete an alarm
     * @param context The context
     * @param idItem The id of the item
     */
    public static void deleteAlarm(Context context, int idItem) {
        AppDatabase database = new AppDatabase(context);
        database.open();

        AlertInfo alert = database.getAlertInfoByItemID(idItem);
        if (alert != null) {
            database.deleteAlert(alert.id);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, alert.id, intent, PendingIntent.FLAG_ONE_SHOT);
            alarmManager.cancel(pIntent);
        }
    }
}
