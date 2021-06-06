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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.anjovaca.gestipedi.Category.CategoryActivity;
import com.anjovaca.gestipedi.DB.DbGestiPedi;
import com.anjovaca.gestipedi.DB.Models.ClientModel;
import com.anjovaca.gestipedi.DB.Models.OrderModel;
import com.anjovaca.gestipedi.LogIn.LogIn;
import com.anjovaca.gestipedi.LogIn.Profile;
import com.anjovaca.gestipedi.LogIn.RegisterAdministrator;
import com.anjovaca.gestipedi.Main.MainActivity;
import com.anjovaca.gestipedi.R;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    public static final String EXTRA_ID =
            "com.example.android.twoactivities.extra.id";
    public boolean login;
    public List<OrderModel> orderModelList;
    public List<ClientModel> clientModelList;
    public String rol;
    int orderId, idUser;
    public static final String EXTRA_LOGED_IN =
            "com.example.android.twoactivities.extra.login";
    public OrderAdapter orderAdapter;
    String state;
    DbGestiPedi dbGestiPedi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        dbGestiPedi = new DbGestiPedi(getApplicationContext());
        getPreferences();
        setSpinner();
        setRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        dbGestiPedi = new DbGestiPedi(getApplicationContext());
        getPreferences();
        setSpinner();
        setRecyclerView();
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
        final RecyclerView recyclerViewOrder = findViewById(R.id.rvOrders);
        recyclerViewOrder.setLayoutManager(new LinearLayoutManager(this));

        clientModelList = dbGestiPedi.showClients();

        if (rol.equals("Administrador")) {
            orderModelList = dbGestiPedi.showOrders();
        } else {
            orderModelList = dbGestiPedi.showOrdersByUser(idUser);
        }


        orderAdapter = new OrderAdapter(OrderActivity.this, orderModelList, clientModelList);


        orderAdapter.setOnClickListener(v -> {
            int orderId = orderAdapter.orderModelList.get(recyclerViewOrder.getChildAdapterPosition(v)).getId();
            Intent intent = new Intent(getApplicationContext(), OrderDetail.class);
            intent.putExtra(EXTRA_ID, orderId);
            startActivity(intent);
        });

        recyclerViewOrder.setAdapter(orderAdapter);
    }

    //Función que permite la obtención de los datos almacenados en SharedPreferences.
    private void getPreferences() {
        String sharedPrefFile = "com.example.android.sharedprefs";
        SharedPreferences mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        String LOG_KEY = "log";
        login = mPreferences.getBoolean(LOG_KEY, login);
        String ORDER_ID_KEY = "id";
        orderId = mPreferences.getInt(ORDER_ID_KEY, orderId);
        String USER_KEY = "user";
        idUser = mPreferences.getInt(USER_KEY, idUser);
        String ROL_KEY = "rol";
        rol = mPreferences.getString(ROL_KEY, rol);
    }

    //Función que establecer los datos que se mostrarán en el Spinner.
    private void setSpinner() {
        Spinner spinner = findViewById(R.id.spnState);
        if (spinner != null) {
            spinner.setOnItemSelectedListener(this);
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.states_array, R.layout.spinner_item);

        adapter.setDropDownViewResource
                (R.layout.spinner_item);
        if (spinner != null) {
            spinner.setAdapter(adapter);
        }
    }

    //Función que permite regresar al menú principal al pulsar sobre el logotipo de la empresa.
    public void returnMainMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void filter(String state) {
        ArrayList<OrderModel> filterList = new ArrayList<>();
        if (!state.equals("Todos")) {
            for (OrderModel order : orderModelList) {
                if (order.getState().toLowerCase().contains(state.toLowerCase())) {
                    filterList.add(order);
                }
            }
        } else {
            filterList.addAll(orderModelList);
        }

        orderAdapter.filter(filterList);
    }

    public void addOrder(View view) {
        Intent intent = new Intent(getApplicationContext(), SelectClientForOrder.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        state = parent.getItemAtPosition(position).toString();
        filter(state);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}