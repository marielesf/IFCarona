package br.edu.ifrs.canoas.ifcaronasolidaria.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import br.edu.ifrs.canoas.ifcaronasolidaria.AnalyticsApplication;
import br.edu.ifrs.canoas.ifcaronasolidaria.LoginActivity;
import br.edu.ifrs.canoas.ifcaronasolidaria.NavigationDrawerActivity;
import ifcaronasolidaria.canoas.ifrs.edu.br.ifcarona.R;

public class BoasVindasFragment extends BaseFragment {

    private Button btnBuscarCarona;
    private Button btnOfertarCaroma;
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
        mTracker.setScreenName("Home");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    @Override
    public void onBackPressed() {
        getFragmentManager().getFragments().clear();
        Intent intent = new Intent(getContext(), NavigationDrawerActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_boas_vindas, container, false);
        btnBuscarCarona = (Button) view.findViewById(R.id.btnBuscarCarona);
        btnOfertarCaroma = (Button) view.findViewById(R.id.btnOferecerCarona);
        btnOfertarCaroma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCadastroCarona();
            }
        });

        btnBuscarCarona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirBuscarCarona();
            }
        });

        return view;
    }

    public void abrirCadastroCarona() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(this.getClass().getCanonicalName());
        ft.replace(R.id.content_frame, new OfertarCaronaFragment());
        ft.commit();
    }

    public void abrirBuscarCarona() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(this.getClass().getCanonicalName());
        ft.replace(R.id.content_frame, new BuscarCaronaFragment());
        ft.commit();
    }
}
