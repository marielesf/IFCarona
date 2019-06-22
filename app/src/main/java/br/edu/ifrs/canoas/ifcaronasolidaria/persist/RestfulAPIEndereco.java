package br.edu.ifrs.canoas.ifcaronasolidaria.persist;

import android.util.Log;

import java.util.List;

import br.edu.ifrs.canoas.ifcaronasolidaria.model.Endereco;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class RestfulAPIEndereco extends RetrofitInitializer{

    public interface Insert {
        @FormUrlEncoded
        @POST("controle/endereco-controle.php?op=1")
        Call<Endereco> insert(@Field("nome") String nome, @Field("cidade") String cidade, @Field("bairro") String bairro, @Field("rua") String rua, @Field("numero") int numero);
    }

    public Call<Endereco> insert(String nome, String cidade, String bairro, String rua, int numero){
        Insert ins = getRetrofit().create(Insert.class);
        Log.e("Debug","1");
        return ins.insert(nome, cidade, bairro, rua, numero);
    }

    public interface Delete {
        @FormUrlEncoded
        @POST("controle/endereco-controle.php?op=2")
        Call<Endereco> delete(@Field("nome") String nome, @Field("rua") String rua);
    }

    public Call<Endereco> delete(String nome, String rua){
        Delete delete = getRetrofit().create(Delete.class);
        return delete.delete(nome, rua);
    }

    public interface Update {
        @FormUrlEncoded
        @POST("controle/endereco-controle.php?op=3")
        Call<Endereco> update(@Field("nome") String nome, @Field("novonome") String novonome);
    }

    public Call<Endereco> update(String nome, String novoNome){
        Update update = getRetrofit().create(Update.class);
        return update.update(nome, novoNome);
    }

    public interface Read {
        @GET("controle/endereco-controle.php?op=4")
        Call<List<Endereco>> read();
    }
    public Call<List<Endereco>> read(){
        Read read = getRetrofit().create(Read.class);
        return read.read();
    }

    public interface ReadQuery {
        @FormUrlEncoded
        @POST("controle/endereco-controle.php?op=5")
        Call<List<Endereco>> readquery(@Field("nome") String nome);
    }
    public Call<List<Endereco>> readQuery(String nome){
        ReadQuery readQuery = getRetrofit().create(ReadQuery.class);
        return readQuery.readquery(nome);
    }
}
