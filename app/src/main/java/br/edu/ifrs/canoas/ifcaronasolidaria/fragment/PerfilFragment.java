package br.edu.ifrs.canoas.ifcaronasolidaria.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import br.edu.ifrs.canoas.ifcaronasolidaria.AnalyticsApplication;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.Usuario;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.UsuarioMoodle;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.UsuarioHelper;
import br.edu.ifrs.canoas.ifcaronasolidaria.persist.RestfulAPIUsuario;
import ifcaronasolidaria.canoas.ifrs.edu.br.ifcarona.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PerfilFragment extends Fragment {

    private EditText etPlaca;
    private EditText etModeloCor;
    private EditText etTelefone;
    private EditText etUsuarioName;
    private ImageView fotoUsuario;

    private Button btnSalvar;
    private int idPerfil = -1;
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
        mTracker.setScreenName("Perfil");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_perfil, container, false);
        etPlaca = (EditText) view.findViewById(R.id.etPlaca);
        etModeloCor = (EditText) view.findViewById(R.id.etModeloCor);
        etTelefone = (EditText) view.findViewById(R.id.etTelefone);
        etUsuarioName = (EditText) view.findViewById(R.id.etNome);
        btnSalvar = (Button) view.findViewById(R.id.btnSalvar);
        fotoUsuario = (ImageView) view.findViewById(R.id.fotoUsuario);

        try {
            carregaDados();
            btnSalvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (idPerfil > -1) {
                        atualizaDados(idPerfil, etPlaca.getText().toString(),
                                etModeloCor.getText().toString(), etTelefone.getText().toString(), getContext());
                    } else {
                        cadastraDados(etPlaca.getText().toString(),
                                etModeloCor.getText().toString(), etTelefone.getText().toString());
                    }
                }
            });

        } catch (IOException e) {
            Toast.makeText(getContext(), "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return view;
    }


    private void carregaDados() throws IOException {
        final UsuarioMoodle retorno = UsuarioHelper.getUsuarioLogado(getContext());

        RestfulAPIUsuario rest = new RestfulAPIUsuario();
        Call<List<Usuario>> callperfil = rest.readQuery(retorno.getUserid());
        etUsuarioName.setText(retorno.getFullname());

        new AsyncTask<String, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... strings) {
                return getImageBitmap(retorno.getUserpictureurl().replace("http://moodle.canoas.ifrs.edu.br/", "http://moodle.canoas.ifrs.edu.br/webservice/") + "&token=" + retorno.getToken());
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                fotoUsuario.setImageBitmap(result);
            }
        }.execute();

        callperfil.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.body().size() > 0) {
                    final Usuario usuario = response.body().get(0);

                    if (usuario != null) {
                        etPlaca.setText(usuario.getPlacaCarro());
                        etModeloCor.setText(usuario.getModeloCor());
                        etTelefone.setText(usuario.getTelefone());
                        idPerfil = usuario.getIdPerfil();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                if(!UsuarioHelper.verificaConexao(getContext())){
                    Toast.makeText(getContext(), "ERRO: Sem Internet", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "ERRO: não foi possível conectar com o banco.", Toast.LENGTH_SHORT).show();
                    Log.e("ERRO", t.getMessage());
                }
            }
        });
    }

    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("ERRO-DOWNLOAD-IMAGEM", "Error getting bitmap", e);
        }
        return bm;
    }

    private void atualizaDados(final int idPerfil, final String placa, final String modeloCor, final String telefone, final Context context) {
        final Handler handler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            public void run() {
                try {
                    RestfulAPIUsuario rest = new RestfulAPIUsuario();
                    final Response<Usuario> perfil = rest.update(idPerfil, placa, modeloCor, telefone);
                    handler.post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (perfil.isSuccessful()) {

                                        Toast.makeText(context, "Atualizado com Sucesso!", Toast.LENGTH_SHORT).show();
                                    } else

                                    {
                                        Toast.makeText(context, "Erro inesperado: tente novamente", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });
                } catch (IOException e) {
                    Toast.makeText(context, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    private void cadastraDados(String placa, String modeloCor, String telefone) {
        UsuarioMoodle retorno = UsuarioHelper.getUsuarioLogado(getContext());

        RestfulAPIUsuario rest = new RestfulAPIUsuario();
        Call<Usuario> perfil = rest.insert(retorno.getUserid(), retorno.getFullname(), placa, modeloCor, telefone, "");

        perfil.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                Toast.makeText(getContext(), "Salvo com Sucesso!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
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
