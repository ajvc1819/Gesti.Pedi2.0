package com.anjovaca.gestipedi.Order;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.anjovaca.gestipedi.Category.CategoryActivity;
import com.anjovaca.gestipedi.DB.DbGestiPedi;
import com.anjovaca.gestipedi.DB.Models.OrderDetailModel;
import com.anjovaca.gestipedi.DB.Models.ProductsModel;
import com.anjovaca.gestipedi.LogIn.LogIn;
import com.anjovaca.gestipedi.LogIn.Profile;
import com.anjovaca.gestipedi.LogIn.RegisterAdministrator;
import com.anjovaca.gestipedi.Main.MainActivity;
import com.anjovaca.gestipedi.R;
import com.anjovaca.gestipedi.Stock.ProductAdapter;
import java.util.List;

public class AddProductToShoppingCart extends AppCompatActivity {
    public ProductAdapter productAdapter;
    public boolean login;
    public List<ProductsModel> productsModelList;
    public int orderId;
    SharedPreferences mPreferences;
    DbGestiPedi dbGestiPedi;
    String rol;
    public static final String EXTRA_LOGED_IN =
            "com.example.android.twoactivities.extra.login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_to_shopping_cart);
        dbGestiPedi = new DbGestiPedi(getApplicationContext());
        getPreferences();
        setRecyclerView();
    }

    //Función que permite la creación de funcionalidades de los elementos que se muestran en el menú superior.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
            return true;
        }

        if (id == R.id.initSession) {
            Intent intent;
            if (login) {
                intent = new Intent(getApplicationContext(), Profile.class);
                intent.putExtra(EXTRA_LOGED_IN, login);
            } else {
                intent = new Intent(this, LogIn.class);
            }
            startActivity(intent);
        }

        if (id == R.id.ShoppingCart) {
            Intent intent = new Intent(getApplicationContext(), ShoppingCart.class);
            startActivity(intent);
        }

        if (id == R.id.Users) {
            Intent intent = new Intent(getApplicationContext(), RegisterAdministrator.class);
            startActivity(intent);
        }

        if (id == R.id.Category) {
            Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    //Función que nos permite crear los diferentes elementos que aparecen en el menú superior.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem shoppingCart = menu.findItem(R.id.ShoppingCart);
        MenuItem addAdmin = menu.findItem(R.id.Users);
        MenuItem categories = menu.findItem(R.id.Category);

        if (orderId == 0) {
            shoppingCart.setVisible(false);
        }

        if (rol == null || !rol.equals("Administrador")) {
            addAdmin.setVisible(false);
            categories.setVisible(false);
        }

        return true;
    }

    //Función que permite establecer los elementos necesarios para el funcionamiento correcto del RecyclerView.
    private void setRecyclerView() {
        final RecyclerView recyclerViewProduct = findViewById(R.id.rvProducts);
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(this));

        productsModelList = dbGestiPedi.showProducts();

        productAdapter = new ProductAdapter(dbGestiPedi.showProducts(), getApplicationContext());

        productAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int productId = productAdapter.productsModelList.get(recyclerViewProduct.getChildAdapterPosition(v)).getId();
                List<ProductsModel> productsModelList = dbGestiPedi.selectProductById(productId);
                List<OrderDetailModel> orderDetailModelList = dbGestiPedi.checkOrderDetail(productId, orderId);
                double priceProduct = productsModelList.get(0).getPrice();
                int stock = productsModelList.get(0).getStock();

                if (!orderDetailModelList.isEmpty()) {
                    int id = orderDetailModelList.get(0).getId();
                    int quantity = orderDetailModelList.get(0).getQuantity();
                    double priceDetail = orderDetailModelList.get(0).getPrice();
                    dbGestiPedi.increaseQuantity(id, stock, quantity, priceDetail, priceProduct, orderId);
                    double totalPrice = updateTotalPriceOrder();
                    dbGestiPedi.updateTotalPrice(orderId, totalPrice);
                    finish();

                } else {
                    if (stock > 0) {
                        dbGestiPedi.insertOrderDetail(priceProduct, orderId, productId);
                        double totalPrice = updateTotalPriceOrder();
                        dbGestiPedi.updateTotalPrice(orderId, totalPrice);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "No se puede añadir el producto al carrito por que no hay existencias en el almacen.", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        recyclerViewProduct.setAdapter(productAdapter);
    }

    //Función que permite la obtención de los datos almacenados en SharedPreferences.
    private void getPreferences() {
        String sharedPrefFile = "com.example.android.sharedprefs";
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        String LOG_KEY = "log";
        login = mPreferences.getBoolean(LOG_KEY, login);
        String ORDER_ID_KEY = "id";
        orderId = mPreferences.getInt(ORDER_ID_KEY, orderId);
        String ROL_KEY = "rol";
        rol = mPreferences.getString(ROL_KEY, rol);
    }

    //Función que nos permite actualizar el precio total de pedido.
    public double updateTotalPriceOrder() {
        double totalPrice = 0;
        List<OrderDetailModel> orderDetailModelList = dbGestiPedi.showOrderDetail(orderId);

        for (OrderDetailModel orderDetail : orderDetailModelList) {
            double price = orderDetail.getPrice();
            totalPrice += price;
        }

        return totalPrice;
    }

    //Función que permite regresar al menú principal al pulsar sobre el logotipo de la empresa.
    public void returnMainMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}