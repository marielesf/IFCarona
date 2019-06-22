package br.edu.ifrs.canoas.ifcaronasolidaria.model;

import java.io.Serializable;

/**
 * Created by mariele on 25/11/2017.
 */

public class Rota implements Serializable {
    private int idRota;
    private String matricula;
    private String nomeUser;
    private int status;
    private Endereco idEnderecoInicio;
    private Endereco idEnderecoFim;
    private String horaSaida;

    public Rota(){}

    public Rota(int idRota, String matricula, String nomeUser, int status, Endereco idEnderecoInicio, Endereco idEnderecoFim, String horaSaida) {
        this.idRota = idRota;
        this.matricula = matricula;
        this.nomeUser = nomeUser;
        this.status = status;
        this.idEnderecoInicio = idEnderecoInicio;
        this.idEnderecoFim = idEnderecoFim;
        this.horaSaida = horaSaida;
    }

    public int getIdRota() {
        return idRota;
    }

    public void setIdRota(int idRota) {
        this.idRota = idRota;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNomeUser() {
        return nomeUser;
    }

    public void setNomeUser(String nomeUser) {
        this.nomeUser = nomeUser;
    }

    public int isStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isAtivo() {
        return status == 1;
    }

    public Endereco getIdEnderecoInicio() {
        return idEnderecoInicio;
    }

    public void setIdEnderecoInicio(Endereco idEnderecoInicio) {
        this.idEnderecoInicio = idEnderecoInicio;
    }

    public Endereco getIdEnderecoFim() {
        return idEnderecoFim;
    }

    public void setIdEnderecoFim(Endereco idEnderecoFim) {
        this.idEnderecoFim = idEnderecoFim;
    }

    public String getHoraSaida() {
        return horaSaida;
    }

    public void setHoraSaida(String horaSaida) {
        this.horaSaida = horaSaida;
    }

    @Override
    public String toString() {
        return " De "+idEnderecoInicio.getNome()+" até "+idEnderecoFim.getNome();
    }
}
