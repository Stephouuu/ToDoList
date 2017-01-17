package fr.todolist.todolist.receivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v4.util.LongSparseArray;

import java.util.concurrent.atomic.AtomicInteger;

import fr.todolist.todolist.R;
import fr.todolist.todolist.activities.MainActivity;
import fr.todolist.todolist.utils.AlertInfo;

/**
 * Created by Stephane on 17/01/2017.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {

    private final static String EXTRA_ALARM = "extra.receiver.alert";
    private final static String EXTRA_KEY = "extra.receiver.key";

    private final static LongSparseArray<AlertInfo> Alerts = new LongSparseArray<>();

    @NonNull
    private static final AtomicInteger id = new AtomicInteger(0);

    @Override
    public void onReceive(Context context, Intent intent) {
        AlertInfo alert = intent.getParcelableExtra(EXTRA_ALARM);
        Long key = intent.getLongExtra(EXTRA_KEY, -1);
        if (key > -1) {
            deleteAlarm(context, key);
        }

        if (alert != null) {
            alarm(context, String.format(context.getString(R.string.alert_title), alert.title), alert.content);
        } else {
            alarm(context, "Wake up!", "You have to do something");
        }
    }

    private void alarm(Context context, String title, String content) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setSmallIcon(R.drawable.todo_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setTicker(content);

        Intent intent = new Intent(context, MainActivity.class);

        builder.setContentIntent(PendingIntent.getActivity(context, id.get(), intent, PendingIntent.FLAG_ONE_SHOT));
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(id.incrementAndGet(), notification);
    }

    public static void addAlarm(Context context, String title, String content, long key, long ms) {
        AlertInfo alert = new AlertInfo();
        alert.title = title;
        alert.content = content;
        alert.time = ms;

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(EXTRA_ALARM, alert);
        intent.putExtra(EXTRA_KEY, key);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, alert.id, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alert.time, pIntent);
        Alerts.put(key, alert);
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
