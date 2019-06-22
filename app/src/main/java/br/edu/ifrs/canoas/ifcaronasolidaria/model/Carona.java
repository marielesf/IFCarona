package br.edu.ifrs.canoas.ifcaronasolidaria.model;

/**
 * Created by mariele on 15/11/2017.
 */

public class Carona {
    private int idCarona;
    private int idRota;
    private int matricula;
    private String nomeUser;



    public int getIdCarona() {
        return idCarona;
    }

    public void setIdCarona(int idCarona) {
        this.idCarona = idCarona;
    }

    public int getIdRota() {
        return idRota;
    }

    public void setIdRota(int idRota) {
        this.idRota = idRota;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public String getNomeUser() {
        return nomeUser;
    }

    public void setNomeUser(String nomeUser) {
        this.nomeUser = nomeUser;
    }
}
