package com.example.shim.simpledelivery.Model;

public class User {

    int id;
    String email;
    String phone;
    String password;

    public User(){

    }

    public User(String email, String phone, String password) {
        this.email = email;
        this.phone = phone;
        this.password = password;
    }
}
