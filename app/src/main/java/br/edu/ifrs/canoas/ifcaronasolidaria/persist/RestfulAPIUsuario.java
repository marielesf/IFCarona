package br.edu.ifrs.canoas.ifcaronasolidaria.persist;

import java.io.IOException;
import java.util.List;

import br.edu.ifrs.canoas.ifcaronasolidaria.model.Usuario;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class RestfulAPIUsuario extends RetrofitInitializer{

    public interface Insert {
        @FormUrlEncoded
        @POST("controle/usuario-controle.php?op=1")
        Call<Usuario> insert(@Field("matricula") String matricula, @Field("nomeUser") String nomeUser,
                             @Field("placaCarro") String placaCarro, @Field("modeloCor") String modeloCor,
                             @Field("telefone") String telefone,
                             @Field("token") String token);
    }

    public Call<Usuario> insert(String matricula, String nomeUser,
                                String placaCarro, String modeloCor, String telefone, String token){
        Insert ins = getRetrofit().create(Insert.class);
        return ins.insert(matricula, nomeUser, placaCarro, modeloCor, telefone, token);
    }

    public interface Update {
        @FormUrlEncoded
        @POST("controle/usuario-controle.php?op=2")
        Call<Usuario> update(@Field("idPerfil") int idPerfil, @Field("placaCarro") String placaCarro, @Field("modeloCor") String modeloCor,
                             @Field("telefone") String telefone);
    }

    public Response<Usuario> update(int idPerfil, String placaCarro, String modeloCor, String telefone) throws IOException {
        Update update = getRetrofit().create(Update.class);
        System.out.println(update.update(idPerfil, placaCarro, modeloCor, telefone));
        return update.update(idPerfil, placaCarro, modeloCor, telefone).execute();
    }

    public interface ReadQuery {
        @FormUrlEncoded
        @POST("controle/usuario-controle.php?op=3")
        Call<List<Usuario>> readquery(@Field("matricula") String matricula);
    }
    public Call<List<Usuario>> readQuery(String matricula){
        ReadQuery readQuery = getRetrofit().create(ReadQuery.class);
        System.out.println(readQuery.readquery(matricula));
        return readQuery.readquery(matricula);
    }

    public interface UpdateToken {
        @FormUrlEncoded
        @POST("controle/usuario-controle.php?op=4")
        Call<Usuario> updateToken(@Field("matricula") String matricula,@Field("token") String token);
    }
    public void updateToken(String matricula, String token) throws IOException {
        UpdateToken updateToken = getRetrofit().create(UpdateToken.class);
        System.out.println(updateToken.updateToken(matricula, token));
        Call<Usuario> resposne = updateToken.updateToken(matricula, token);
        resposne.execute();
        resposne.isExecuted();

    }


}