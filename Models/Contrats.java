package com.stillvalid.asus.stillvalid.Models;

import java.util.Date;

/**
 * Created by user on 10/07/2018.
 */

public class Contrats {

    private int id;
    private String type;
    private String dateEcheance;
    private String photo;
    private String id_user;


    public Contrats() {

    }

    public Contrats(String type, String dateEcheance,String photo,int id) {
        this.type = type;
        this.dateEcheance = dateEcheance;
        this.photo = photo;
        this.id = id;
    }

    public Contrats(String type, String dateEcheance, String photo, String id_user) {
        this.type = type;
        this.dateEcheance = dateEcheance;
        this.photo = photo;
        this.id_user = id_user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(String dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }
}
