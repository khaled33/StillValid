package com.stillvalid.asus.stillvalid.Models;

/**
 * Created by user on 31/07/2018.
 */

public class User {

        private int  id;
        private String email;
        private String password;
        private String admin;
        private String etat;
        private String codeActivation;

        public User() {
        }
        public User(String email, String password, String admin, String etat, String codeActivation) {
            super();
            this.email = email;
            this.password = password;
            this.admin = admin;
            this.etat = etat;
            this.codeActivation = codeActivation;
        }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getAdmin() {
            return admin;
        }

        public void setAdmin(String admin) {
            this.admin = admin;
        }

        public String getEtat() {
            return etat;
        }

        public void setEtat(String etat) {
            this.etat = etat;
        }

        public String getCodeActivation() {
            return codeActivation;
        }

        public void setCodeActivation(String codeActivation) {
            this.codeActivation = codeActivation;
        }

    }