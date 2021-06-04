package com.anjovaca.gestipedi.Category;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.anjovaca.gestipedi.Client.ClientActivity;
import com.anjovaca.gestipedi.DB.DbGestiPedi;
import com.anjovaca.gestipedi.DB.Models.CategoryModel;
import com.anjovaca.gestipedi.LogIn.LogIn;
import com.anjovaca.gestipedi.LogIn.Profile;
import com.anjovaca.gestipedi.LogIn.RegisterAdministrator;
import com.anjovaca.gestipedi.Main.MainActivity;
import com.anjovaca.gestipedi.Order.ShoppingCart;
import com.anjovaca.gestipedi.R;

import java.util.List;

public class CategoryDetail extends AppCompatActivity {
    public static final String EXTRA_ID =
            "com.example.android.twoactivities.extra.id";
    DbGestiPedi dbGestiPedi;
    int id;
    TextView name;
    public List<CategoryModel> categoryModelList;
    public String rol;
    public boolean login;
    public static final String EXTRA_LOGED_IN =
            "com.example.android.twoactivities.extra.login";
    int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);
        Intent intent = getIntent();
        id = intent.getIntExtra(ClientActivity.EXTRA_ID, 0);

        name = findViewById(R.id.tvmName);

        dbGestiPedi = new DbGestiPedi(getApplicationContext());
        categoryModelList = dbGestiPedi.selectCategoryById(id);

        name.setText(categoryModelList.get(0).getName());

        getPreferences();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        id = intent.getIntExtra(ClientActivity.EXTRA_ID, 0);

        name = findViewById(R.id.tvmName);

        dbGestiPedi = new DbGestiPedi(getApplicationContext());
        categoryModelList = dbGestiPedi.selectCategoryById(id);

        name.setText(categoryModelList.get(0).getName());

        getPreferences();
    }

    //Función que permite la creación de los botones que se muestran en el menú superior.
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

    //Función que permite la eliminación de una categoria de la base de datos.
    public void deleteCategory(View view) {
        dbGestiPedi.deleteCategory(id);
        finish();
    }

    //Función que permite la edición de una categoria de la base de datos.
    public void editCategory(View view) {
        Intent intent = new Intent(getApplicationContext(), EditCategory.class);
        intent.putExtra(EXTRA_ID, id);
        startActivity(intent);
    }

    //Función que permite regresar al menú principal al pulsar sobre el logotipo de la empresa.
    public void returnMainMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}