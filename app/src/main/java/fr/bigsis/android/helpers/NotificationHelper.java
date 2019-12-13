package fr.bigsis.android.helpers;


import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import fr.bigsis.android.R;
import fr.bigsis.android.activity.ChatActivity;

public class NotificationHelper {

public static void displayNotification(Context context, String title, String body) {

    NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(context, ChatActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
    notificationManagerCompat.notify(1, mBuilder.build());
}
}

