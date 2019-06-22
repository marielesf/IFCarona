package br.edu.ifrs.canoas.ifcaronasolidaria.fragment;


import android.app.AlertDialog;
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
import android.widget.ListView;
import android.widget.Toast;

import br.edu.ifrs.canoas.ifcaronasolidaria.AnalyticsApplication;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.UsuarioHelper;
import ifcaronasolidaria.canoas.ifrs.edu.br.ifcarona.R;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifrs.canoas.ifcaronasolidaria.model.Rota;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.UsuarioMoodle;
import br.edu.ifrs.canoas.ifcaronasolidaria.persist.RestfulAPIInteresseCarona;
import br.edu.ifrs.canoas.ifcaronasolidaria.persist.RestfulAPIOfertarCarona;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListarPassegeiroFragment extends Fragment {

    private ListView passageiros;
    private List<Rota> myRotas = new ArrayList<Rota>();
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
        mTracker.setScreenName("Lista Passageiros");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_listar_passegeiro, container, false);

        passageiros = (ListView) view.findViewById(R.id.listaPassageiros);
        carregarPassageiro();

        passageiros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RestfulAPIInteresseCarona restNotifition = new RestfulAPIInteresseCarona();
                Call<List<String>> queryIntereseCarona = restNotifition.getUsersByRota(myRotas.get(i).getIdRota());

                queryIntereseCarona.enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(Call<List<String>> call, Response<List<String>> response) {

                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        String users = "";
                        for (String user : response.body()) {
                            users += user + "\n";
                        }
                        if (!users.isEmpty())
                            dialog.setTitle("Passageiros").setMessage(users).setPositiveButton("OK", null).show();
                    }

                    @Override
                    public void onFailure(Call<List<String>> call, Throwable t) {
                        if(!UsuarioHelper.verificaConexao(getContext())){
                            Toast.makeText(getContext(), "ERRO: Sem Internet", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getContext(), "ERRO: não foi possível conectar com o banco.", Toast.LENGTH_SHORT).show();
                            Log.e("ERRO", t.getMessage());
                        }
                    }
                });
            }
        });
        return view;
    }

    public void carregarPassageiro() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String usuario = sharedPref.getString("USER", "SEM_TOKEN");
        Gson g = new Gson();
        UsuarioMoodle retorno = g.fromJson(usuario.trim(), UsuarioMoodle.class);

        RestfulAPIOfertarCarona restCarona = new RestfulAPIOfertarCarona();
        Call<List<Rota>> ofrtaCarona = restCarona.readQuery(retorno.getUserid());
        ofrtaCarona.enqueue(new Callback<List<Rota>>() {

            @Override
            public void onResponse(Call<List<Rota>> call, Response<List<Rota>> response) {
                ArrayAdapter<Rota> rotas = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, response.body());

                for (int i = 0; i < response.body().size(); i++) {
                    if (response.body().get(i).getMatricula().equals(UsuarioHelper.getUsuarioLogado(getContext()).getUserid())) {

                        myRotas.add(response.body().get(i));
                    }
                }
                if(myRotas.size() < 1){
                    Toast.makeText(getContext(),"Nehuma Carona Cadastrada!", Toast.LENGTH_LONG).show();
                }
                passageiros.setAdapter(rotas);
                rotas.notifyDataSetChanged();
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
