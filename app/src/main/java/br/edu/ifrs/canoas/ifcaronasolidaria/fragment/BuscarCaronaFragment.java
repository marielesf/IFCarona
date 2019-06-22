package br.edu.ifrs.canoas.ifcaronasolidaria.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.FirebaseApp;

import br.edu.ifrs.canoas.ifcaronasolidaria.Adapter.BuscaCaronaListaAdapter;
import br.edu.ifrs.canoas.ifcaronasolidaria.AnalyticsApplication;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.UsuarioHelper;
import ifcaronasolidaria.canoas.ifrs.edu.br.ifcarona.R;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import java.util.List;

import br.edu.ifrs.canoas.ifcaronasolidaria.model.Rota;
import br.edu.ifrs.canoas.ifcaronasolidaria.persist.RestfulAPIOfertarCarona;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mariele on 04/12/2017.
 */

public class BuscarCaronaFragment extends Fragment {
    private EditText etPesquisa;
    private ListView listaCaronas;
    private ArrayList<Rota> lst_Encontrados = new ArrayList<Rota>();
    BuscaCaronaListaAdapter ldGerenciaListaRotaAdapter;
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
        mTracker.setScreenName("Busca Carona");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_buscar_carona, container, false);
        etPesquisa = (EditText) view.findViewById(R.id.etPesquisa);
        listaCaronas = (ListView) view.findViewById(R.id.listaCaronas);
        ldGerenciaListaRotaAdapter = new BuscaCaronaListaAdapter(getContext());

        carregarRotas();
        etPesquisa.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                if (ldGerenciaListaRotaAdapter != null) {
                    ldGerenciaListaRotaAdapter.getLista().clear();
                    for(Rota rota: lst_Encontrados){
                        if((rota.getIdEnderecoInicio().getNome().contains(arg0)) || (rota.getIdEnderecoFim().getNome().contains(arg0))) {
                            ldGerenciaListaRotaAdapter.getLista().add(rota);
                        }
                    }
                    listaCaronas.setAdapter(ldGerenciaListaRotaAdapter);
                }
                if(arg0.toString().equals("")){
                    carregarRotas();
                }
            }
        });


        listaCaronas.setAdapter(ldGerenciaListaRotaAdapter);


        return view;
    }

    public void carregarRotas() {
        RestfulAPIOfertarCarona rest = new RestfulAPIOfertarCarona();
        Call<List<Rota>> rotas = rest.read();
        rotas.enqueue(new Callback<List<Rota>>() {

            LocalTime horaSaida = LocalTime.now();

            @Override
            public void onResponse(Call<List<Rota>> call, Response<List<Rota>> response) {
                ldGerenciaListaRotaAdapter.getLista().clear();
                for(int i =0; i < response.body().size(); i++){
                    DateTimeFormatter form = DateTimeFormat.forPattern("HH:mm:ss");
                        horaSaida =  LocalTime.parse(response.body().get(i).getHoraSaida(), form);
                    if(response.body().get(i).isAtivo() && LocalTime.now().isBefore(horaSaida)) {
                        if(!response.body().get(i).getMatricula().equals(UsuarioHelper.getUsuarioLogado(getContext()).getUserid())) {
                            ldGerenciaListaRotaAdapter.getLista().add(response.body().get(i));
                            lst_Encontrados.add(response.body().get(i));
                        }
                    }
                }
                listaCaronas.setAdapter(ldGerenciaListaRotaAdapter);
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

}