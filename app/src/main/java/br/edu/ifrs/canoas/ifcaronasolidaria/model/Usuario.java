package br.edu.ifrs.canoas.ifcaronasolidaria.model;

import java.io.Serializable;

public class Usuario implements Serializable {

    private int idPerfil;
    private String matricula;
    private String nomeUser;
    private String placaCarro;
    private String modeloCor;
    private String telefone;

    public Usuario(){
    }

    public Usuario(int idPerfil, String matricula, String nomeUser, String placaCarro, String modeloCor, String telefone) {
        this.idPerfil = idPerfil;
        this.matricula = matricula;
        this.nomeUser = nomeUser;
        this.placaCarro = placaCarro;
        this.modeloCor = modeloCor;
        this.telefone = telefone;
    }

    public Usuario(int idPerfil, String matricula, String nomeUser, String placaCarro, String modeloCor) {
        this.idPerfil = idPerfil;
        this.matricula = matricula;
        this.nomeUser = nomeUser;
        this.placaCarro = placaCarro;
        this.modeloCor = modeloCor;
    }

    public int getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(int idPerfil) {
        this.idPerfil = idPerfil;
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

    public String getPlacaCarro() {
        return placaCarro;
    }

    public void setPlacaCarro(String placaCarro) {
        this.placaCarro = placaCarro;
    }

    public String getModeloCor() {
        return modeloCor;
    }

    public void setModeloCor(String modeloCor) {
        this.modeloCor = modeloCor;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public String toString() {
        return "Perfil{" +
                "idPerfil=" + idPerfil +
                ", matricula='" + matricula + '\'' +
                ", nomeUser='" + nomeUser + '\'' +
                ", placaCarro='" + placaCarro + '\'' +
                ", modeloCor='" + modeloCor + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}
