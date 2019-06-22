package br.edu.ifrs.canoas.ifcaronasolidaria.model;

import java.io.Serializable;

public class NotificacaoCarona implements Serializable {

    private String text;
    private InteresseCarona interesseCarona;

    public NotificacaoCarona(String text, InteresseCarona interesseCarona) {
        this.text = text;
        this.interesseCarona = interesseCarona;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public InteresseCarona getInteresseCarona() {
        return interesseCarona;
    }

    public void setInteresseCarona(InteresseCarona interesseCarona) {
        this.interesseCarona = interesseCarona;
    }
}
