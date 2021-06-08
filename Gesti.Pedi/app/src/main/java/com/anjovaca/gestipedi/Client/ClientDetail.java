package com.anjovaca.gestipedi.Client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anjovaca.gestipedi.Category.CategoryActivity;
import com.anjovaca.gestipedi.DB.Models.ClientModel;
import com.anjovaca.gestipedi.DB.DbGestiPedi;
import com.anjovaca.gestipedi.LogIn.LogIn;
import com.anjovaca.gestipedi.LogIn.Profile;
import com.anjovaca.gestipedi.DB.Models.OrderModel;
import com.anjovaca.gestipedi.LogIn.RegisterAdministrator;
import com.anjovaca.gestipedi.Main.MainActivity;
import com.anjovaca.gestipedi.Order.ShoppingCart;
import com.anjovaca.gestipedi.R;

import java.util.List;

public class ClientDetail extends AppCompatActivity {

    public static final String EXTRA_ID =
            "com.example.android.twoactivities.extra.id";
    DbGestiPedi dbGestiPedi;
    int id, idUser;
    TextView dni, name, lastname, enterprise, cp, address, city, country, phone, email;
    public List<ClientModel> clientModelList;
    public String rol;
    public boolean login;
    Button btnEdit, btnDelete;
    public static final String EXTRA_LOGED_IN =
            "com.example.android.twoactivities.extra.login";

    SharedPreferences mPreferences;
    int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_detail);
        Intent intent = getIntent();

        id = intent.getIntExtra(ClientActivity.EXTRA_ID, 0);
        dni = findViewById(R.id.tvmDni);
        name = findViewById(R.id.tvmNombre);
        lastname = findViewById(R.id.tvmApellidos);
        enterprise = findViewById(R.id.tvmEmpresa);
        cp = findViewById(R.id.tvmCP);
        address = findViewById(R.id.tvmDireccion);
        city = findViewById(R.id.tvmCiudad);
        country = findViewById(R.id.tvmPais);
        phone = findViewById(R.id.tvmTelf);
        email = findViewById(R.id.tvmEmail);

        dbGestiPedi = new DbGestiPedi(getApplicationContext());

        clientModelList = dbGestiPedi.getClientsById(id);

        dni.setText(clientModelList.get(0).getDni());
        name.setText(clientModelList.get(0).getName());
        lastname.setText(clientModelList.get(0).getLastname());
        enterprise.setText(clientModelList.get(0).getEnterprise());
        cp.setText(clientModelList.get(0).getCp());
        address.setText(clientModelList.get(0).getAddress());
        city.setText(clientModelList.get(0).getCity());
        country.setText(clientModelList.get(0).getCountry());
        phone.setText(clientModelList.get(0).getPhone());
        email.setText(clientModelList.get(0).getEmail());

        getPreferences();

        btnEdit = findViewById(R.id.btnEditClient);
        btnDelete = findViewById(R.id.btnDeleteClient);

        if (!rol.equals("Administrador")) {
            btnDelete.setVisibility(View.INVISIBLE);
            btnEdit.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = getIntent();
        id = intent.getIntExtra(ClientActivity.EXTRA_ID, 0);

        dni = findViewById(R.id.tvmDni);
        name = findViewById(R.id.tvmNombre);
        lastname = findViewById(R.id.tvmApellidos);
        enterprise = findViewById(R.id.tvmEmpresa);
        cp = findViewById(R.id.tvmCP);
        address = findViewById(R.id.tvmDireccion);
        city = findViewById(R.id.tvmCiudad);
        country = findViewById(R.id.tvmPais);
        phone = findViewById(R.id.tvmTelf);
        email = findViewById(R.id.tvmEmail);

        dbGestiPedi = new DbGestiPedi(getApplicationContext());

        clientModelList = dbGestiPedi.getClientsById(id);

        dni.setText(clientModelList.get(0).getDni());
        name.setText(clientModelList.get(0).getName());
        lastname.setText(clientModelList.get(0).getLastname());
        enterprise.setText(clientModelList.get(0).getEnterprise());
        cp.setText(clientModelList.get(0).getCp());
        address.setText(clientModelList.get(0).getAddress());
        city.setText(clientModelList.get(0).getCity());
        country.setText(clientModelList.get(0).getCountry());
        phone.setText(clientModelList.get(0).getPhone());
        email.setText(clientModelList.get(0).getEmail());

        getPreferences();
    }

    //Función que nos permite la creación de los elementos que aparecen en el menú superior.
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

    //Función que permite la creación de funcionalidades de los elementos que se muestran en el menú superior.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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

    //Función que permite la obtención de los datos almacenados en SharedPreferences.
    private void getPreferences() {
        String sharedPrefFile = "com.example.android.sharedprefs";
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        String LOG_KEY = "log";
        login = mPreferences.getBoolean(LOG_KEY, login);
        String ROL_KEY = "rol";
        rol = mPreferences.getString(ROL_KEY, rol);
        String ORDER_ID_KEY = "id";
        orderId = mPreferences.getInt(ORDER_ID_KEY, orderId);
    }

    //Función que permite la eliminación de un cliente de la base de datos.
    public void deleteClient(View view) {
        try{
            dbGestiPedi.deleteClient(id);
            finish();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "No se puede eliminar el cliente seleccionado, ya que existen datos ligados a el.", Toast.LENGTH_SHORT).show();
        }

    }

    //Función que nos permite hacer la llamada a la actividad EditClient.
    public void editClient(View view) {
        Intent intent = new Intent(getApplicationContext(), EditClient.class);
        intent.putExtra(EXTRA_ID, id);
        startActivity(intent);
    }

    //Función que permite la creación de un nuevo pedido.
    public void addOrder(View view) {
        String USER_KEY = "user";
        idUser = mPreferences.getInt(USER_KEY, idUser);
        int idClient = clientModelList.get(0).getId();

        dbGestiPedi.insertOrder(idClient, idUser);

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        List<OrderModel> orderDetailModelList = dbGestiPedi.selectLastOrder();
        int id = orderDetailModelList.get(0).getId();

        String ORDER_ID_KEY = "id";
        preferencesEditor.putInt(ORDER_ID_KEY, id);
        preferencesEditor.apply();

        Intent intent = new Intent(getApplicationContext(), ShoppingCart.class);
        startActivity(intent);
    }

    //Función que permite regresar al menú principal al pulsar sobre el logotipo de la empresa.
    public void returnMainMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}