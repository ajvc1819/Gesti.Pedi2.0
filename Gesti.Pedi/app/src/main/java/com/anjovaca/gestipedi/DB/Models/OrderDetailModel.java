package com.anjovaca.gestipedi.DB.Models;

public class OrderDetailModel {
    int id, quantity, idOrder, idProduct;
    double price;

    public OrderDetailModel() {
    }

    public OrderDetailModel(int id, int quantity, double price, int idOrder, int idProduct) {
        this.id = id;
        this.quantity = quantity;
        this.idOrder = idOrder;
        this.idProduct = idProduct;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(int idOrder) {
        this.idOrder = idOrder;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
