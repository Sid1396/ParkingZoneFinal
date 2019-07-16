package com.zone.parking.parkingzone;

public class User {
    public String name, email,carnumber,phno,points,slot;


    public  User(){

    }


    public User(String name, String email, String carnumber, String phno, String points, String slot) {
        this.name = name;
        this.email = email;
        this.carnumber = carnumber;
        this.phno = phno;
        this.points = points;
        this.slot = slot;
    }

    public String getName() {
        return name;
    }

    public  String getSlot() {
        return  slot;
    }
    public String getEmail() {
        return email;
    }

    public String getCarnumber() {
        return carnumber;
    }

    public String getPhno() {
        return phno;
    }

    public String getPoints() {
        return points;
    }

}
