package com.anjovaca.gestipedi.DB;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.anjovaca.gestipedi.DB.Models.CategoryModel;
import com.anjovaca.gestipedi.DB.Models.ClientModel;
import com.anjovaca.gestipedi.DB.Models.ProductsModel;
import com.anjovaca.gestipedi.DB.Models.UserModel;
import com.anjovaca.gestipedi.DB.Models.OrderDetailModel;
import com.anjovaca.gestipedi.DB.Models.OrderModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DbGestiPedi extends SQLiteOpenHelper {

    private static final String USERS_TABLE_CREATE = "CREATE TABLE Users(id INTEGER PRIMARY KEY AUTOINCREMENT,dni TEXT NOT NULL, name TEXT NOT NULL, lastname TEXT NOT NULL, username TEXT NOT NULL, password TEXT NOT NULL, rol TEXT NOT NULL, phone TEXT NOT NULL, email TEXT NOT NULL, city TEXT NOT NULL, country TEXT NOT NULL)";
    private static final String PRODUCTS_TABLE_CREATE = "CREATE TABLE Products(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL,idCategory INTEGER NOT NULL, description TEXT NOT NULL, stock INTEGER NOT NULL, price DOUBLE NOT NULL, image TEXT NOT NULL, urlImage TEXT NOT NULL, FOREIGN KEY (idCategory) REFERENCES Categories(id))";
    private static final String CLIENTS_TABLE_CREATE = "CREATE TABLE Clients(id INTEGER PRIMARY KEY AUTOINCREMENT, dni TEXT NOT NULL, name TEXT NOT NULL, lastname TEXT NOT NULL, enterprise TEXT NOT NULL, address TEXT NOT NULL, cp TEXT NOT NULL, city TEXT NOT NULL, country TEXT NOT NULL, phone TEXT NOT NULL, email TEXT NOT NULL)";
    private static final String ORDERDETAIL_TABLE_CREATE = "CREATE TABLE OrderDetails(id INTEGER PRIMARY KEY AUTOINCREMENT, quantity INTEGER NOT NULL, price DOUBLE NOT NULL, idOrder INTEGER NOT NULL, idProduct INTEGER NOT NULL, FOREIGN KEY (idOrder) REFERENCES Orders(id), FOREIGN KEY (idProduct) REFERENCES Products(id))";
    private static final String ORDER_TABLE_CREATE = "CREATE TABLE Orders(id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT NOT NULL, idClient INTEGER NOT NULL, state TEXT NOT NULL, total DOUBLE NOT NULL, idUser INTEGER NOT NULL, FOREIGN KEY (idClient) REFERENCES Clients(id), FOREIGN KEY (idUser) REFERENCES Users(id))";
    private static final String CATEGORY_TABLE_CREATE = "CREATE TABLE Categories(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL)";
    private static final String DB_NAME = "Gesti.Pedi.DtBs";
    private static final int DB_VERSION = 1;

    public DbGestiPedi(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERS_TABLE_CREATE);
        db.execSQL(PRODUCTS_TABLE_CREATE);
        db.execSQL(CLIENTS_TABLE_CREATE);
        db.execSQL(ORDERDETAIL_TABLE_CREATE);
        db.execSQL(ORDER_TABLE_CREATE);
        db.execSQL(CATEGORY_TABLE_CREATE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.setForeignKeyConstraintsEnabled(true);
        } else {
            db.execSQL("PRAGMA foreign_keys=ON");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            String dropTableProducts = "DROP TABLE IF EXISTS PRODUCTS_TABLE_CREATE";
            String dropTableClients = "DROP TABLE IF EXISTS CLIENTS_TABLE_CREATE";
            String dropTableOrderDetail = "DROP TABLE IF EXISTS ORDERDETAIL_TABLE_CREATE";
            String dropTablaOrder = "DROP TABLE IF EXISTS ORDER_TABLE_CREATE";

            db.execSQL(dropTableProducts);
            db.execSQL(dropTableClients);
            db.execSQL(dropTableOrderDetail);
            db.execSQL(dropTablaOrder);

            onCreate(db);
        } catch (SQLiteException e) {
            Log.e("$TAG (onUpgrade)", e.toString());
        }
    }

    /************CONSULTAS CLIENTES**************/
    //Función que permite obtener los datos de un cliente por su id en la base de datos.
    public List<ClientModel> getClientsById(int idClient) {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * " +
                "FROM Clients WHERE id = '" + idClient + "' ", null);
        List<ClientModel> clients = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                clients.add(new ClientModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10)));
            } while ((cursor.moveToNext()));
        }
        return clients;
    }

    //Función que permite obtener todos los datos de todos los clientes de la base de datos.
    public List<ClientModel> showClients() {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * " +
                "FROM Clients", null);
        List<ClientModel> clients = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                clients.add(new ClientModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10)));
            } while ((cursor.moveToNext()));
        }
        return clients;
    }

    //Función que permite comprobar que no se pueda añadir un cliente con algunos de sus campos únicos como duplicados.
    public List<ClientModel> checkClient(String dni, String phone, String email) {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * " +
                "FROM Clients " +
                "WHERE Dni = '" + dni + "' OR phone = '" + phone + "' OR email = '" + email + "'", null);
        List<ClientModel> clients = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                clients.add(new ClientModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10)));
            } while ((cursor.moveToNext()));
        }
        return clients;
    }

    //Función que permite crear un nuevo cliente en la base de datos.
    public void insertClient(String dni, String name, String lastname, String enterprise, String address, String cp, String city, String country, String phone, String email) {
        SQLiteDatabase db = getWritableDatabase();

        if (db != null) {
            try {
                db.execSQL("INSERT INTO Clients (dni,name,lastname,enterprise,address,cp,city,country,phone,email) " +
                        "VALUES ('" + dni + "', '" + name + "','" + lastname + "','" + enterprise + "','" + address + "','" + cp + "','" + city + "','" + country + "','" + phone + "','" + email + "')");
                db.close();
            } catch (Exception ex) {
                Log.d("Tag", ex.toString());
            }
        }
    }

    //Función que permite la edición de un cliente de la base de datos por su id.
    public void editClient(int id, String dni, String name, String lastname, String enterprise, String address, String cp, String city, String country, String phone, String email) {
        SQLiteDatabase db = getWritableDatabase();

        if (db != null) {
            try {
                db.execSQL("UPDATE Clients " +
                        "SET dni = '" + dni + "', name = '" + name + "', lastname = '" + lastname + "', enterprise = '" + enterprise + "', address = '" + address + "', cp = '" + cp + "', city = '" + city + "', country = '" + country + "', phone = '" + phone + "', email = '" + email + "' " +
                        "WHERE id = '" + id + "'");
                db.close();

            } catch (Exception ex) {
                Log.d("Tag", ex.toString());
            }
        }
    }

    //Función que permite la eliminación de un cliente de la base de datos por su id.
    public void deleteClient(int idClient) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {

            db.execSQL("DELETE FROM Clients WHERE id = '" + idClient + "'");
            db.close();

        }
    }

    /************CONSULTAS USUARIOS**************/
    //Función que permite obtener todos los datos de todos los usuarios de la base de datos.
    public List<UserModel> getUsers() {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * " +
                "FROM Users", null);
        List<UserModel> users = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                users.add(new UserModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10)));
            } while ((cursor.moveToNext()));
        }
        return users;
    }

    //Función que permite el registro de un nuevo usuario a la aplicación.
    public void insertUser(String name, String lastname, String username, String password, String rol, String dni, String phone, String email, String city, String country) {
        SQLiteDatabase db = getWritableDatabase();

        if (db != null) {
            try {
                db.execSQL("INSERT INTO Users (dni,name,lastname, username,password,rol,phone, email, city, country) " +
                        "VALUES ('" + dni + "','" + name + "','" + lastname + "','" + username + "','" + password + "','" + rol + "','" + phone + "','" + email + "','" + city + "','" + country + "')");
                db.close();
            } catch (Exception ex) {
                Log.d("Tag", ex.toString());
            }
        }
    }

    //Función que permite actualizar el perfil de un usuario de la aplicación.
    public void updateProfile(int id, String name, String lastname, String dni, String city, String country, String phone, String email) {
        SQLiteDatabase db = getWritableDatabase();

        if (db != null) {
            try {
                db.execSQL("UPDATE Users " +
                        "SET name = '" + name + "', lastname ='" + lastname + "', dni ='" + dni + "', city ='" + city + "', country = '" + country + "', phone = '" + phone + "',email = '" + email +
                        "'WHERE id = '" + id + "'");
                db.close();
            } catch (Exception ex) {
                Log.d("Tag", ex.toString());
            }
        }
    }

    //Función que permite obtener los datos de un usuario por su id en la base de datos.
    public List<UserModel> getUsersById(int idUser) {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * " +
                "FROM Users WHERE id = '" + idUser + "' ", null);
        List<UserModel> users = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                users.add(new UserModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10)));
            } while ((cursor.moveToNext()));
        }
        return users;
    }

    //Función que permite realizar las comprobaciones necesarias para el inicio de sesión de un usario en la aplicación.
    public List<UserModel> initSession(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * " +
                "FROM Users " +
                "WHERE username = '" + username + "' AND password = '" + password + "'", null);
        List<UserModel> users = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                users.add(new UserModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10)));
            } while ((cursor.moveToNext()));
        }
        return users;
    }

    //Función que permite la comprobación de datos de los usuarios para evitar la duplicidad.
    public List<UserModel> checkUsers(String username) {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * " +
                "FROM Users " +
                "WHERE username = '" + username + "'", null);
        List<UserModel> users = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                users.add(new UserModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10)));
            } while ((cursor.moveToNext()));
        }
        return users;
    }

    /************CONSULTAS PRODUCTOS**************/
    //Función que permite crear nuevos productos en la base de datos.
    public void insertProduct(String name, String description, int stock, double price, String image, int category, String urlImage) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            try {
                db.execSQL("INSERT INTO Products (name,idCategory,description,stock,price,image,urlImage ) " +
                        "VALUES ('" + name + "', '" + category + "','" + description + "','" + stock + "','" + price + "','" + image + "','" + urlImage + "')");
                db.close();
            } catch (Exception ex) {
                Log.d("Tag", ex.toString());
            }
        }
    }

    //Función que permite obtener los datos de los productos relacionados con las categorías.
    public ArrayList<String> getProductWithCategoryData(int id) {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT Products.name, Products.description, Products.stock, Products.price, Categories.name, Products.urlImage  " +
                "FROM Products INNER JOIN Categories ON Products.idCategory = Categories.id " +
                "WHERE Products.id ='" + id + "'", null);

        ArrayList<String> productData = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                productData.add(cursor.getString(0));
                productData.add(cursor.getString(1));
                productData.add(Integer.toString(cursor.getInt(2)));
                productData.add(Double.toString(cursor.getDouble(3)));
                productData.add(cursor.getString(4));
                productData.add(cursor.getString(5));
            } while ((cursor.moveToNext()));
        }
        return productData;
    }

    //Función que permite mostrar los datos de todos los productos de la base de datos.
    public List<ProductsModel> showProducts() {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * " +
                "FROM Products", null);
        List<ProductsModel> products = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                products.add(new ProductsModel(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getInt(4), cursor.getDouble(5), cursor.getString(6), cursor.getString(7)));
            } while ((cursor.moveToNext()));
        }
        return products;
    }

    //Función que permite la obtención de los datos de un producto por su id.
    public List<ProductsModel> selectProductById(int idProduct) {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * " +
                "FROM Products " +
                "WHERE id = '" + idProduct + "' ", null);
        List<ProductsModel> products = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                products.add(new ProductsModel(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getInt(4), cursor.getDouble(5), cursor.getString(6), cursor.getString(7)));
            } while ((cursor.moveToNext()));
        }
        return products;
    }

    //Función que permite la edición de los datos de un producto.
    public void editProduct(int id, String name, String description, int stock, double price, String image, int category, String urlImage) {
        SQLiteDatabase db = getWritableDatabase();

        if (db != null) {
            try {
                db.execSQL("UPDATE Products " +
                        "SET name = '" + name + "', description = '" + description + "', stock = '" + stock + "', price = '" + price + "', image = '" + image + "', idCategory = '" + category + "', urlImage = '" + urlImage +
                        "'WHERE id = '" + id + "'");
                db.close();
            } catch (Exception ex) {
                Log.d("Tag", ex.toString());
            }
        }
    }

    //Función que permite la eliminación de un producto de la base de datos.
    public void deleteProduct(int idProduct, Context context) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            try {
                db.execSQL("DELETE FROM Products " +
                        "WHERE id = '" + idProduct + "'");
                db.close();
            } catch (Exception ex) {
                Toast.makeText(context, "No se puede eliminar el producto seleccionado, ya que existen datos ligados a el.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /************CONSULTAS PEDIDOS**************/
    //Función que permite obtener los datos de todos los pedidos de la base de datos.
    public List<OrderModel> showOrders() {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * " +
                "FROM Orders", null);
        List<OrderModel> orders = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                orders.add(new OrderModel(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getDouble(4), cursor.getInt(5)));
            } while ((cursor.moveToNext()));
        }
        return orders;
    }

    //Función que permite obtener los datos de los productos relacionados con las categorías.
    public ArrayList<String> getOrderWithUserAndClientData(int id) {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT Orders.id, Clients.enterprise, date, state, Users.name, total  " +
                "FROM Orders INNER JOIN Clients ON Orders.idClient = Clients.id INNER JOIN Users ON Orders.idUser = Users.id " +
                "WHERE Orders.id ='" + id + "'", null);


        ArrayList<String> orderData = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                orderData.add(Integer.toString(cursor.getInt(0)));
                orderData.add(cursor.getString(1));
                orderData.add(cursor.getString(2));
                orderData.add(cursor.getString(3));
                orderData.add(cursor.getString(4));
                orderData.add(Double.toString(cursor.getDouble(5)));
            } while ((cursor.moveToNext()));
        }
        return orderData;
    }

    //Función que permite obtener los datos de todos los pedidos de la base de datos filtrados por usuarios.
    public List<OrderModel> showOrdersByUser(int idUser) {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * " +
                "FROM Orders " +
                "WHERE idUser = '" + idUser + "'", null);
        List<OrderModel> orders = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                orders.add(new OrderModel(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getDouble(4), cursor.getInt(5)));
            } while ((cursor.moveToNext()));
        }
        return orders;
    }

    //Función que permite obtener los datos de un pedido por su id.
    public List<OrderModel> getOrderById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * " +
                "FROM Orders " +
                "WHERE id = '" + id + "'", null);
        List<OrderModel> orders = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                orders.add(new OrderModel(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getDouble(4), cursor.getInt(5)));
            } while ((cursor.moveToNext()));
        }
        return orders;
    }

    //Función que permite la creación de pedidos.
    public void insertOrder(int idClient, int idUser) {
        SQLiteDatabase db = getWritableDatabase();
        double total = 0;
        String state = "En Proceso";
        Date currentTime = new Date();
        String date = DateFormat.getDateTimeInstance().format(currentTime);

        if (db != null) {
            try {
                db.execSQL("INSERT INTO Orders (date,idClient,state,total,idUser) " +
                        "VALUES ('" + date + "', '" + idClient + "','" + state + "','" + total + "','" + idUser + "')");
                db.close();
            } catch (Exception ex) {
                Log.d("Tag", ex.toString());
            }
        }
    }

    //Función que permite la actualización del estado de un pedido.
    public void confirmOrder(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String state = "Confirmado";
        Date currentTime = new Date();
        String date = DateFormat.getDateTimeInstance().format(currentTime);

        if (db != null) {
            try {
                db.execSQL("UPDATE Orders " +
                        "SET state = '" + state + "', date = '" + date + "' WHERE id = '" + id + "'");
                db.close();
            } catch (Exception ex) {
                Log.d("Tag", ex.toString());
            }
        }
    }

    //Función que permite la actualización del estado de un pedido.
    public void sendOrder(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String state = "Enviado";

        if (db != null) {
            try {
                db.execSQL("UPDATE Orders " +
                        "SET state = '" + state + "' " +
                        "WHERE id = '" + id + "'");
                db.close();
            } catch (Exception ex) {
                Log.d("Tag", ex.toString());
            }
        }
    }

    //Función que permite la actualización del estado de un pedido.
    public void cancelOrder(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String state = "Cancelado";

        if (db != null) {
            try {
                db.execSQL("UPDATE Orders " +
                        "SET state = '" + state + "' " +
                        "WHERE id = '" + id + "'");
                db.close();
            } catch (Exception ex) {
                Log.d("Tag", ex.toString());
            }
        }
    }

    //Función que permite la eliminación de un pedido de la base de datos.
    public void deleteOrder(int id) {
        SQLiteDatabase db = getWritableDatabase();

        if (db != null) {
            try {
                db.execSQL("DELETE FROM Orders " +
                        "WHERE id = '" + id + "'");
                db.close();
            } catch (Exception ex) {
                Log.d("Tag", ex.toString());
            }
        }
    }

    //Función que permite actualizar el precio total de un pedido.
    public void updateTotalPrice(int id, double totalPrice) {
        SQLiteDatabase db = getWritableDatabase();

        if (db != null) {
            try {
                db.execSQL("UPDATE Orders " +
                        "SET total = '" + totalPrice + "' " +
                        "WHERE id = '" + id + "'");
                db.close();
            } catch (Exception ex) {
                Log.d("Tag", ex.toString());
            }
        }
    }

    /************CONSULTAS DETALLES DE PEDIDOS**************/
    //Función que permite obtener los detalles de pedido de un pedido concreto.
    public List<OrderDetailModel> showOrderDetail(int idOrder) {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * " +
                "FROM OrderDetails " +
                "WHERE idOrder = '" + idOrder + "' ", null);
        List<OrderDetailModel> details = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                details.add(new OrderDetailModel(cursor.getInt(0), cursor.getInt(1), cursor.getDouble(2), cursor.getInt(3), cursor.getInt(4)));
            } while ((cursor.moveToNext()));
        }
        return details;
    }

    //Función que permite obtener un detalle de pedido por su id.
    public List<OrderDetailModel> getOrderDetailById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * " +
                "FROM OrderDetails " +
                "WHERE id = '" + id + "' ", null);
        List<OrderDetailModel> details = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                details.add(new OrderDetailModel(cursor.getInt(0), cursor.getInt(1), cursor.getDouble(2), cursor.getInt(3), cursor.getInt(4)));
            } while ((cursor.moveToNext()));
        }
        return details;
    }

    //Función que permite la creación de un nuevo detalle de pedido.
    public void insertOrderDetail(double price, int idOrder, int idProduct) {
        SQLiteDatabase db = getWritableDatabase();
        int quantity = 1;

        if (db != null) {
            try {
                db.execSQL("INSERT INTO OrderDetails (quantity, price, idOrder, idProduct) " +
                        "VALUES ('" + quantity + "', '" + price + "','" + idOrder + "','" + idProduct + "')");
                db.close();


            } catch (Exception ex) {
                Log.d("Tag", ex.toString());
            }
        }
        updateTotalPrice(idOrder, price);
    }

    //Función que permite la eliminación de un detalle de pedido.
    public void deleteOrderDetail(int id, int quantity, double priceDetail, double priceProduct, int idOrder) {
        SQLiteDatabase db = getWritableDatabase();
        priceDetail = priceDetail - (priceProduct * quantity);

        if (db != null) {
            try {
                db.execSQL("DELETE FROM OrderDetails " +
                        "WHERE id = '" + id + "'");
                db.close();
            } catch (Exception ex) {
                Log.d("Tag", ex.toString());
            }
        }
        updateTotalPrice(idOrder, priceDetail);
    }

    //Función que permite la eliminación de un detalle de pedido por su id.
    public void deleteOrderDetailById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            try {
                db.execSQL("DELETE FROM OrderDetails " +
                        "WHERE id = '" + id + "'");
                db.close();
            } catch (Exception ex) {
                Log.d("Tag", ex.toString());
            }
        }
    }

    //Función que permite obtener los datos del ultimo pedido.
    public List<OrderModel> selectLastOrder() {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * " +
                "FROM Orders " +
                "ORDER BY id DESC LIMIT 1", null);
        List<OrderModel> orders = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                orders.add(new OrderModel(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getDouble(4), cursor.getInt(5)));
            } while ((cursor.moveToNext()));
        }
        return orders;
    }

    //Función que permite comprobar si el producto ya existe dentro del carrito de la compra.
    public List<OrderDetailModel> checkOrderDetail(int idProduct, int idOrder) {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * " +
                "FROM OrderDetails " +
                "WHERE idOrder = '" + idOrder + "' AND idProduct = '" + idProduct + "'", null);
        List<OrderDetailModel> details = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                details.add(new OrderDetailModel(cursor.getInt(0), cursor.getInt(1), cursor.getDouble(2), cursor.getInt(3), cursor.getInt(4)));
            } while ((cursor.moveToNext()));
        }
        return details;
    }

    //Función que permite el incremento de la cantidad de un producto en el carrito.
    public void increaseQuantity(int id, int stock, int quantity, double priceDetail, double priceProduct, int idOrder) {
        SQLiteDatabase db = getWritableDatabase();
        if (stock != quantity) {
            quantity = quantity + 1;
            priceDetail = priceDetail + priceProduct;
            if (db != null) {
                try {
                    db.execSQL("UPDATE OrderDetails " +
                            "SET quantity = '" + quantity + "', price = '" + priceDetail + "' " +
                            "WHERE id = '" + id + "'");
                    db.close();
                } catch (Exception ex) {
                    Log.d("Tag", ex.toString());
                }
            }
        }
        updateTotalPrice(idOrder, priceDetail);
    }

    //Función que permite la reducción de la cantidad de un producto en el carrito.
    public void decreaseQuantity(int id, int quantity, double priceDetail, double priceProduct, int idOrder) {
        SQLiteDatabase db = getWritableDatabase();
        if (quantity > 1) {
            quantity = quantity - 1;
            priceDetail = priceDetail - priceProduct;
            if (db != null) {
                try {
                    db.execSQL("UPDATE OrderDetails " +
                            "SET quantity = '" + quantity + "', price = '" + priceDetail + "' " +
                            "WHERE id = '" + id + "'");
                    db.close();
                } catch (Exception ex) {
                    Log.d("Tag", ex.toString());
                }
            }
        }
        updateTotalPrice(idOrder, priceDetail);
    }

    //Función que permite la reducción del stock de un producto.
    public void decreaseStock(int id, int quantity, int stock) {
        SQLiteDatabase db = getWritableDatabase();
        stock = stock - quantity;
        if (db != null) {
            try {
                db.execSQL("UPDATE Products " +
                        "SET stock = '" + stock + "' " +
                        "WHERE id = '" + id + "'");
                db.close();
            } catch (Exception ex) {
                Log.d("Tag", ex.toString());
            }
        }
    }

    //Función que permite el incremento del stock de un producto.
    public void increaseStock(int id, int quantity, int stock) {
        SQLiteDatabase db = getWritableDatabase();
        stock = stock + quantity;
        if (db != null) {
            try {
                db.execSQL("UPDATE Products " +
                        "SET stock = '" + stock + "' " +
                        "WHERE id = '" + id + "'");
                db.close();
            } catch (Exception ex) {
                Log.d("Tag", ex.toString());
            }
        }
    }

    /************CONSULTAS CATEGORIAS**************/
    //Función que permite la obtención de una categoría por su id.
    public List<CategoryModel> selectCategoryById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * " +
                "FROM Categories " +
                "WHERE id = '" + id + "' ", null);
        List<CategoryModel> categories = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                categories.add(new CategoryModel(cursor.getInt(0), cursor.getString(1)));
            } while ((cursor.moveToNext()));
        }
        return categories;
    }

    //Función que permite añadir nuevas categorías a la base de datos.
    public void addCategory(String name) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            try {
                db.execSQL("INSERT INTO Categories (name) " +
                        "VALUES ('" + name + "')");
                db.close();
            } catch (Exception ex) {
                Log.d("Tag", ex.toString());
            }
        }
    }

    //Función que permite la obtención de todas las categorías registradas en la base de datos.
    public List<CategoryModel> getCategories() {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * " +
                "FROM Categories", null);
        List<CategoryModel> categories = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                categories.add(new CategoryModel(cursor.getInt(0), cursor.getString(1)));
            } while ((cursor.moveToNext()));
        }
        return categories;
    }

    //Función que permite la actualización de categorías.
    public void updateCategory(int id, String name) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            try {
                db.execSQL("UPDATE Categories " +
                        "SET name = '" + name + "' " +
                        "WHERE id = '" + id + "'");
                db.close();
            } catch (Exception ex) {
                Log.d("Tag", ex.toString());
            }
        }
    }

    //Función que permite la eliminación de categorías de la base de datos.
    public void deleteCategory(int idCategory, Context context) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            try {
                db.execSQL("DELETE FROM Categories " +
                        "WHERE id = '" + idCategory + "'");
                db.close();
            } catch (Exception ex) {
                Toast.makeText(context, "No se puede eliminar el cliente seleccionado, ya que existen datos ligados a el.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
