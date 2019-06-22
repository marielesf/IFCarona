package br.edu.ifrs.canoas.ifcaronasolidaria;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import br.edu.ifrs.canoas.ifcaronasolidaria.Notifications.NotificationControl;
import br.edu.ifrs.canoas.ifcaronasolidaria.fragment.BaseFragment;
import br.edu.ifrs.canoas.ifcaronasolidaria.fragment.BoasVindasFragment;
import br.edu.ifrs.canoas.ifcaronasolidaria.fragment.BuscarCaronaFragment;
import br.edu.ifrs.canoas.ifcaronasolidaria.fragment.ListarPassegeiroFragment;
import br.edu.ifrs.canoas.ifcaronasolidaria.fragment.OfertarCaronaFragment;
import br.edu.ifrs.canoas.ifcaronasolidaria.fragment.PerfilFragment;
import br.edu.ifrs.canoas.ifcaronasolidaria.fragment.PontoInteresseFragment;
import br.edu.ifrs.canoas.ifcaronasolidaria.fragment.RotaFragment;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.Usuario;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.UsuarioHelper;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.UsuarioMoodle;
import br.edu.ifrs.canoas.ifcaronasolidaria.persist.RestfulAPIUsuario;
import br.edu.ifrs.canoas.ifcaronasolidaria.service.WebServiceUtil;
import ifcaronasolidaria.canoas.ifrs.edu.br.ifcarona.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigationDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView nomeCompleto;
    private ImageView fotoUsuario;
    private UsuarioMoodle usuario;

    /**
     * The {@link Tracker} used to record screen views.
     */
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        setTitle("IFCaronaSolidaria");

        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nomeCompleto = (TextView) ((NavigationView) drawer.findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.fullName);
        fotoUsuario = (ImageView) ((NavigationView) drawer.findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.fotoUsuario);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        displayView(getIntent().getIntExtra("initialFragment", R.id.nav_Home));
        new DownloadDadosUserTask().execute(buildRequestUsuario());
    }

    private String buildRequestUsuario() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = sharedPref.getString("TOKEN", "SEM_token");
        return "http://moodle.canoas.ifrs.edu.br/webservice/rest/server.php?" +
                "wstoken=" + token + "&wsfunction=core_webservice_get_site_info&" +
                "moodlewsrestformat=json";
    }


    public void displayView(int viewId) {

        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.nav_Home:
                fragment = new BoasVindasFragment();
                break;
            case R.id.nav_OferecerCarona:
                fragment = new OfertarCaronaFragment();
                break;
            case R.id.nav_BuscarCarona:
                fragment = new BuscarCaronaFragment();
                break;
            case R.id.nav_Perfil:
                fragment = new PerfilFragment();
                break;
            case R.id.nav_Passageiros:
                fragment = new ListarPassegeiroFragment();
                break;
            case R.id.nav_pontos:
                fragment = new PontoInteresseFragment();
                break;
            case R.id.nav_rotas:
                fragment = new RotaFragment();
                break;
            case R.id.nav_Sair:
                SharedPreferences.Editor edt = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                edt.remove("TOKEN");
                edt.remove("USER");
                edt.commit();
                edt.apply();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
        }


        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.addToBackStack(BoasVindasFragment.class.getCanonicalName());
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            tellFragments();
            super.onBackPressed();
        }
    }

    private void tellFragments() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment f : fragments) {
            if (f != null && f instanceof BaseFragment)
                ((BaseFragment) f).onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displayView(item.getItemId());
        return true;
    }


    private class DownloadDadosUserTask extends AsyncTask<String, Void, String> {
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
            Log.d("teste", result);
            Gson g = new Gson();
            if(!UsuarioHelper.verificaConexao(getApplicationContext())){
                Toast.makeText(getApplicationContext(), "ERRO: verifique a conxão.", Toast.LENGTH_LONG).show();
            }else {
                if (!result.trim().equals("Nenhum")) {
                    UsuarioMoodle retorno = g.fromJson(result.trim(), UsuarioMoodle.class);
                    Log.d("Teste", retorno.toString());
                    processarRetorno(retorno);
                } else {
                    Toast.makeText(getApplicationContext(), "Problemas ao conectar com o moodle, tente novamente mais tarde", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    private void processarRetorno(UsuarioMoodle retorno) {
        nomeCompleto.setText(retorno.getFullname());
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = sharedPref.getString("TOKEN", "SEM_token");
        final String urlImagem = retorno.getUserpictureurl().replace("http://moodle.canoas.ifrs.edu.br/", "http://moodle.canoas.ifrs.edu.br/webservice/") + "&token=" + token;

        usuario = new UsuarioMoodle(token, retorno.getUserid(), retorno.getFullname(), retorno.getUserpictureurl());

        SharedPreferences.Editor edt = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        Gson gson = new Gson();
        String json = gson.toJson(usuario);
        edt.putString("USER", json);
        edt.commit();
        edt.apply();
        NotificationControl notificationControl = new NotificationControl();
        notificationControl.checkNotifications(this);
        final RestfulAPIUsuario rest = new RestfulAPIUsuario();
        Call<List<Usuario>> callperfil = rest.readQuery(usuario.getUserid());
        FirebaseMessaging.getInstance().subscribeToTopic(usuario.getUserid());
        callperfil.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.body().size() == 0) {
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(final InstanceIdResult instanceIdResult) {
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        rest.insert(usuario.getUserid(), usuario.getFullname(), "", "", "", instanceIdResult.getToken()).execute();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    });

                } else {

                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            final String deviceToken = instanceIdResult.getToken();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        rest.updateToken(usuario.getUserid(), deviceToken);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();

                        }
                    });


                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                if(!UsuarioHelper.verificaConexao(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "ERRO: Sem Internet", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "ERRO: não foi possível conectar com o banco.", Toast.LENGTH_SHORT).show();
                    Log.e("ERRO", t.getMessage());
                }
            }
        });


        new AsyncTask<String, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... strings) {
                return getImageBitmap(urlImagem);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                fotoUsuario.setImageBitmap(result);
            }
        }.execute();

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


}
