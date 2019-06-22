package br.edu.ifrs.canoas.ifcaronasolidaria.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.util.List;

import br.edu.ifrs.canoas.ifcaronasolidaria.NavigationDrawerActivity;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.InteresseCarona;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.NotificacaoCarona;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.Usuario;
import br.edu.ifrs.canoas.ifcaronasolidaria.persist.RestfulAPIUsuario;
import br.edu.ifrs.canoas.ifcaronasolidaria.service.NotificationTrigger;
import ifcaronasolidaria.canoas.ifrs.edu.br.ifcarona.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsHelper {

    public static NotificationCompat.Builder createBuilder(Context context, String message, PendingIntent resultPendingIntent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notification))
                .setVibrate(new long[]{150, 300})
                .setContentTitle("Solicitação de carona")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.horn_sound))
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);
        return mBuilder;
    }

    public static void clearNotifications(Context context){
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
    }

    public static void sendNotification(NotificationCompat.Builder mBuilder, Context context, int position) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "my_channel_01";// The id of the channel.
            mBuilder.setChannelId(CHANNEL_ID);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(channel);
        }
        nm.notify(position, mBuilder.build());
    }

    public static PendingIntent goToIntent(Class<?> destination, Context context) {
        Intent resultIntent = new Intent(context, destination);
        final PendingIntent resultPendingIntent = PendingIntent.getActivity( // atividade
                context, // Contexto que vem do receive "MUITO IMPORTANTE"
                0, // Parametro não usado esse código pode servir na hora de capturar o broadcast nesse caso não me importa.
                resultIntent, // Intent que lancará
                PendingIntent.FLAG_UPDATE_CURRENT //FLAG_ONE_SHOT - FLAG_CANCEL_CURRENT -FLAG_UPDATE_CURRENT https://developer.android.com/reference/android/app/PendingIntent.html
        );

        return resultPendingIntent;
    }


    private static String createMessageRejeitado(NotificacaoCarona notificacaoCarona) {
        String message = notificacaoCarona.getText() +
                "\nRota:" +
                "\n  Inicio: " + notificacaoCarona.getInteresseCarona().getRota().getIdEnderecoInicio().getNome() +
                "\n  Fim: " + notificacaoCarona.getInteresseCarona().getRota().getIdEnderecoFim().getNome() +
                "\n  Hora De Saida: " + notificacaoCarona.getInteresseCarona().getRota().getHoraSaida();
        return message;
    }

    private static String createMessageAceitoComMotorista(NotificacaoCarona notificacaoCarona, Usuario usuario) {
        String message = notificacaoCarona.getText() + " por " + notificacaoCarona.getInteresseCarona().getRota().getNomeUser();
        if (usuario != null) {
            if (usuario.getTelefone() != null) {
                message += "\n Telefone: " + usuario.getTelefone();
            }
            if (usuario.getPlacaCarro() != null) {
                message += "\n Placa: " + usuario.getPlacaCarro();
            }
            if (usuario.getModeloCor() != null) {
                message += "\n Modelo/Cor: " + usuario.getModeloCor();
            }
            message += createMessageRota(notificacaoCarona);

        }
        return message;
    }

    private static String createMessageRota(NotificacaoCarona notificacaoCarona){
        String message = "\nRota:" +
                "\n  Inicio: " + notificacaoCarona.getInteresseCarona().getRota().getIdEnderecoInicio().getNome() +
                "\n  Fim: " + notificacaoCarona.getInteresseCarona().getRota().getIdEnderecoFim().getNome() +
                "\n  Hora De Saida: " + notificacaoCarona.getInteresseCarona().getRota().getHoraSaida();

        return message;
    }

    private static String createMessageAceitoSemMotorista(NotificacaoCarona notificacaoCarona) {
        String message = notificacaoCarona.getText() + " por " + notificacaoCarona.getInteresseCarona().getRota().getNomeUser();
        message += createMessageRota(notificacaoCarona);

        return message;
    }

    public static void sendMessageRejeitado(final NotificacaoCarona notificacaoCarona, final Context context) {
        String message = NotificationsHelper.createMessageRejeitado(notificacaoCarona);
        PendingIntent resultPendingIntent = NotificationsHelper.goToIntent(NavigationDrawerActivity.class, context);
        NotificationCompat.Builder mBuilder = NotificationsHelper.createBuilder(context, message, resultPendingIntent);
        NotificationsHelper.sendNotification(mBuilder, context,1);
    }


    public static void sendMessageAceito(final NotificacaoCarona notificacaoCarona, final Context context) {
        RestfulAPIUsuario perfilRest = new RestfulAPIUsuario();
        retrofit2.Call<List<Usuario>> callPerfil = perfilRest.readQuery(notificacaoCarona.getInteresseCarona().getRota().getMatricula());
        callPerfil.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                Usuario usuario = response.body().get(0);
                String message = createMessageAceitoComMotorista(notificacaoCarona, usuario);
                PendingIntent resultPendingIntent = goToIntent(NavigationDrawerActivity.class, context);
                NotificationCompat.Builder mBuilder = createBuilder(context, message, resultPendingIntent);
                sendNotification(mBuilder, context, 1);

            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                String message = createMessageAceitoSemMotorista(notificacaoCarona);
                PendingIntent resultPendingIntent = goToIntent(NavigationDrawerActivity.class, context);
                NotificationCompat.Builder mBuilder = createBuilder(context, message, resultPendingIntent);
                sendNotification(mBuilder, context, 1);
            }
        });

    }


    private static String createMessageRequisicaoCarona(InteresseCarona interesseCarona) {
        String message = "Nome: " + interesseCarona.getUsuario().getNomeUser();

        if(interesseCarona.getUsuario().getTelefone() != null)
            message += "\nTelefone: " + interesseCarona.getUsuario().getTelefone();

        message +=
                        "\nRota: " +
                        "\n  Inicio: " + interesseCarona.getRota().getIdEnderecoInicio().getNome() +
                        "\n  Fim: " + interesseCarona.getRota().getIdEnderecoFim().getNome() +
                        "\n  Hora De Saida: " + interesseCarona.getRota().getHoraSaida();
        return  message;
    }

    private static PendingIntent createActionIntentRedirectingTo(InteresseCarona interesseCarona, String action, Class<?> reditctTo, Context context, String position, String key){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("interesseCarona", interesseCarona);
        intent.putExtra("key", key);
        intent.putExtra("idCancel", position);
        intent.setClass(context, reditctTo);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    public static void sendRequisicaoCarona(final InteresseCarona interesseCarona, final Context context, int position, String key) {
        String message = createMessageRequisicaoCarona(interesseCarona);
        PendingIntent resultPendingIntent = goToIntent(NavigationDrawerActivity.class, context);
        NotificationCompat.Builder mBuilder = createBuilder(context, message, resultPendingIntent);

        PendingIntent pendingIntentSim = createActionIntentRedirectingTo(interesseCarona,"YES_ACTION",NotificationTrigger.class,context,String.valueOf(position),key);
        PendingIntent pendingIntentNao = createActionIntentRedirectingTo(interesseCarona,"NO_ACTION",NotificationTrigger.class,context,String.valueOf(position),key);

        int icon = 0;
        mBuilder.addAction(icon, "ACEITAR", pendingIntentSim);
        mBuilder.addAction(icon, "REJEITAR", pendingIntentNao);
        sendNotification(mBuilder,context, position);
        position++;

    }

}
