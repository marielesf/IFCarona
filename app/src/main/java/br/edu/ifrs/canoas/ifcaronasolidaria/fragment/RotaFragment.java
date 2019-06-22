package br.edu.ifrs.canoas.ifcaronasolidaria.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import br.edu.ifrs.canoas.ifcaronasolidaria.Adapter.GerenciaListaRotaAdapter;
import br.edu.ifrs.canoas.ifcaronasolidaria.AnalyticsApplication;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.UsuarioHelper;
import ifcaronasolidaria.canoas.ifrs.edu.br.ifcarona.R;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifrs.canoas.ifcaronasolidaria.model.Endereco;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.Rota;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.UsuarioMoodle;
import br.edu.ifrs.canoas.ifcaronasolidaria.persist.RestfulAPIEndereco;
import br.edu.ifrs.canoas.ifcaronasolidaria.persist.RestfulAPIOfertarCarona;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RotaFragment extends Fragment implements CheckBox.OnCheckedChangeListener, AdapterView.OnItemClickListener{
    private ListView lvPontoInteresse;
    private GerenciaListaRotaAdapter ldGerenciaListaRotaAdapter;
    private List<Endereco> end;
    private static Tracker mTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    public void onResume(){
        super.onResume();
        mTracker.setScreenName("Solicita Carona");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_rota, container, false);
        lvPontoInteresse = (ListView) view.findViewById(R.id.lvRotas);
        carregarRotas();
        return view;
    }

    public void carregarEnderecos(){
        RestfulAPIEndereco rest = new RestfulAPIEndereco();
        Call<List<Endereco>> enderecos = rest.read();
        enderecos.enqueue(new Callback<List<Endereco>>() {
            @Override
            public void onResponse(Call<List<Endereco>> call, Response<List<Endereco>> response) {
                end = response.body();

                ArrayAdapter<Endereco> adapter = new ArrayAdapter<>(
                        getActivity().getBaseContext(),
                        android.R.layout.simple_list_item_1,
                        end
                );

                lvPontoInteresse.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Endereco>> call, Throwable t) {
                if(!UsuarioHelper.verificaConexao(getContext())){
                    Toast.makeText(getContext(), "ERRO: Sem Internet", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "ERRO: não foi possível conectar com o banco.", Toast.LENGTH_SHORT).show();
                    Log.e("ERRO", t.getMessage());
                }
            }
        });
    }

    public void carregarEnderecos(String nome) {
        RestfulAPIEndereco rest = new RestfulAPIEndereco();
        Call<List<Endereco>> enderecos = rest.readQuery(nome);
        enderecos.enqueue(new Callback<List<Endereco>>() {
            @Override
            public void onResponse(Call<List<Endereco>> call, Response<List<Endereco>> response) {
                end = response.body();

                ArrayAdapter<Endereco> adapter = new ArrayAdapter<>(
                        getActivity().getBaseContext(),
                        android.R.layout.simple_list_item_1,
                        end
                );

                lvPontoInteresse.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Endereco>> call, Throwable t) {
                if(!UsuarioHelper.verificaConexao(getContext())){
                    Toast.makeText(getContext(), "ERRO: Sem Internet", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "ERRO: não foi possível conectar com o banco.", Toast.LENGTH_SHORT).show();
                    Log.e("ERRO", t.getMessage());
                }
            }
        });
    }

    public void carregarRotas() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String usuario = sharedPref.getString("USER", "SEM_TOKEN");
        Gson g = new Gson();
        UsuarioMoodle retorno = g.fromJson(usuario.trim(), UsuarioMoodle.class);

        RestfulAPIOfertarCarona restCarona = new RestfulAPIOfertarCarona();
        Call<List<Rota>> ofrtaCarona = restCarona.readQuery(retorno.getUserid());
        ofrtaCarona.enqueue(new Callback<List<Rota>>() {

            @Override
            public void onResponse(Call<List<Rota>> call, Response<List<Rota>> response) {
                ArrayList<Rota> lista = new ArrayList<>();
                if(response.body().size() < 1){
                    Toast.makeText(getContext(),"Nehuma Carona Cadastrada!", Toast.LENGTH_LONG).show();
                }else {
                    for (int i = 0; i < response.body().size(); i++) {
                        if (response.body().get(i).getMatricula().equals(UsuarioHelper.getUsuarioLogado(getContext()).getUserid())) {
                            lista.add(response.body().get(i));
                        }
                    }
                    ldGerenciaListaRotaAdapter = new GerenciaListaRotaAdapter(lista, getContext());
                    lvPontoInteresse.setAdapter(ldGerenciaListaRotaAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Rota>> call, Throwable t) {
                if(!UsuarioHelper.verificaConexao(getContext())){
                    Toast.makeText(getContext(), "ERRO: Sem Internet", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "ERRO: não foi possível conectar com o banco.", Toast.LENGTH_SHORT).show();
                    Log.e("ERRO", t.getMessage());
                }
            }
        });

    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        String pesquisa = compoundButton.getText().toString();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
