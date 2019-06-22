package br.edu.ifrs.canoas.ifcaronasolidaria.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

public class UsuarioHelper {

    public static UsuarioMoodle getUsuarioLogado(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String usuario = sharedPref.getString("USER", "SEM_TOKEN");
        Gson g = new Gson();
        if (sharedPref.contains("TOKEN") && sharedPref.contains("USER")) {
            return g.fromJson(usuario.trim(), UsuarioMoodle.class);
        }
        else {
            return null;
        }
    }

    public static boolean verificaConexao(Context context) {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }
}
