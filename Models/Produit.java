package com.stillvalid.asus.stillvalid.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by user on 09/07/2018.
 */

public class Produit {
    @SerializedName("ID")
    private int id;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("nom")
    @Expose
    private String nom;

    @SerializedName("enseigne")
    private String enseigne;

    @SerializedName("marque")
    private String marque;

    @SerializedName("dateAchat")
    private String dateAchat;

    @SerializedName("duree")
    private String duree;

    @SerializedName("photo")
    private String photo;

    @SerializedName("facture")
    private String facture;

    @SerializedName("dateFin")
    private String dateFin;

    @SerializedName("sav")
    private String sav;


    public Produit() {

    }

    public Produit(String nom, String enseigne,String photo,int id) {
        this.nom = nom;
        this.enseigne = enseigne;
        this.photo = photo;
        this.id=id;
    }

    public Produit(String user_id, String nom, String enseigne, String marque, String dateAchat, String duree, String photo, String facture, String dateFin, String sav) {
        this.user_id = user_id;
        this.nom = nom;
        this.enseigne = enseigne;
        this.marque = marque;
        this.dateAchat = dateAchat;
        this.duree = duree;
        this.photo = photo;
        this.facture = facture;
        this.dateFin = dateFin;
        this.sav = sav;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSav() {
        return sav;
    }

    public void setSav(String sav) {
        this.sav = sav;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEnseigne() {
        return enseigne;
    }

    public void setEnseigne(String enseigne) {
        this.enseigne = enseigne;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(String dateAchat) {
        this.dateAchat = dateAchat;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFacture() {
        return facture;
    }

    public void setFacture(String facture) {
        this.facture = facture;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }
}
