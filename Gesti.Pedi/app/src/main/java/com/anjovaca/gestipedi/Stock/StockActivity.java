package com.anjovaca.gestipedi.Stock;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.anjovaca.gestipedi.Category.CategoryActivity;
import com.anjovaca.gestipedi.DB.DbGestiPedi;
import com.anjovaca.gestipedi.DB.Models.CategoryModel;
import com.anjovaca.gestipedi.DB.Models.ProductsModel;
import com.anjovaca.gestipedi.LogIn.LogIn;
import com.anjovaca.gestipedi.LogIn.Profile;
import com.anjovaca.gestipedi.LogIn.RegisterAdministrator;
import com.anjovaca.gestipedi.Main.MainActivity;
import com.anjovaca.gestipedi.Order.ShoppingCart;
import com.anjovaca.gestipedi.R;
import java.util.ArrayList;
import java.util.List;

public class StockActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
    Button btnAddProduct;
    public ProductAdapter productAdapter;
    String category;
    public List<ProductsModel> productsModelList;
    List<CategoryModel> categoryModelList;
    ArrayList<String> categoryList;
    EditText buscar;
    public boolean login;
    public String rol;

    public static final String EXTRA_LOGED_IN =
            "com.example.android.twoactivities.extra.login";
    public static final String EXTRA_PRODUCT_ID =
            "com.example.android.twoactivities.extra.ID";
    int orderId;
    DbGestiPedi dbGestiPedi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        dbGestiPedi = new DbGestiPedi(getApplicationContext());
        categoryModelList = dbGestiPedi.getCategories();
        btnAddProduct = findViewById(R.id.btnAddProduct);
        obtenerLista();
        getPreferences();
        setRecyclerView();
        setEditTextEvent();
        setSpinner();

        if(!rol.equals("Administrador")){
            btnAddProduct.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        dbGestiPedi = new DbGestiPedi(getApplicationContext());
        categoryModelList = dbGestiPedi.getCategories();
        obtenerLista();
        getPreferences();
        setRecyclerView();
        setEditTextEvent();
        setSpinner();
    }

    //Función que permite establecer los elementos necesarios para el funcionamiento correcto del RecyclerView.
    private void setRecyclerView() {
        final RecyclerView recyclerViewProduct = findViewById(R.id.rvProducts);
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(this));

        productsModelList = dbGestiPedi.showProducts();
        productAdapter = new ProductAdapter(dbGestiPedi.showProducts(), getApplicationContext());

        productAdapter.setOnClickListener(v -> {
            int productId = productAdapter.productsModelList.get(recyclerViewProduct.getChildAdapterPosition(v)).getId();
            Intent intent = new Intent(getApplicationContext(), ProductDetail.class);
            intent.putExtra(EXTRA_PRODUCT_ID, productId);
            startActivity(intent);
        });

        recyclerViewProduct.setAdapter(productAdapter);
    }

    //Función que establecer los datos que se mostrarán en el Spinner.
    private void setSpinner() {
        Spinner spinner = findViewById(R.id.spnSearchCategory);
        if (spinner != null) {
            spinner.setOnItemSelectedListener(this);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, categoryList);
        adapter.setDropDownViewResource
                (R.layout.spinner_item);
        if (spinner != null) {
            spinner.setAdapter(adapter);
        }
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

    //Función que permite establecer el evento TextChangedListener.
    private void setEditTextEvent() {
        buscar = findViewById(R.id.etBuscarN);
        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString(), category);
            }
        });
    }

    //Función que permite establecer filtros de busqueda a un RecyclerView.
    public void filter(String nombre, String category) {
        ArrayList<ProductsModel> filterList = new ArrayList<>();
        if (!category.equals("Todos") && (category != null)) {
            for (ProductsModel product : productsModelList) {
                List<CategoryModel> categoryModelList = dbGestiPedi.selectCategoryById(product.getCategory());
                if (product.getName().toLowerCase().contains(nombre.toLowerCase()) && categoryModelList.get(0).getName().toLowerCase().contains(category.toLowerCase())) {
                    filterList.add(product);
                }
            }
        } else {
            for (ProductsModel product : productsModelList) {
                if (product.getName().toLowerCase().contains(nombre.toLowerCase())) {
                    filterList.add(product);
                }
            }
        }

        productAdapter.filter(filterList);
    }

    //Función que permite obtener la lista de categorias.
    public void obtenerLista() {
        categoryList = new ArrayList<>();
        categoryList.add("Todos");
        for (CategoryModel category : categoryModelList) {
            categoryList.add(category.getName());
        }
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

    //Función que nos permite asignar las funcionalides de los diferentes elementos que aparecen en el menú superior.
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

    //Función que permite lanzar la actividad AddProduct.
    public void addProduct(View view) {
        Intent intent = new Intent(this, AddProduct.class);
        startActivity(intent);
    }

    //Función que permite obtener la opción seleccionada en un Spinner.
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category = parent.getItemAtPosition(position).toString();
        filter(buscar.getText().toString(), category);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Función que permite regresar al menú principal al pulsar sobre el logotipo de la empresa.
    public void returnMainMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}