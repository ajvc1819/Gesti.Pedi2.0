package com.anjovaca.gestipedi.Order;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.anjovaca.gestipedi.DB.DbGestiPedi;
import com.anjovaca.gestipedi.DB.Models.CategoryModel;
import com.anjovaca.gestipedi.DB.Models.OrderDetailModel;
import com.anjovaca.gestipedi.DB.Models.ProductsModel;
import com.anjovaca.gestipedi.Main.MainActivity;
import com.anjovaca.gestipedi.R;
import com.anjovaca.gestipedi.Stock.ProductAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AddProductToShoppingCart extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
    public ProductAdapter productAdapter;
    public boolean login;
    public List<ProductsModel> productsModelList;
    public int orderId;
    SharedPreferences mPreferences;
    DbGestiPedi dbGestiPedi;
    String rol;
    public static final String EXTRA_LOGED_IN =
            "com.example.android.twoactivities.extra.login";
    String category;
    List<CategoryModel> categoryModelList;
    ArrayList<String> categoryList;
    EditText buscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_to_shopping_cart);
        dbGestiPedi = new DbGestiPedi(getApplicationContext());
        categoryModelList = dbGestiPedi.getCategories();
        buscar = findViewById(R.id.etBuscar);

        if (savedInstanceState != null) {
            category = savedInstanceState.getString("category");
        }

        getPreferences();
        setRecyclerView();
        getList();
        setSpinner();
        setEditTextEvent();

    }

    //Función que permite la creación de funcionalidades de los elementos que se muestran en el menú superior.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("category",category);
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
        if (!category.equals("Todos")) {
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

    //Función que permite obtener la lista de categorías.
    public void getList() {
        categoryList = new ArrayList<>();
        categoryList.add("Todos");
        for (CategoryModel category : categoryModelList) {
            categoryList.add(category.getName());
        }
    }

    //Función que permite establecer los elementos necesarios para el funcionamiento correcto del RecyclerView.
    private void setRecyclerView() {
        final RecyclerView recyclerViewProduct = findViewById(R.id.rvProducts);
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(this));

        productsModelList = dbGestiPedi.showProducts();

        productAdapter = new ProductAdapter(dbGestiPedi.showProducts(), getApplicationContext());

        productAdapter.setOnClickListener(v -> {
            int productId = productAdapter.productsModelList.get(recyclerViewProduct.getChildAdapterPosition(v)).getId();
            List<ProductsModel> productsModelList = dbGestiPedi.selectProductById(productId);
            List<OrderDetailModel> orderDetailModelList = dbGestiPedi.checkOrderDetail(productId, orderId);
            double priceProduct = productsModelList.get(0).getPrice();
            int stock = productsModelList.get(0).getStock();

            if (!orderDetailModelList.isEmpty()) {
                int id = orderDetailModelList.get(0).getId();
                int quantity = orderDetailModelList.get(0).getQuantity();
                double priceDetail = orderDetailModelList.get(0).getPrice();
                dbGestiPedi.increaseQuantity(id, stock, quantity, priceDetail, priceProduct, orderId);
                double totalPrice = updateTotalPriceOrder();
                dbGestiPedi.updateTotalPrice(orderId, totalPrice);
                finish();

            } else {
                if (stock > 0) {
                    dbGestiPedi.insertOrderDetail(priceProduct, orderId, productId);
                    double totalPrice = updateTotalPriceOrder();
                    dbGestiPedi.updateTotalPrice(orderId, totalPrice);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "No se puede añadir el producto al carrito por que no hay existencias en el almacen.", Toast.LENGTH_SHORT).show();
                }
            }


        });

        recyclerViewProduct.setAdapter(productAdapter);
    }

    //Función que permite la obtención de los datos almacenados en SharedPreferences.
    private void getPreferences() {
        String sharedPrefFile = "com.example.android.sharedprefs";
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        String LOG_KEY = "log";
        login = mPreferences.getBoolean(LOG_KEY, login);
        String ORDER_ID_KEY = "id";
        orderId = mPreferences.getInt(ORDER_ID_KEY, orderId);
        String ROL_KEY = "rol";
        rol = mPreferences.getString(ROL_KEY, rol);
    }

    //Función que permite comprobar si el carrito de la compra esta vacío antes de salir de este para eliminar el pedido.
    private void orderDetailEmpty() {
        List<OrderDetailModel> orderDetailModelList = dbGestiPedi.showOrderDetail(orderId);

        if (orderDetailModelList.isEmpty()) {
            setAlertDialogExit();
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    //Función que permitirá mostrar un mensaje con botones de entrada para confirmar la acción de eliminar el pedido activo al salir del carrito por estar vacío.
    private void setAlertDialogExit() {
        AlertDialog.Builder myAlertBuilder = new
                AlertDialog.Builder(AddProductToShoppingCart.this);

        myAlertBuilder.setTitle("Importante");
        myAlertBuilder.setMessage("El carrito esta vacío, si sale de la sección de carrito el pedido se eliminará de la base de datos, ¿Desea continuar?");

        myAlertBuilder.setPositiveButton("Confirmar", (dialog, which) -> {
            dbGestiPedi.deleteOrder(orderId);
            orderId = 0;
            SharedPreferences.Editor preferencesEditor = mPreferences.edit();
            String ORDER_ID_KEY = "id";
            preferencesEditor.putInt(ORDER_ID_KEY, orderId);
            preferencesEditor.apply();

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });
        myAlertBuilder.setNegativeButton("Cancelar", (dialog, which) -> {

        });

        myAlertBuilder.show();
    }

    //Función que nos permite actualizar el precio total de pedido.
    public double updateTotalPriceOrder() {
        double totalPrice = 0;
        List<OrderDetailModel> orderDetailModelList = dbGestiPedi.showOrderDetail(orderId);

        for (OrderDetailModel orderDetail : orderDetailModelList) {
            double price = orderDetail.getPrice();
            totalPrice += price;
        }

        return totalPrice;
    }

    //Función que permite regresar al menú principal al pulsar sobre el logotipo de la empresa.
    public void returnMainMenu(View view) {
        orderDetailEmpty();
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
}