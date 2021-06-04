package com.anjovaca.gestipedi.LogIn;

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
import com.anjovaca.gestipedi.DB.DbGestiPedi;
import com.anjovaca.gestipedi.DB.Models.UserModel;
import com.anjovaca.gestipedi.Main.MainActivity;
import com.anjovaca.gestipedi.Order.ShoppingCart;
import com.anjovaca.gestipedi.R;

import java.util.List;

public class RegisterAdministrator extends AppCompatActivity {
    public List<UserModel> userModelList;
    DbGestiPedi dbGestiPedi;
    EditText username, password, name, lastName, dni, city, country, phone, email;
    boolean login;
    int orderId;
    String rol;
    public static final String EXTRA_LOGED_IN =
            "com.example.android.twoactivities.extra.login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_administrator);
        dbGestiPedi = new DbGestiPedi(getApplicationContext());

        username = findViewById(R.id.etUser);
        password = findViewById(R.id.etPass);
        name = findViewById(R.id.etNombre);
        lastName = findViewById(R.id.etApellidos);
        dni = findViewById(R.id.etDni);
        city = findViewById(R.id.etCiudad);
        country = findViewById(R.id.etPais);
        phone = findViewById(R.id.etTelf);
        email = findViewById(R.id.etEmail);

        getPreferences();
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

    //Función que permite el registro de un administrador en la base de datos.
    public void register(View view) {
        userModelList = dbGestiPedi.checkUsers(username.getText().toString());
        if (userModelList.isEmpty()) {
            boolean dniValid = dni.getText().toString().matches("^[0-9]{8}[A-Za-z]$");
            if (dni.getText().toString().length() == 9 && dniValid && !name.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty() && !city.getText().toString().isEmpty() && !country.getText().toString().isEmpty() && phone.getText().toString().length() == 9 && !email.getText().toString().isEmpty()) {
                dbGestiPedi.insertUser(name.getText().toString(), lastName.getText().toString(), username.getText().toString(), password.getText().toString(), "Administrador", dni.getText().toString(), phone.getText().toString(), email.getText().toString(), city
                        .getText().toString(), country.getText().toString());
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Ha introducido un campo vacío.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "El usuario ya existe.", Toast.LENGTH_SHORT).show();
        }
    }

    //Función que permite la cancelación de la acción y el cierre de la actividad.
    public void cancel(View view) {
        finish();
    }

    //Función que permite regresar al menú principal al pulsar sobre el logotipo de la empresa.
    public void returnMainMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}