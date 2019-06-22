package br.edu.ifrs.canoas.ifcaronasolidaria.service;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by pc on 25/09/2017.
 */

public class WebServiceUtil {

    public static String getContentAsString(String urlStr) throws IOException {
        URL url = null;
        InputStream is = null;
        try {
            try {
                String urlFinal = URLEncoder.encode(urlStr, "utf-8");// Bug de caracteres especiais na URL
                url = new URL(urlStr);
            } catch (MalformedURLException e) {
                Log.e("SENHOR PROGRAMADOR", "Você fez caca verifique");
                e.printStackTrace();
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            try {
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("DEBUG", "Resposta HTTP: " + response);

                is = conn.getInputStream();

                return readIt(is);
            }catch (Exception e){
                return "Nenhum";
            }

        } finally {
            if (is != null) {
                is.close();
            }
        }

    }

    /**
     * Método ReadIt genérico permite ler um número ilimitado de caracteres.
     *
     * @param stream
     * @return String
     * @throws IOException
     */
    private static String readIt(InputStream stream) throws IOException {
        Reader reader = null;
        StringBuffer buffer = new StringBuffer();//Objeto de que vai armazenar o resultado
        reader = new InputStreamReader(stream, "UTF-8"); //Objeto leitor
        Reader in = new BufferedReader(reader); //Converte de input em buffer
        int ch;
        while ((ch = in.read()) > -1) {//Lendo Char por char
            buffer.append((char) ch);
        }
        in.close();
        return new String(buffer);
    }
}

