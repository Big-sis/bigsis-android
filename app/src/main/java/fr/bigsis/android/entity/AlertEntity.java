package fr.bigsis.android.entity;

import java.util.Date;

public class AlertEntity {

    double latitudeAlert;
    double longitudeAlert;
    String imageProfile;
    String lastname;
    String firstname;
    Date dateAlert;
    Date dateEndAlert;

    public AlertEntity() {
    }

    public AlertEntity(double latitudeAlert, double longitudeAlert, String imageProfile, String lastname, String firstname, Date dateAlert, Date dateEndAlert) {
        this.latitudeAlert = latitudeAlert;
        this.longitudeAlert = longitudeAlert;
        this.imageProfile = imageProfile;
        this.lastname = lastname;
        this.firstname = firstname;
        this.dateAlert = dateAlert;
        this.dateEndAlert = dateEndAlert;
    }

    public double getLatitudeAlert() {
        return latitudeAlert;
    }

    public void setLatitudeAlert(double latitudeAlert) {
        this.latitudeAlert = latitudeAlert;
    }

    public double getLongitudeAlert() {
        return longitudeAlert;
    }

    public void setLongitudeAlert(double longitudeAlert) {
        this.longitudeAlert = longitudeAlert;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public Date getDateAlert() {
        return dateAlert;
    }

    public void setDateAlert(Date dateAlert) {
        this.dateAlert = dateAlert;
    }

    public Date getDateEndAlert() {
        return dateEndAlert;
    }

    public void setDateEndAlert(Date dateEndAlert) {
        this.dateEndAlert = dateEndAlert;
    }
}
