package com.anjovaca.gestipedi.Order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anjovaca.gestipedi.Category.CategoryActivity;
import com.anjovaca.gestipedi.DB.DbGestiPedi;
import com.anjovaca.gestipedi.DB.Models.OrderDetailModel;
import com.anjovaca.gestipedi.DB.Models.OrderModel;
import com.anjovaca.gestipedi.DB.Models.ProductsModel;
import com.anjovaca.gestipedi.DB.Models.UserModel;
import com.anjovaca.gestipedi.LogIn.LogIn;
import com.anjovaca.gestipedi.LogIn.Profile;
import com.anjovaca.gestipedi.LogIn.RegisterAdministrator;
import com.anjovaca.gestipedi.Main.MainActivity;
import com.anjovaca.gestipedi.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderDetail extends AppCompatActivity {

    public static final String EXTRA_ID =
            "com.example.android.twoactivities.extra.id";
    DbGestiPedi dbGestiPedi;
    int id;
    TextView idOrder, client, date, state, total, user;
    public List<OrderModel> orderModelList;
    public List<OrderDetailModel> orderDetailModelList;
    public String rol;
    String userOrderName, userLoggedName;
    int userLogged;
    public boolean login;
    Button btnEdit, btnDelete, btnCancel, btnSend;
    public static final String EXTRA_LOGED_IN =
            "com.example.android.twoactivities.extra.login";
    SharedPreferences mPreferences;
    int orderId;
    public OrderDetailAdapter orderDetailAdapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        dbGestiPedi = new DbGestiPedi(getApplicationContext());
        Intent intent = getIntent();
        id = intent.getIntExtra(OrderActivity.EXTRA_ID, 0);
        orderModelList = dbGestiPedi.getOrderById(id);
        orderDetailModelList = dbGestiPedi.showOrderDetail(id);

        idOrder = findViewById(R.id.tvmIdOrderD);
        client = findViewById(R.id.tvmClientOrderD);
        date = findViewById(R.id.tvmDateOrderD);
        state = findViewById(R.id.tvmStateOrderD);
        total = findViewById(R.id.tvmTotalOrderD);
        user = findViewById(R.id.tvmUsuarioOrderD);
        btnEdit = findViewById(R.id.btnEditOrder);
        btnDelete = findViewById(R.id.btnDeleteOrder);
        btnCancel = findViewById(R.id.btnCancelOrder);
        btnSend = findViewById(R.id.btnSendOrder);

        getOrderDetailData();
        setRecyclerView();
        getPreferences();
        Resources res = getResources();

        if (!orderModelList.get(0).getState().equals("En Proceso")) {
            btnDelete.setBackground(ResourcesCompat.getDrawable(res, R.drawable.button_disabled, null));
            btnEdit.setBackground(ResourcesCompat.getDrawable(res, R.drawable.button_disabled, null));
        }
        if (!orderModelList.get(0).getState().equals("Confirmado") || !rol.equals("Administrador")) {
            btnSend.setBackground(ResourcesCompat.getDrawable(res, R.drawable.button_disabled, null));
            btnCancel.setBackground(ResourcesCompat.getDrawable(res, R.drawable.button_disabled, null));
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onRestart() {
        super.onRestart();
        dbGestiPedi = new DbGestiPedi(getApplicationContext());
        orderModelList = dbGestiPedi.getOrderById(id);
        orderDetailModelList = dbGestiPedi.showOrderDetail(id);

        Intent intent = getIntent();

        id = intent.getIntExtra(OrderActivity.EXTRA_ID, 0);

        idOrder = findViewById(R.id.tvmIdOrderD);
        client = findViewById(R.id.tvmClientOrderD);
        date = findViewById(R.id.tvmDateOrderD);
        state = findViewById(R.id.tvmStateOrderD);
        total = findViewById(R.id.tvmTotalOrderD);
        btnEdit = findViewById(R.id.btnEditOrder);
        btnDelete = findViewById(R.id.btnDeleteOrder);
        btnCancel = findViewById(R.id.btnCancelOrder);
        btnSend = findViewById(R.id.btnSendOrder);

        getOrderDetailData();
        setRecyclerView();
        getPreferences();
        Resources res = getResources();

        if (!orderModelList.get(0).getState().equals("En Proceso") || !orderModelList.get(0).getState().equals("Cancelado")) {
            btnDelete.setBackground(ResourcesCompat.getDrawable(res, R.drawable.button_disabled, null));
            btnEdit.setBackground(ResourcesCompat.getDrawable(res, R.drawable.button_disabled, null));
        }

        if (!orderModelList.get(0).getState().equals("Confirmado") && !rol.equals("Administrador")) {
            btnSend.setBackground(ResourcesCompat.getDrawable(res, R.drawable.button_disabled, null));
            btnCancel.setBackground(ResourcesCompat.getDrawable(res, R.drawable.button_disabled, null));
        }
    }

    //Función que nos permite crear los diferentes elementos que aparecen en el menú superior.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    //Función que permite la creación de funcionalidades de los elementos que se muestran en el menú superior.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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

    //Función que permite establecer los elementos necesarios para el funcionamiento correcto del RecyclerView.
    private void setRecyclerView() {
        final RecyclerView recyclerViewOrder = findViewById(R.id.rvProductsOrder);

        orderDetailAdapter = new OrderDetailAdapter(OrderDetail.this, orderDetailModelList, dbGestiPedi.showProducts(), id);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerViewOrder.setLayoutManager(manager);
        recyclerViewOrder.setHasFixedSize(true);
        recyclerViewOrder.setAdapter(orderDetailAdapter);

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
        String USER_KEY = "user";
        userLogged = mPreferences.getInt(USER_KEY, userLogged);
    }

    //Función que permite la edición de un pedido en proceso.
    public void editOrder(View view) {
        List<UserModel> userModelList = dbGestiPedi.getUsersById(userLogged);
        userLoggedName = userModelList.get(0).getName();
        if (orderModelList.get(0).getState().equals("En Proceso")) {
            if ((orderId == 0 || orderId == id)) {
                if (userLoggedName.equals(userOrderName)) {
                    orderId = id;
                    Intent intent = new Intent(getApplicationContext(), ShoppingCart.class);
                    SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                    String ORDER_ID_KEY = "id";
                    preferencesEditor.putInt(ORDER_ID_KEY, orderId);
                    preferencesEditor.apply();
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "No puedes editar un pedido creado por otro usuario.", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), "No puedes editar el pedido, ya que ya existe otro activo.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No puedes editar el pedido, ya que no está en proceso.", Toast.LENGTH_SHORT).show();
        }

    }

    //Función que permite eliminar un pedido.
    public void deleteOrder(View view) {

        if (orderModelList.get(0).getState().equals("En Proceso")) {
            for (OrderDetailModel orderDetailModel : orderDetailModelList) {
                dbGestiPedi.deleteOrderDetailById(orderDetailModel.getId());
            }

            dbGestiPedi.deleteOrder(id);

            orderId = 0;
            SharedPreferences.Editor preferencesEditor = mPreferences.edit();
            String ORDER_ID_KEY = "id";
            preferencesEditor.putInt(ORDER_ID_KEY, orderId);
            preferencesEditor.apply();
            Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "No puedes eliminar el pedido, ya que no está en proceso.", Toast.LENGTH_SHORT).show();
        }

    }

    //Función que permite enviar un pedido.
    public void sendOrder(View view) {
        if (orderModelList.get(0).getState().equals("Confirmado")) {
            if (rol.equals("Administrador")) {
                dbGestiPedi.sendOrder(id);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "No tienes permisos para enviar el pedido.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No puedes enviar el pedido, ya que no está confirmado.", Toast.LENGTH_SHORT).show();
        }

    }

    //Función que permite cancelar un pedido.
    public void cancelOrder(View view) {
        List<OrderDetailModel> orderDetailModelList = dbGestiPedi.showOrderDetail(id);
        if (orderModelList.get(0).getState().equals("Confirmado")) {
            if (rol.equals("Administrador")) {
                for (OrderDetailModel orderDetailModel : orderDetailModelList) {
                    int quantity = orderDetailModel.getQuantity();
                    int idProd = orderDetailModel.getIdProduct();

                    List<ProductsModel> products = dbGestiPedi.selectProductById(idProd);
                    int stock = products.get(0).getStock();

                    dbGestiPedi.increaseStock(idProd, quantity, stock);

                }

                dbGestiPedi.cancelOrder(id);
                finish();

            } else {
                Toast.makeText(getApplicationContext(), "No tienes permisos para cancelar el pedido.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No puedes cancelar el pedido, ya que no está confirmado.", Toast.LENGTH_SHORT).show();
        }

    }

    //Función que permite obtener datos relativos a los detalles de los pedidos.
    @SuppressLint("SetTextI18n")
    public void getOrderDetailData() {
        ArrayList<String> orderData = dbGestiPedi.getOrderWithUserAndClientData(id);
        idOrder.setText(orderData.get(0));
        client.setText(orderData.get(1));
        date.setText(orderData.get(2));
        state.setText(orderData.get(3));
        userOrderName = orderData.get(4);
        user.setText(userOrderName);
        DecimalFormat df = new DecimalFormat("#.00");
        String totalPrice = df.format(Double.parseDouble(orderData.get(5)));
        total.setText(totalPrice + "€");
    }

    //Función que permite regresar al menú principal al pulsar sobre el logotipo de la empresa.
    public void returnMainMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}