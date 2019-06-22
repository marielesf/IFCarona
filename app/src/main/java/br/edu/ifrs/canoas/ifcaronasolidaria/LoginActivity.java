package br.edu.ifrs.canoas.ifcaronasolidaria;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import br.edu.ifrs.canoas.ifcaronasolidaria.model.LoginRetorno;
import br.edu.ifrs.canoas.ifcaronasolidaria.service.WebServiceUtil;
import ifcaronasolidaria.canoas.ifrs.edu.br.ifcarona.R;

public class LoginActivity extends AppCompatActivity {

    private TextView mensagem;
    private EditText usuario;
    private EditText senha;
    private CheckBox maiorIdade;
    private static Tracker mTracker;


    @Override
    public void onResume(){
        super.onResume();
        mTracker.setScreenName("Login");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        FirebaseApp.initializeApp(this);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPref.contains("TOKEN")) {
            iniciarServicoListenerInteressesCarona();
            redirecionarPaginaInicial(sharedPref.getString("TOKEN", "SEM_token"));
        } else {
            setContentView(R.layout.activity_login);
        }
    }

    public void login(View v) {
        usuario = (EditText) findViewById(R.id.textMatricula);
        senha = (EditText) findViewById(R.id.textSenha);
        maiorIdade = (CheckBox) findViewById(R.id.cbMaioridade);

        String uri = "https://moodle.canoas.ifrs.edu.br/login/token.php";

        try {
            uri += "?username=" + usuario.getText().toString() +
                    "&password=" + URLEncoder.encode(senha.getText().toString(), "utf-8") +
                    "&service=moodle_mobile_app";

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (maiorIdade.isChecked()) {
            DownloadTokenTask tarefa = new DownloadTokenTask();
            tarefa.execute(uri);
        } else {
            Toast.makeText(getApplicationContext(), "Necessário ter no mínimo 18 anos para utilizar o app.", Toast.LENGTH_LONG).show();
        }
    }

    private void processaRetorno(LoginRetorno retorno) {
        if (retorno.getToken() == "Nenhum") {
            mensagem.setText(retorno.getError());
        } else {
            SharedPreferences.Editor edt = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            edt.putString("TOKEN", retorno.getToken());
            edt.commit();
            //Vamos criar um bundle para passar as info para outra tela e como alternativa seria usar variável estática.
            iniciarServicoListenerInteressesCarona();
            redirecionarPaginaInicial(retorno.getToken());

        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void iniciarServicoListenerInteressesCarona() {
        Intent serviceIntent = new Intent(getApplicationContext(), LoginActivity.class);
        getApplicationContext().startService(serviceIntent);
    }

    private void redirecionarPaginaInicial(String token) {
        Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
        intent.putExtra("TOKEN", token);//Observar que o putExtra tem várias assinaturas
        //Log.i("token",token);
        startActivity(intent);
    }

    private class DownloadTokenTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return WebServiceUtil.getContentAsString(urls[0]);
            } catch (IOException e) {
                Log.e("Exception", e.toString());//Observe que aqui uso o log.e e não log.d
                return "Problema ao montar a requisição";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Gson g = new Gson();
            LoginRetorno retorno = g.fromJson(result.trim(), LoginRetorno.class);
            if (retorno.getToken() == "Nenhum") {
                Toast.makeText(getApplicationContext(), "Usuário ou senha inválido.", Toast.LENGTH_SHORT).show();
            } else {
                processaRetorno(retorno);

            }
        }
    }
}
