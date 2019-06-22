package br.edu.ifrs.canoas.ifcaronasolidaria.service;

/**
 * Created by mariele on 03/12/2017.
 */

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.IOException;

import br.edu.ifrs.canoas.ifcaronasolidaria.model.InteresseCarona;
import br.edu.ifrs.canoas.ifcaronasolidaria.persist.RestfulAPINotification;

public class InteresseCaronaService {
    private FirebaseDatabase firebase = FirebaseDatabase.getInstance("https://ifcaronasolidaria.firebaseio.com");
    private DatabaseReference rootRef = firebase.getReference().child("interesse-carona");

    private static InteresseCaronaService instance = null;

    public static InteresseCaronaService getInstance() {
        if (instance == null) {
            instance = new InteresseCaronaService();
        }
        return instance;
    }

    private InteresseCaronaService() {
    }

    public DatabaseReference pushNew(final InteresseCarona interesseCarona) {
        Gson gson = new Gson();
        final String interesseCaronaJson = gson.toJson(interesseCarona);
        new Thread(new Runnable() {
            @Override
            public void run() {
                RestfulAPINotification restfulAPINotification = new RestfulAPINotification();
                try {
                    restfulAPINotification.sendNotification(interesseCarona.getRota().getMatricula(), interesseCaronaJson, "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        DatabaseReference interesseCaronaNode = rootRef.child(interesseCarona.getRota().getMatricula()).push();

        interesseCaronaNode.setValue(interesseCaronaJson);
        return interesseCaronaNode;
    }

    public DatabaseReference getNodoInteresseCaronaReference() {
        return rootRef;
    }
}
