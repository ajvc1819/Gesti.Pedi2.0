package com.anjovaca.gestipedi.Client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.anjovaca.gestipedi.Category.CategoryActivity;
import com.anjovaca.gestipedi.DB.DbGestiPedi;
import com.anjovaca.gestipedi.DB.Models.ClientModel;
import com.anjovaca.gestipedi.LogIn.LogIn;
import com.anjovaca.gestipedi.LogIn.Profile;
import com.anjovaca.gestipedi.LogIn.RegisterAdministrator;
import com.anjovaca.gestipedi.Main.MainActivity;
import com.anjovaca.gestipedi.Order.ShoppingCart;
import com.anjovaca.gestipedi.R;

import java.util.ArrayList;
import java.util.List;

public class ClientActivity extends AppCompatActivity {
    public Button btnAddClient;
    public static final String EXTRA_ID =
            "com.example.android.twoactivities.extra.id";
    public boolean login;
    public List<ClientModel> clientModelList;
    public String rol;
    public String ROL_KEY = "rol";
    int orderId;
    public static final String EXTRA_LOGED_IN =
            "com.example.android.twoactivities.extra.login";
    public ClientAdapter clientAdapter;
    EditText buscar;
    DbGestiPedi dbGestiPedi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        dbGestiPedi = new DbGestiPedi(getApplicationContext());

        setEditTextEvent();
        setRecyclerView();
        getPreferences();

        if (!rol.equals("Administrador")) {
            btnAddClient.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        dbGestiPedi = new DbGestiPedi(getApplicationContext());
        setEditTextEvent();
        setRecyclerView();
        getPreferences();
    }

    //Función que nos permite la creación de los elementos que aparecen en el menú superior.
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

    //Función que permite el filtrado de la información por un dato concreto.
    public void filtrar(String text) {
        ArrayList<ClientModel> filterList = new ArrayList<>();

        for (ClientModel client : clientModelList) {
            if (client.getEnterprise().toLowerCase().contains(text.toLowerCase())) {
                filterList.add(client);
            }
        }
        clientAdapter.filter(filterList);
    }

    //Función que permite establecer los elementos necesarios para el funcionamiento correcto del RecyclerView.
    private void setRecyclerView() {
        final RecyclerView recyclerViewClient = findViewById(R.id.rvClient);
        recyclerViewClient.setLayoutManager(new LinearLayoutManager(this));

        clientAdapter = new ClientAdapter(ClientActivity.this, dbGestiPedi.showClients());

        clientAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clientId = clientAdapter.clientModelList.get(recyclerViewClient.getChildAdapterPosition(v)).getId();
                Intent intent = new Intent(getApplicationContext(), ClientDetail.class);
                intent.putExtra(EXTRA_ID, clientId);
                startActivity(intent);
            }
        });

        recyclerViewClient.setAdapter(clientAdapter);
    }

    //Función que permite la obtención de los datos almacenados en SharedPreferences.
    private void getPreferences() {
        String sharedPrefFile = "com.example.android.sharedprefs";
        SharedPreferences mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        String LOG_KEY = "log";
        login = mPreferences.getBoolean(LOG_KEY, login);
        String ROL_KEY = "rol";
        rol = mPreferences.getString(ROL_KEY, rol);
        String ORDER_ID_KEY = "id";
        orderId = mPreferences.getInt(ORDER_ID_KEY, orderId);
    }

    //Función que permite establecer el evento TextChangedListener.
    private void setEditTextEvent() {
        buscar = findViewById(R.id.etBuscar);
        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filtrar(s.toString());
            }
        });
    }

    //Función que permite permite abrir la actividad AddClient.
    public void addClient(View view) {
        Intent intent = new Intent(this, AddClient.class);
        startActivity(intent);
    }

    //Función que permite regresar al menú principal al pulsar sobre el logotipo de la empresa.
    public void returnMainMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}