package br.edu.ifrs.canoas.ifcaronasolidaria.Notifications;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.edu.ifrs.canoas.ifcaronasolidaria.model.InteresseCarona;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.NotificacaoCarona;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.UsuarioHelper;

public class NotificationControl {

    private FirebaseDatabase firebase;
    private ValueEventListener handler;

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

    public void checkNotifications(final Context context) {
        FirebaseApp.initializeApp(context);


        firebase = FirebaseDatabase.getInstance("https://ifcaronasolidaria.firebaseio.com");
        handler = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot arg0) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                if (isAppOnForeground(context) && sharedPref.contains("TOKEN") && sharedPref.contains("USER")) {
                    postNotif((HashMap) arg0.getValue(), context);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Error on connect database", Toast.LENGTH_LONG).show();
            }
        };

        firebase.getReference().addValueEventListener(handler);
    }

    private void postNotif(HashMap map, Context context) {
        if (UsuarioHelper.getUsuarioLogado(context) != null) {
            if (map != null && map.entrySet() != null) {
                if (map.get("interesse-carona") != null && ((HashMap) map.get("interesse-carona")).get(UsuarioHelper.getUsuarioLogado(context).getUserid()) != null) {
                    NotificationsHelper.clearNotifications(context);
                    Iterator iterator1 = ((HashMap) ((HashMap) map.get("interesse-carona")).get(UsuarioHelper.getUsuarioLogado(context).getUserid())).entrySet().iterator();
                    interesseCaronaNotification(iterator1, context);
                }
                if (map.get("notification-carona") != null && ((HashMap) map.get("notification-carona")).get(UsuarioHelper.getUsuarioLogado(context).getUserid()) != null) {
                    NotificationsHelper.clearNotifications(context);
                    Iterator iterator2 = ((HashMap) ((HashMap) map.get("notification-carona")).get(UsuarioHelper.getUsuarioLogado(context).getUserid())).entrySet().iterator();
                    caronaNotification(iterator2, context);
                }

            }
        }
    }


    private void caronaNotification(Iterator iterator1, final Context context) {
        while (iterator1.hasNext()) {
            Map.Entry pair1 = (Map.Entry) iterator1.next();
            Gson gson = new Gson();
            final NotificacaoCarona notificacaoCarona = gson.fromJson((String) pair1.getValue(), NotificacaoCarona.class);
            firebase.getReference().child("notification-carona").child(notificacaoCarona.getInteresseCarona().getUsuario().getMatricula()).child((String) pair1.getKey()).removeValue();
            if (notificacaoCarona.getText().contains("Ops")) {
                NotificationsHelper.sendMessageRejeitado(notificacaoCarona, context);
            } else {
                NotificationsHelper.sendMessageAceito(notificacaoCarona, context);
            }
        }
    }


    private void interesseCaronaNotification(Iterator iterator1, Context context) {
        int position = 0;
        while (iterator1.hasNext()) {
            Map.Entry pair1 = (Map.Entry) iterator1.next();
            Gson gson = new Gson();
            InteresseCarona interesseCarona = gson.fromJson(pair1.getValue().toString(), InteresseCarona.class);
            NotificationsHelper.sendRequisicaoCarona(interesseCarona,context,position,(String)pair1.getKey());
            position++;
        }
    }
}
