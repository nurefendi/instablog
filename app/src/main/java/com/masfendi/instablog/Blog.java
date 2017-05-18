package com.masfendi.instablog;

/**
 * Created by mas on 03/05/2017.
 */

public class Blog {

    private String title;
    private String description;
    private String image;
    private String username;
    private String profil_image;
    private String curen_nameUser;
    private String curent_profil;

    public Blog(){

    }

    public Blog(String title, String description, String image, String username, String profil_image, String curen_nameUser, String curent_profil) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.username = username;
        this.profil_image = profil_image;
        this.curen_nameUser = curen_nameUser;
        this.curent_profil = curent_profil;


    }

    public String getCuren_nameUser() {
        return curen_nameUser;
    }

    public void setCuren_nameUser(String curen_nameUser) {
        this.curen_nameUser = curen_nameUser;
    }

    public String getCurent_profil() {
        return curent_profil;
    }

    public void setCurent_profil(String curent_profil) {
        this.curent_profil = curent_profil;
    }

    public String getProfil_image() {
        return profil_image;
    }

    public void setProfil_image(String profil_image) {
        this.profil_image = profil_image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



}
