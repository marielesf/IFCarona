package br.edu.ifrs.canoas.ifcaronasolidaria.Notifications;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import br.edu.ifrs.canoas.ifcaronasolidaria.NavigationDrawerActivity;
import br.edu.ifrs.canoas.ifcaronasolidaria.fragment.BoasVindasFragment;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.UsuarioHelper;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.UsuarioMoodle;
import br.edu.ifrs.canoas.ifcaronasolidaria.persist.RestfulAPIUsuario;
import ifcaronasolidaria.canoas.ifrs.edu.br.ifcarona.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            if(!isAppOnForeground(getApplicationContext()))
                sendNotification( remoteMessage.getData());
        }


        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }


    private void sendRegistrationToServer(final String token) {
        final UsuarioMoodle usuario = UsuarioHelper.getUsuarioLogado(getApplicationContext());
        final RestfulAPIUsuario rest = new RestfulAPIUsuario();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    rest.updateToken(usuario.getUserid(), token);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    private void sendNotification(Map<String,String> map) {
        PendingIntent pendingIntent = NotificationsHelper.goToIntent(NavigationDrawerActivity.class, getApplicationContext());

        String channelId = "chanel 1";
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.notification)
                        .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.notification))
                        .setVibrate(new long[]{150, 300})
                        .setContentTitle(map.get("title"))
                        .setContentText(map.get("body"))
                        .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.horn_sound))
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}