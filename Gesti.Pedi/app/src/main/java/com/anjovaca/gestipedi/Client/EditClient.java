package com.anjovaca.gestipedi.Client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.anjovaca.gestipedi.Category.CategoryActivity;
import com.anjovaca.gestipedi.DB.Models.ClientModel;
import com.anjovaca.gestipedi.DB.DbGestiPedi;
import com.anjovaca.gestipedi.LogIn.LogIn;
import com.anjovaca.gestipedi.LogIn.Profile;
import com.anjovaca.gestipedi.LogIn.RegisterAdministrator;
import com.anjovaca.gestipedi.Main.MainActivity;
import com.anjovaca.gestipedi.Order.ShoppingCart;
import com.anjovaca.gestipedi.R;

import java.util.List;

public class EditClient extends AppCompatActivity {
    DbGestiPedi dbGestiPedi;
    int id;
    EditText dni, name, lastname, enterprise, cp, address, city, country, phone, email;
    public List<ClientModel> clientModelList;
    boolean login;
    int orderId;
    String rol;
    public static final String EXTRA_LOGED_IN =
            "com.example.android.twoactivities.extra.login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_client);
        Intent intent = getIntent();
        id = intent.getIntExtra(ClientDetail.EXTRA_ID, 0);

        dni = findViewById(R.id.etDni);
        name = findViewById(R.id.etNombre);
        lastname = findViewById(R.id.etApellidos);
        enterprise = findViewById(R.id.etEmpresa);
        cp = findViewById(R.id.etCP);
        address = findViewById(R.id.etDireccion);
        city = findViewById(R.id.etCiudad);
        country = findViewById(R.id.etPais);
        phone = findViewById(R.id.etTelf);
        email = findViewById(R.id.etEmail);

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

    //Funci??n que permite la creaci??n de funcionalidades de los elementos que se muestran en el men?? superior.
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

    //Funci??n que nos permite crear los diferentes elementos que aparecen en el men?? superior.
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

    //Funci??n que permite la obtenci??n de los datos almacenados en SharedPreferences.
    private void getPreferences() {
        String sharedPrefFile = "com.example.android.sharedprefs";
        SharedPreferences mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        String LOG_KEY = "log";
        login = mPreferences.getBoolean(LOG_KEY, login);
        String ORDER_ID_KEY = "id";
        orderId = mPreferences.getInt(ORDER_ID_KEY, orderId);
        String ROL_KEY = "rol";
        rol = mPreferences.getString(ROL_KEY, rol);
    }

    //Funci??n que permite cancelar la acci??n y cerrar la actividad.
    public void cancel(View view) {
        finish();
    }

    //Funci??n que permite la edici??n de los datos de un cliente en la base de datos.
    public void editClient(View view) {
        boolean dniValid = dni.getText().toString().matches("^[0-9]{8}[A-Za-z]$");
        if (dni.getText().toString().length() == 9 && dniValid && !name.getText().toString().isEmpty() && !lastname.getText().toString().isEmpty() && !enterprise.getText().toString().isEmpty() && cp.getText().toString().length() == 5 && !address.getText().toString().isEmpty() && !city.getText().toString().isEmpty() && !country.getText().toString().isEmpty() && phone.getText().toString().length() == 9 && !email.getText().toString().isEmpty()) {
            dbGestiPedi.editClient(id, dni.getText().toString(), name.getText().toString(), lastname.getText().toString(), enterprise.getText().toString(), address.getText().toString(), cp.getText().toString(), city.getText().toString(), country.getText().toString(), phone.getText().toString(), email.getText().toString());
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "No se han podido editar los datos del cliente.", Toast.LENGTH_SHORT).show();
        }

    }

    //Funci??n que permite regresar al men?? principal al pulsar sobre el logotipo de la empresa.
    public void returnMainMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}