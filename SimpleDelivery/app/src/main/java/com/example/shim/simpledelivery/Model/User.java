package com.example.shim.simpledelivery.Model;

public class User {

    private int id;
    private String email;
    private String phone;
    private String password;

    public User(){

    }

    public User(String email, String phone, String password) {
        this.email = email;
        this.phone = phone;
        this.password = password;
    }
}
