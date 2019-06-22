package br.edu.ifrs.canoas.ifcaronasolidaria.persist;

import java.io.IOException;
import java.util.List;

import br.edu.ifrs.canoas.ifcaronasolidaria.model.Usuario;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class RestfulAPINotification extends RetrofitInitializer{

    public interface SendNotification {
        @FormUrlEncoded
        @POST("controle/notification-controle.php?op=1")
        Call<Usuario> sendNotification(@Field("matricula") String matricula,
                                       @Field("interesse-carona") String interesseCarona,
                                       @Field("message") String message);
    }

    public void sendNotification(String matricula, String interesseCaroa, String message) throws IOException {
        SendNotification sendNotification = getRetrofit().create(SendNotification.class);
        sendNotification.sendNotification(matricula, interesseCaroa, message).execute();
    }
}