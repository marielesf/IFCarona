package br.edu.ifrs.canoas.ifcaronasolidaria.persist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.edu.ifrs.canoas.ifcaronasolidaria.model.InteresseCarona;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by mariele on 30/11/2017.
 */

public class RestfulAPIInteresseCarona extends RetrofitInitializer{

    public interface Insert {
        @FormUrlEncoded
        @POST("controle/interesse-controle.php?op=1")
        Call<InteresseCarona> insert(@Field("idRota") int idRota, @Field("matricula") String matricula, @Field("dataHora") String dataHora);
    }

    public Call<InteresseCarona> insert(int idRota, String matricula, Date datHora){
        Insert ins = getRetrofit().create(Insert.class);
        String hora = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(datHora);
        return ins.insert(idRota, matricula, hora);
    }

    public interface Delete {
        @FormUrlEncoded
        @POST("controle/interesse-controle.php?op=2")
        Call<InteresseCarona> delete(@Field("idInteresse") int idInteresse);
    }

    public Call<InteresseCarona> delete(int idInteresse){
        Delete delete = getRetrofit().create(Delete.class);
        return delete.delete(idInteresse);
    }

    public interface ReadQuery {
        @FormUrlEncoded
        @POST("controle/interesse-controle.php?op=3")
        Call<List<InteresseCarona>> readquery(@Field("idRota") int idRota);
    }
    public Call<List<InteresseCarona>>readQuery(int idRota){
        ReadQuery readQuery = getRetrofit().create(ReadQuery.class);
        System.out.println(readQuery.readquery(idRota));
        return readQuery.readquery(idRota);
    }

    public interface GetUsersByRota {
        @FormUrlEncoded
        @POST("controle/interesse-controle.php?op=4")
        Call<List<String>> getUsersByRota(@Field("idRota") int idRota);
    }
    public Call<List<String>>getUsersByRota(int idRota){
        GetUsersByRota getUsersByRota = getRetrofit().create(GetUsersByRota.class);
        System.out.println(getUsersByRota.getUsersByRota(idRota));
        return getUsersByRota.getUsersByRota(idRota);
    }

    public interface CheckUserRota {
        @FormUrlEncoded
        @POST("controle/interesse-controle.php?op=5")
        Call<Boolean> getUsersByRota(@Field("idRota") int idRota,@Field("matricula") String matricula);
    }
    public Call<Boolean> checkUserRota(int idRota, String matricula){
        CheckUserRota checkUserRota = getRetrofit().create(CheckUserRota.class);
        System.out.println(checkUserRota.getUsersByRota(idRota, matricula));
        return checkUserRota.getUsersByRota(idRota, matricula);
    }
}
