package com.anjovaca.gestipedi.Category;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.anjovaca.gestipedi.DB.DbGestiPedi;
import com.anjovaca.gestipedi.DB.Models.CategoryModel;
import com.anjovaca.gestipedi.LogIn.LogIn;
import com.anjovaca.gestipedi.LogIn.Profile;
import com.anjovaca.gestipedi.LogIn.RegisterAdministrator;
import com.anjovaca.gestipedi.Main.MainActivity;
import com.anjovaca.gestipedi.Order.ShoppingCart;
import com.anjovaca.gestipedi.R;

import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    public static final String EXTRA_ID =
            "com.example.android.twoactivities.extra.id";
    public boolean login;
    public List<CategoryModel> categoryModelList;
    public String rol;
    int orderId;
    public static final String EXTRA_LOGED_IN =
            "com.example.android.twoactivities.extra.login";
    public CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        setRecyclerAdapter();
        getPreferences();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setRecyclerAdapter();
        getPreferences();
    }

    //Función que permite mostrar los botones del menú superior.
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

    //Función que permite dar funcionalidades a los diferentes botones que aparecen en el menú superior.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
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

    //Función que permite establecer los elementos necesarios para el funcionamiento correcto del RecyclerView.
    private void setRecyclerAdapter() {
        final RecyclerView recyclerView = findViewById(R.id.rvCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final DbGestiPedi dbGestiPedi = new DbGestiPedi(getApplicationContext());
        categoryAdapter = new CategoryAdapter(CategoryActivity.this, dbGestiPedi.getCategories());

        categoryModelList = dbGestiPedi.getCategories();

        categoryAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int categoryId = categoryAdapter.categoryModelList.get(recyclerView.getChildAdapterPosition(v)).getId();
                Intent intent = new Intent(getApplicationContext(), CategoryDetail.class);
                intent.putExtra(EXTRA_ID, categoryId);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(categoryAdapter);
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

    //Función que nos permite acceder a la actividad AddCategory.
    public void addCategory(View view) {
        Intent intent = new Intent(getApplicationContext(), AddCategory.class);
        startActivity(intent);
    }

    //Función que permite regresar al menú principal al pulsar sobre el logotipo de la empresa.
    public void returnMainMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}