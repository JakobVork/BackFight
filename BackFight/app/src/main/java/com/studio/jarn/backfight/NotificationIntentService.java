package com.studio.jarn.backfight;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.studio.jarn.backfight.gameboard.GameActivity;

public class NotificationIntentService extends IntentService {
    public static final String ACTION_NEWROUND = "com.studio.jarn.backfight.action.NEWROUND";

    public static final String EXTRA_TITLE = "com.studio.jarn.backfight.extra.TITLE";
    public static final String EXTRA_TEXT = "com.studio.jarn.backfight.extra.TEXT";

    public NotificationIntentService() {
        super("NotificationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_NEWROUND.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_TITLE);
                final String param2 = intent.getStringExtra(EXTRA_TEXT);
                handleActionNewRound(param1, param2);
            }
        }
    }

    //inspiration from: http://stackoverflow.com/questions/1207269/sending-a-notification-from-a-service-in-android
    private void handleActionNewRound(String param1, String param2) {

        if (!GameActivity.isGameActivityVisible) {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.player32)
                            .setContentTitle(param1)
                            .setContentText(param2)
                            .setAutoCancel(true);

            int NOTIFICATION_ID = 12345;
            Intent targetIntent = new Intent(this, GameActivity.class);
            targetIntent.putExtra(getString(R.string.EXTRA_HOST), false);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
            NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}
