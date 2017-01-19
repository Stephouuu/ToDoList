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
import android.support.v4.util.LongSparseArray;

import java.util.concurrent.atomic.AtomicInteger;

import fr.todolist.todolist.R;
import fr.todolist.todolist.activities.MainActivity;
import fr.todolist.todolist.database.TodoItemDatabase;
import fr.todolist.todolist.utils.AlertInfo;
import fr.todolist.todolist.utils.TodoItemInfo;

/**
 * Created by Stephane on 17/01/2017.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {

    private final static String EXTRA_ALARM = "extra.receiver.alert";
    private final static String EXTRA_ITEM = "extra.receiver.item";

    private final static LongSparseArray<AlertInfo> Alerts = new LongSparseArray<>();

    @NonNull
    private static final AtomicInteger id = new AtomicInteger(0);

    @Override
    public void onReceive(Context context, Intent intent) {
        AlertInfo alert = intent.getParcelableExtra(EXTRA_ALARM);
        TodoItemInfo item = intent.getParcelableExtra(EXTRA_ITEM);
        if (item != null) {
            deleteAlarm(context, item.id);
        }

        if (alert != null) {
            alarm(context, item, String.format(context.getString(R.string.alert_title), alert.title), alert.content);
        } else {
            alarm(context, item, "Wake up!", "You have to do something");
        }
    }

    private void alarm(Context context, TodoItemInfo item, String title, String content) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setSmallIcon(R.drawable.todo_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(content);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setData(Uri.parse("doit://consultation/" + item.id));

        TodoItemDatabase database = new TodoItemDatabase(context);
        database.open();
        item.status = TodoItemInfo.Status.Expired;
        database.updateTodoItem(item);
        database.close();

        builder.setContentIntent(PendingIntent.getActivity(context, id.get(), intent, PendingIntent.FLAG_ONE_SHOT));
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(id.incrementAndGet(), notification);
    }

    public static void addAlarm(Context context, String title, String content, TodoItemInfo item, long ms) {
        AlertInfo alert = new AlertInfo();
        alert.title = title;
        alert.content = content;
        alert.time = ms;

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(EXTRA_ALARM, alert);
        intent.putExtra(EXTRA_ITEM, item);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, alert.id, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alert.time, pIntent);
        Alerts.put(item.id, alert);
    }

    public static void deleteAlarm(Context context, long key) {
        if (Alerts.get(key) != null) {
            AlertInfo alert = Alerts.get(key);
            Alerts.remove(key);

            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, alert.id, intent, PendingIntent.FLAG_ONE_SHOT);
            alarmManager.cancel(pIntent);
        }
    }
}
