package br.edu.ifrs.canoas.ifcaronasolidaria.service;

/**
 * Created by mariele on 20/11/2017.
 */

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import br.edu.ifrs.canoas.ifcaronasolidaria.NavigationDrawerActivity;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.InteresseCarona;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.NotificacaoCarona;
import br.edu.ifrs.canoas.ifcaronasolidaria.persist.RestfulAPIInteresseCarona;
import br.edu.ifrs.canoas.ifcaronasolidaria.persist.RestfulAPINotification;
import ifcaronasolidaria.canoas.ifrs.edu.br.ifcarona.R;

public class NotificationTrigger extends BroadcastReceiver {

    private FirebaseDatabase firebase = FirebaseDatabase.getInstance("https://ifcaronasolidaria.firebaseio.com");
    private DatabaseReference rootRef = firebase.getReference().child("notification-carona");

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String action = intent.getAction();
        final Gson gson = new Gson();
        final InteresseCarona interesseCarona = (InteresseCarona) intent.getSerializableExtra("interesseCarona");
        String key = intent.getStringExtra("key");
        final String idCancel = intent.getStringExtra("idCancel");
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
        nMgr.cancel(Integer.parseInt(idCancel));// remover do celular a notificacao
        firebase.getReference().child("interesse-carona").child(interesseCarona.getRota().getMatricula()).child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DatabaseReference databaseReference = rootRef.child(interesseCarona.getUsuario().getMatricula()).push();
                NotificacaoCarona notificacaoCarona;
                if ("YES_ACTION".equals(action)) {
                    notificacaoCarona = new NotificacaoCarona("Carona aceita", interesseCarona);
                    databaseReference.setValue(gson.toJson(notificacaoCarona));
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                RestfulAPIInteresseCarona restNotifition = new RestfulAPIInteresseCarona();
                                RestfulAPINotification restfulAPINotification = new RestfulAPINotification();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                                restNotifition.insert(interesseCarona.getRota().getIdRota(), interesseCarona.getUsuario().getMatricula(), sdf.parse(interesseCarona.getDataHora())).execute();
                                restfulAPINotification.sendNotification(interesseCarona.getUsuario().getMatricula(), gson.toJson(interesseCarona), "Carona aceita");
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else if ("NO_ACTION".equals(action)) {
                    notificacaoCarona = new NotificacaoCarona("Ops, carro lotado! Tente outra carona.", interesseCarona);
                    databaseReference.setValue(gson.toJson(notificacaoCarona));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                RestfulAPINotification restfulAPINotification = new RestfulAPINotification();
                                restfulAPINotification.sendNotification(interesseCarona.getUsuario().getMatricula(), gson.toJson(interesseCarona), "Ops, carro lotado! Tente outra carona.");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    Intent resultIntent = new Intent(context, NavigationDrawerActivity.class);
                    resultIntent.putExtra("initialFragment", R.id.nav_Passageiros);
                    context.startActivity(resultIntent);
                }
            }
        });

    }
}