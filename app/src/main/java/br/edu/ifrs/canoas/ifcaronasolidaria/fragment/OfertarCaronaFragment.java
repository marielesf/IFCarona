package br.edu.ifrs.canoas.ifcaronasolidaria.fragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import br.edu.ifrs.canoas.ifcaronasolidaria.AnalyticsApplication;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.UsuarioHelper;
import ifcaronasolidaria.canoas.ifrs.edu.br.ifcarona.R;


import java.io.IOException;
import java.util.List;

import br.edu.ifrs.canoas.ifcaronasolidaria.model.Endereco;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.UsuarioMoodle;
import br.edu.ifrs.canoas.ifcaronasolidaria.persist.RestfulAPIEndereco;
import br.edu.ifrs.canoas.ifcaronasolidaria.persist.RestfulAPIOfertarCarona;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mariele on 04/12/2017.
 */

public class OfertarCaronaFragment extends Fragment {
    private Spinner etDe;
    private Spinner etPara;
    private EditText etHoraSair;
    private Button btnHoraSair;

    private Button btnCadastrar;
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
        mTracker.setScreenName("Oferta Carona");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_ofertar_carona, container, false);
        etDe = (Spinner) view.findViewById(R.id.etDe);
        etPara = (Spinner) view.findViewById(R.id.etPara);
        etHoraSair = (EditText) view.findViewById(R.id.etHoraSair);
        btnHoraSair = (Button) view.findViewById(R.id.btnHoraSair);

        btnCadastrar = (Button) view.findViewById(R.id.btnSalvar);

        carregarEnderecos();
        final Context contexto = getContext();

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validaHoraSair(etHoraSair)) {
                    final Handler handler = new Handler(Looper.getMainLooper());

                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                cadastrar(
                                        ((Endereco) etDe.getSelectedItem()).getIdEndereco(),
                                        ((Endereco) etPara.getSelectedItem()).getIdEndereco(),
                                        etHoraSair.getText().toString());
                                handler.post(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(contexto, "Salvo com SUCESSO!", Toast.LENGTH_LONG).show();
                                            }
                                        });


                                Fragment fragment = new BoasVindasFragment();
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.addToBackStack(BoasVindasFragment.class.getCanonicalName());
                                ft.replace(R.id.content_frame, fragment);
                                ft.commit();
                            } catch (IOException e) {

                            }
                        }
                    }).start();
                }
            }
        });


        btnHoraSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exibirTimePicker();
            }
        });

        return view;
    }

    private void exibirTimePicker() {
        TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                etHoraSair.setText(String.format("%02d", i) + ":" + String.format("%02d", i1));
            }
        }, 0, 0, true);

        dialog.show();
    }

    private void carregarEnderecos() {
        RestfulAPIEndereco api = new RestfulAPIEndereco();
        Call<List<Endereco>> callback = api.read();

        callback.enqueue(new Callback<List<Endereco>>() {
            @Override
            public void onResponse(Call<List<Endereco>> call, Response<List<Endereco>> response) {
                ArrayAdapter<Endereco> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, response.body());
                etDe.setAdapter(adapter);
                etPara.setAdapter(adapter);
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


    public void cadastrar(long de, long para, String horario) throws IOException {
        UsuarioMoodle retorno = UsuarioHelper.getUsuarioLogado(getContext());

        RestfulAPIOfertarCarona rest = new RestfulAPIOfertarCarona();
        rest.insert(retorno.getUserid(), retorno.getFullname(), true, de, para, horario).execute();


    }

    public void alterar(String nomeAntigo, String nomeNovo) {
        RestfulAPIEndereco rest = new RestfulAPIEndereco();
        Call<Endereco> alterar = rest.update(nomeAntigo, nomeNovo);
        alterar.enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                Toast.makeText(getActivity().getBaseContext(), "OK", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Endereco> call, Throwable t) {
                if(!UsuarioHelper.verificaConexao(getContext())){
                    Toast.makeText(getContext(), "ERRO: Sem Internet", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "ERRO: não foi possível conectar com o banco.", Toast.LENGTH_SHORT).show();
                    Log.e("ERRO", t.getMessage());
                }
            }
        });
    }

    public void deletar(String nome, String rua) {
        RestfulAPIEndereco rest = new RestfulAPIEndereco();
        Call<Endereco> deletar = rest.delete(nome, rua);
        deletar.enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                Toast.makeText(getActivity().getBaseContext(), "OK", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Endereco> call, Throwable t) {
                if(!UsuarioHelper.verificaConexao(getContext())){
                    Toast.makeText(getContext(), "ERRO: Sem Internet", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "ERRO: não foi possível conectar com o banco.", Toast.LENGTH_SHORT).show();
                    Log.e("ERRO", t.getMessage());
                }
            }
        });
    }


    public boolean validaHoraSair(EditText etHoraSair) {
        String campoHoraSair = etHoraSair.getText().toString();
        System.out.println(campoHoraSair);
        if (campoHoraSair == null || campoHoraSair.equals("")) {
            etHoraSair.setError("Campo obrigatório!");
            return false;
        }
        return true;
    }

}