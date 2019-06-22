package br.edu.ifrs.canoas.ifcaronasolidaria.persist;

import java.io.IOException;
import java.util.List;

import br.edu.ifrs.canoas.ifcaronasolidaria.model.Rota;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class RestfulAPIOfertarCarona extends RetrofitInitializer{

    public interface Insert {
        @FormUrlEncoded
        @POST("controle/rota-controle.php?op=1")
        Call<Rota> insert(@Field("matricula") String matricula, @Field("nomeUser") String nomeUser,
                          @Field("status") int status, @Field("idEnderecoInicio") long idEnderecoInicio,
                          @Field("idEnderecoFim") long idEnderecoFim, @Field("horaSaida") String horaSaida);
    }

    public Call<Rota> insert(String matricula, String nomeUser,
                             boolean status, long idEnderecoInicio, long idEnderecoFim, String horaSaida){
        Insert ins = getRetrofit().create(Insert.class);
        return ins.insert(matricula, nomeUser, status ? 1 : 0, idEnderecoInicio,idEnderecoFim, horaSaida);
    }

    public interface Delete {
        @FormUrlEncoded
        @POST("controle/rota-controle.php?op=2")
        Call<Rota> delete(@Field("idRota") int idRota);
    }

    public Call<Rota> delete(int idRota){
        Delete delete = getRetrofit().create(Delete.class);
        return delete.delete(idRota);
    }

    public interface Update {
        @FormUrlEncoded
        @POST("controle/rota-controle.php?op=3")
        Call<Rota> update(@Field("idRota") String idRota, @Field("status") int status);
    }

    public Response<Rota> update(String idRota, boolean novoStatus) throws IOException {
        Update update = getRetrofit().create(Update.class);
        System.out.println(update.update(idRota, novoStatus? 1 : 0));
        return update.update(idRota, novoStatus? 1 : 0).execute();
    }

    public interface Read {
        @GET("controle/rota-controle.php?op=4")
        Call<List<Rota>> read();
    }
    public Call<List<Rota>> read(){
        Read read = getRetrofit().create(Read.class);
        return read.read();
    }

    public interface ReadQuery {
        @FormUrlEncoded
        @POST("controle/rota-controle.php?op=5")
        Call<List<Rota>> readquery(@Field("matricula") String matricula);
    }
    public Call<List<Rota>> readQuery(String matricula){
        ReadQuery readQuery = getRetrofit().create(ReadQuery.class);
        System.out.println(readQuery.readquery(matricula));
        return readQuery.readquery(matricula);
    }


}