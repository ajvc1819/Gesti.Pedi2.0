package com.anjovaca.gestipedi.Order;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.anjovaca.gestipedi.DB.DbGestiPedi;
import com.anjovaca.gestipedi.DB.Models.OrderDetailModel;
import com.anjovaca.gestipedi.DB.Models.ProductsModel;
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
    protected void onRestart() {
        super.onRestart();
        dbGestiPedi = new DbGestiPedi(getApplicationContext());
        orderDetailModelList = dbGestiPedi.showOrderDetail(orderId);
        getPreferences();
        setRecyclerView();
    }

    //Función que permite la creación de funcionalidades de los elementos que se muestran en el menú superior.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            orderDetailEmpty();

            return true;
        }

        return super.onOptionsItemSelected(item);
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

    //Función que permite comprobar si el carrito de la compra esta vacío antes de salir de este para eliminar el pedido.
    private void orderDetailEmpty() {
        List<OrderDetailModel> orderDetailModelList = dbGestiPedi.showOrderDetail(orderId);

        if (orderDetailModelList.isEmpty()) {
            setAlertDialog();
        }
        else {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
    }

    //Función que permitirá mostrar un mensaje con botones de entrada para confirmar la acción de eliminar el pedido activo al salir del carrito por estar vacío.
    private void setAlertDialog() {
        AlertDialog.Builder myAlertBuilder = new
                AlertDialog.Builder(ShoppingCart.this);

        myAlertBuilder.setTitle("Importante");
        myAlertBuilder.setMessage("El carrito esta vacío, si sale de la sección de carrito el pedido se eliminará de la base de datos, ¿Desea continuar?");

        myAlertBuilder.setPositiveButton("Confirmar", (dialog, which) -> {
            dbGestiPedi.deleteOrder(orderId);
            orderId = 0;
            SharedPreferences.Editor preferencesEditor = mPreferences.edit();
            String ORDER_ID_KEY = "id";
            preferencesEditor.putInt(ORDER_ID_KEY, orderId);
            preferencesEditor.apply();

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });
        myAlertBuilder.setNegativeButton("Cancelar", (dialog, which) -> {

        });

        myAlertBuilder.show();
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
        orderDetailEmpty();
    }
}