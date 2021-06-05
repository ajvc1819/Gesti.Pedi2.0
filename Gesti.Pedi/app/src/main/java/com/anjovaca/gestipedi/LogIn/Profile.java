package com.anjovaca.gestipedi.LogIn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.anjovaca.gestipedi.Category.CategoryActivity;
import com.anjovaca.gestipedi.DB.DbGestiPedi;
import com.anjovaca.gestipedi.DB.Models.UserModel;
import com.anjovaca.gestipedi.Main.MainActivity;
import com.anjovaca.gestipedi.Order.ShoppingCart;
import com.anjovaca.gestipedi.R;

import java.util.List;

public class Profile extends AppCompatActivity {
    boolean login;
    public static final String EXTRA_LOGED_IN =
            "com.example.android.twoactivities.extra.login";

    private SharedPreferences mPreferences;
    public static final String EXTRA_ID =
            "com.example.android.twoactivities.extra.id";
    DbGestiPedi dbGestiPedi;
    int idUser;
    TextView name, lastname, dni, city, country, phone, email;
    public List<UserModel> userModelList;
    public String rol;
    int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        login = intent.getBooleanExtra(MainActivity.EXTRA_LOGED_IN, false);

        getPreferences();

        name = findViewById(R.id.tvmNombre);
        lastname = findViewById(R.id.tvmApellidos);
        dni = findViewById(R.id.tvmDni);
        city = findViewById(R.id.tvmCiudad);
        country = findViewById(R.id.tvmPais);
        phone = findViewById(R.id.tvmTelf);
        email = findViewById(R.id.tvmEmail);

        dbGestiPedi = new DbGestiPedi(getApplicationContext());

        userModelList = dbGestiPedi.getUsersById(idUser);

        name.setText(userModelList.get(0).getName());
        lastname.setText(userModelList.get(0).getLastname());
        dni.setText(userModelList.get(0).getDni());
        city.setText(userModelList.get(0).getCity());
        country.setText(userModelList.get(0).getCountry());
        phone.setText(userModelList.get(0).getPhone());
        email.setText(userModelList.get(0).getEmail());

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        login = intent.getBooleanExtra(MainActivity.EXTRA_LOGED_IN, false);

        getPreferences();

        name = findViewById(R.id.tvmNombre);
        lastname = findViewById(R.id.tvmApellidos);
        dni = findViewById(R.id.tvmDni);
        city = findViewById(R.id.tvmCiudad);
        country = findViewById(R.id.tvmPais);
        phone = findViewById(R.id.tvmTelf);
        email = findViewById(R.id.tvmEmail);

        dbGestiPedi = new DbGestiPedi(getApplicationContext());

        userModelList = dbGestiPedi.getUsersById(idUser);

        name.setText(userModelList.get(0).getName());
        lastname.setText(userModelList.get(0).getLastname());
        dni.setText(userModelList.get(0).getDni());
        city.setText(userModelList.get(0).getCity());
        country.setText(userModelList.get(0).getCountry());
        phone.setText(userModelList.get(0).getPhone());
        email.setText(userModelList.get(0).getEmail());
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
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        String USER_KEY = "user";
        idUser = mPreferences.getInt(USER_KEY, idUser);
        String LOG_KEY = "log";
        login = mPreferences.getBoolean(LOG_KEY, login);
        String ROL_KEY = "rol";
        rol = mPreferences.getString(ROL_KEY, rol);
        String ORDER_ID_KEY = "id";
        orderId = mPreferences.getInt(ORDER_ID_KEY, orderId);
    }

    //Función que permite cerrar la sesión.
    public void logOut(View view) {
        login = false;

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.clear();
        preferencesEditor.apply();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(EXTRA_LOGED_IN, login);
        startActivity(intent);
    }

    //Función que permite regresar al menú principal al pulsar sobre el logotipo de la empresa.
    public void returnMainMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    //Función permite lanzar la actividad EditProfile para editar los datos personales de un usuario.
    public void editProfile(View view) {
        Intent intent = new Intent(getApplicationContext(), EditProfile.class);
        intent.putExtra(EXTRA_ID, idUser);
        startActivity(intent);
    }
}