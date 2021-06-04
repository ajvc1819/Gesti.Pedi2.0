package com.anjovaca.gestipedi.DB.Models;

public class OrderModel {
    int id, idClient, idUser;
    String date, state;
    double total;

    public OrderModel() {
    }

    public OrderModel(int id, String date, int idClient, String state, double total, int idUser) {
        this.id = id;
        this.idClient = idClient;
        this.idUser = idUser;
        this.date = date;
        this.state = state;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
