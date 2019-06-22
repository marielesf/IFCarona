package br.edu.ifrs.canoas.ifcaronasolidaria.fragment;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.IOException;
import java.util.List;

import br.edu.ifrs.canoas.ifcaronasolidaria.AnalyticsApplication;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.Endereco;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.UsuarioHelper;
import br.edu.ifrs.canoas.ifcaronasolidaria.persist.RestfulAPIEndereco;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ifcaronasolidaria.canoas.ifrs.edu.br.ifcarona.R;


public class PontoInteresseFragment extends Fragment {

    private EditText etNomeCad;
    private EditText etRuaCad;
    private EditText etNumeroCad;

    private Button btnCadastrar;

    private EditText etNomeBusca;
    private EditText etNomeDeleta;
    private EditText etRuaDeleta;
    private EditText etNomeAntigo;
    private EditText etNomeNovo;
    private Button btnAlterar;
    private Button btnDeletar;
    private Button btnBuscar;
    private Button btnListar;
    Fragment fragment = null;
    private String cidade;
    private String bairro;
    private String rua;
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
        mTracker.setScreenName("Ponto Interesse");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_ponto_interesse, container, false);

        etNomeCad = (EditText) view.findViewById(R.id.etNomeCad);
        etRuaCad = (EditText) view.findViewById(R.id.etRuaCad);
        etNumeroCad = (EditText) view.findViewById(R.id.etNumeroCad);

        btnCadastrar = (Button) view.findViewById(R.id.btnCadastrar);

        etNomeBusca = (EditText) view.findViewById(R.id.etNomeBusca);
        etNomeDeleta = (EditText) view.findViewById(R.id.etNomeDeleta);
        etRuaDeleta = (EditText) view.findViewById(R.id.etRuaDeleta);
        etNomeAntigo = (EditText) view.findViewById(R.id.etNomeAntigo);
        etNomeNovo = (EditText) view.findViewById(R.id.etNomeNovo);

        btnAlterar = (Button) view.findViewById(R.id.btnAlterar);
        btnDeletar = (Button) view.findViewById(R.id.btnDeletar);
        btnBuscar = (Button) view.findViewById(R.id.btnBuscar);
        btnListar = (Button) view.findViewById(R.id.btnListar);
        final WebView webView = (WebView) view.findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl("https://www.google.com/maps/search/?api=1&query=IFRS Canoas&map_action=map&output=embed");

        etRuaCad.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(!etRuaCad.getText().toString().isEmpty()) {
                        Geocoder coder = new Geocoder(getContext());
                        List<Address> address = null;
                        try {
                            address = coder.getFromLocationName(etRuaCad.getText().toString(), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(address != null && !address.isEmpty()) {
                            Address location = address.get(0);
                            bairro = location.getSubLocality();
                            cidade = location.getSubAdminArea();
                            rua = location.getThoroughfare() != null ?location.getThoroughfare():location.getPostalCode();
                            btnCadastrar.setEnabled(true);

                        }else{
                            Toast.makeText(getContext(), "CEP inválido!", Toast.LENGTH_LONG).show();
                            btnCadastrar.setEnabled(false);
                        }
                        webView.loadUrl("https://www.google.com/maps/search/?api=1&query=" + etRuaCad.getText().toString() + "&map_action=map&output=embed");
                    }
                }
            }
        });


        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etNomeCad.getText().toString().isEmpty() && !rua.isEmpty())
                    cadastrar(etNomeCad.getText().toString(), Integer.parseInt(etNumeroCad.getText().toString()));
            }
        });

        btnAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alterar(
                        etNomeAntigo.getText().toString(),
                        etNomeNovo.getText().toString()
                );
            }
        });

        btnDeletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletar(
                        etNomeDeleta.getText().toString(),
                        etRuaDeleta.getText().toString()
                );
            }
        });

        btnBuscar.setOnClickListener(listener(btnBuscar));

        btnListar.setOnClickListener(listener(btnListar));
        return view;
    }

    private View.OnClickListener listener(final Button b) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new RotaFragment();
                if (b.getId() == R.id.btnBuscar) {
                    Bundle op = new Bundle();
                    op.putString("op", etNomeBusca.getText().toString());
                    fragment.setArguments(op);
                }

                if (fragment != null) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }
            }
        };
    }


    public void cadastrar(String nome, int numero) {
        RestfulAPIEndereco rest = new RestfulAPIEndereco();
        Call<Endereco> cadastro = rest.insert(nome, cidade, bairro, rua, numero);

        cadastro.enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                Toast.makeText(getActivity().getBaseContext(), "Salvo com SUCESSO!", Toast.LENGTH_LONG).show();

                Fragment fragment =  new BoasVindasFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.addToBackStack(BoasVindasFragment.class.getCanonicalName());
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
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

}
