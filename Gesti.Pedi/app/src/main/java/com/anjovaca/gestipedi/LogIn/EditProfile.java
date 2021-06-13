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

public class EditProfile extends AppCompatActivity {
    DbGestiPedi dbGestiPedi;
    int id;
    EditText name, lastName, dni, city, country, phone, email;
    public List<UserModel> userModelList;
    boolean login;
    int orderId;
    String rol;
    public static final String EXTRA_LOGED_IN =
            "com.example.android.twoactivities.extra.login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent intent = getIntent();
        id = intent.getIntExtra(Profile.EXTRA_ID, 0);

        name = findViewById(R.id.etNombre);
        lastName = findViewById(R.id.etApellidos);
        dni = findViewById(R.id.etDni);
        city = findViewById(R.id.etCiudad);
        country = findViewById(R.id.etPais);
        phone = findViewById(R.id.etTelf);
        email = findViewById(R.id.etEmail);

        dbGestiPedi = new DbGestiPedi(getApplicationContext());

        userModelList = dbGestiPedi.getUsersById(id);

        name.setText(userModelList.get(0).getName());
        lastName.setText(userModelList.get(0).getLastname());
        dni.setText(userModelList.get(0).getDni());
        city.setText(userModelList.get(0).getCity());
        country.setText(userModelList.get(0).getCountry());
        phone.setText(userModelList.get(0).getPhone());
        email.setText(userModelList.get(0).getEmail());

        getPreferences();
    }

    //Función que permite la creación de funcionalidades de los elementos que se muestran en el menú superior.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
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
        String ORDER_ID_KEY = "id";
        orderId = mPreferences.getInt(ORDER_ID_KEY, orderId);
        String ROL_KEY = "rol";
        rol = mPreferences.getString(ROL_KEY, rol);
    }

    //Función que permite regresar al menú principal en el caso de hacer click sobre el logo de la empresa.
    public void returnMainMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    //Función que permite cancelar la acción que se está desarrollando y cerrar la actividad.
    public void cancel(View view) {
        finish();
    }

    //Función que permite la edición de los datos de un usuario.
    public void editProfile(View view) {
        if (!name.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty()) {
            dbGestiPedi.updateProfile(id, name.getText().toString(), lastName.getText().toString(), dni.getText().toString(), city.getText().toString(),country.getText().toString(),phone.getText().toString(),email.getText().toString());
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "No se han podido editar los datos del perfil.", Toast.LENGTH_SHORT).show();
        }

    }
}