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

import java.util.List;

public class ShoppingCart extends AppCompatActivity {

    public static final String EXTRA_ID =
            "com.example.android.twoactivities.extra.id";
    public boolean login;
    public List<OrderDetailModel> orderDetailModelList;
    String rol;
    public DbGestiPedi dbGestiPedi;
    public static final String EXTRA_LOGED_IN =
            "com.example.android.twoactivities.extra.login";
    public ShoppingCartAdapter orderAdapter;
    int orderId;
    SharedPreferences mPreferences;
    RecyclerView recyclerViewShopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        dbGestiPedi = new DbGestiPedi(getApplicationContext());
        getPreferences();
        orderDetailModelList = dbGestiPedi.showOrderDetail(orderId);

        setRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        dbGestiPedi = new DbGestiPedi(getApplicationContext());
        orderDetailModelList = dbGestiPedi.showOrderDetail(orderId);
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
        recyclerViewShopping = findViewById(R.id.rvShoppingCart);
        recyclerViewShopping.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new ShoppingCartAdapter(ShoppingCart.this, dbGestiPedi.showOrderDetail(orderId), dbGestiPedi.showProducts(), orderId);
        recyclerViewShopping.setAdapter(orderAdapter);
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

    //Función que permite llamar a la actividad AddProductToShoppingCart.
    public void addProduct(View view) {
        Intent intent = new Intent(getApplicationContext(), AddProductToShoppingCart.class);
        startActivity(intent);
    }

    //Función que permite la confirmación del pedido que está en curso.
    public void confirmOrder(View view) {
        List<OrderDetailModel> orderDetailModelList = dbGestiPedi.showOrderDetail(orderId);
        for (OrderDetailModel orderDetailModel : orderDetailModelList) {
            int quantity = orderDetailModel.getQuantity();
            int idProd = orderDetailModel.getIdProduct();

            List<ProductsModel> products = dbGestiPedi.selectProductById(idProd);
            int stock = products.get(0).getStock();

            dbGestiPedi.decreaseStock(idProd, quantity, stock);

        }

        if (!orderDetailModelList.isEmpty()) {
            dbGestiPedi.confirmOrder(orderId);

            orderId = 0;
            SharedPreferences.Editor preferencesEditor = mPreferences.edit();
            String ORDER_ID_KEY = "id";
            preferencesEditor.putInt(ORDER_ID_KEY, orderId);
            preferencesEditor.apply();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        } else {
            Toast.makeText(getApplicationContext(), "No se puede confirmar un pedido vacío.", Toast.LENGTH_SHORT).show();
        }
    }

    //Función que permite la eliminación del pedido que se encuentra en proceso.
    public void cancelOrder(View view) {

        for (OrderDetailModel orderDetailModel : orderDetailModelList) {
            dbGestiPedi.deleteOrderDetailById(orderDetailModel.getId());
        }

        dbGestiPedi.deleteOrder(orderId);

        orderId = 0;
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        String ORDER_ID_KEY = "id";
        preferencesEditor.putInt(ORDER_ID_KEY, orderId);
        preferencesEditor.apply();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    //Función que permite regresar al menú principal al pulsar sobre el logotipo de la empresa.
    public void returnMainMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}