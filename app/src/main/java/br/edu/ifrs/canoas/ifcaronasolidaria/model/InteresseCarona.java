package br.edu.ifrs.canoas.ifcaronasolidaria.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mariele on 30/11/2017.
 */

public class InteresseCarona implements Serializable {
    private Rota rota;
    private Usuario usuario;
    private String dataHora;

    public InteresseCarona(Rota rota, Usuario usuario, String dataHora) {
        this.rota = rota;
        this.usuario = usuario;
        this.dataHora = dataHora;
    }


    @Override
    public String toString() {
        return "InteresseCarona{" +
                ", rota=" + rota +
                ", usuario='" + usuario + '\'' +
                ", dataHora=" + dataHora +
                '}';
    }

    public Rota getRota() {
        return rota;
    }

    public void setRota(Rota rota) {
        this.rota = rota;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
