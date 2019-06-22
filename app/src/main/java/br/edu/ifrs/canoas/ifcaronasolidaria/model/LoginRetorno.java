package br.edu.ifrs.canoas.ifcaronasolidaria.model;

/**
 * Created by pc on 25/09/2017.
 */

public class LoginRetorno {
    private String error;
    private String token = "Nenhum";

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String toString() {
        return token + " " + error;
    }
}
