package com.anjovaca.gestipedi.Stock;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class EditProduct extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    ImageView image;
    EditText name, description, stock, price;
    DbGestiPedi dbGestiPedi;
    int category;
    int id;
    public List<ProductsModel> productsModelList;
    List<CategoryModel> categoryModelList;
    List<CategoryModel> categoryModelListId;
    ArrayList<String> categoryList;
    boolean login;
    int orderId;
    String rol, urlImage;
    public static final String EXTRA_LOGED_IN =
            "com.example.android.twoactivities.extra.login";
    StorageReference storageReference;
    boolean pushedImage = false;
    Resources res;
    private Button btnSaveImage;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        storageReference = FirebaseStorage.getInstance().getReference();
        dbGestiPedi = new DbGestiPedi(getApplicationContext());
        Intent intent = getIntent();
        id = intent.getIntExtra(ProductDetail.EXTRA_ID, 0);

        productsModelList = dbGestiPedi.selectProductById(id);
        categoryModelList = dbGestiPedi.getCategories();
        obtenerLista();
        setSpinner();
        getPreferences();

        image = findViewById(R.id.imgImageProdE);
        name = findViewById(R.id.etNameProdE);
        description = findViewById(R.id.etDescriptionProdE);
        stock = findViewById(R.id.etStockProdE);
        price = findViewById(R.id.etPriceProdE);

        res = getResources();
        btnSaveImage = findViewById(R.id.btnPushImage);
        btnSaveImage.setBackground(ResourcesCompat.getDrawable(res, R.drawable.button_disabled, null));

        name.setText(productsModelList.get(0).getName());
        description.setText(productsModelList.get(0).getDescription());
        stock.setText(Integer.toString(productsModelList.get(0).getStock()));
        price.setText(Double.toString(productsModelList.get(0).getPrice()));
        FirebaseStorage storageReference = FirebaseStorage.getInstance();
        StorageReference storageRef = storageReference.getReference();
        storageRef.child(productsModelList.get(0).getUrlImage()).getDownloadUrl().addOnSuccessListener(uri -> Glide.with(getApplicationContext()).load(uri.toString()).into(image));
    }

    //Función que permite la edición de los datos de un producto en la base de datos.
    public void editProduct(View view) {
        int stockInt;
        double priceDouble;
        stockInt = Integer.parseInt(stock.getText().toString());
        priceDouble = Double.parseDouble(price.getText().toString());
        if (imageUri == null) {
            if (!name.getText().toString().isEmpty() && !description.getText().toString().isEmpty() && !stock.getText().toString().isEmpty() && !price.getText().toString().isEmpty()) {
                dbGestiPedi.editProduct(id, name.getText().toString(), description.getText().toString(), stockInt, priceDouble, productsModelList.get(0).getImage(), category, productsModelList.get(0).getUrlImage());
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Falta algún campo por rellenar o se ha introducido un campo erroneo.", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (!name.getText().toString().isEmpty() && !description.getText().toString().isEmpty() && !stock.getText().toString().isEmpty() && !price.getText().toString().isEmpty()) {
                if (pushedImage) {
                    StorageReference dataPath = storageReference.child("images").child(imageUri.getLastPathSegment());
                    String urlImage = dataPath.getPath();
                    dbGestiPedi.editProduct(id, name.getText().toString(), description.getText().toString(), stockInt, priceDouble, imageUri.toString(), category, urlImage);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "No se ha guardado la imagen.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Falta algún campo por rellenar o se ha introducido un campo erroneo.", Toast.LENGTH_SHORT).show();
            }
        }


    }

    //Función que establecer los datos que se mostrarán en el Spinner.
    private void setSpinner() {
        Spinner spinner = findViewById(R.id.spnCategoriasProdE);
        if (spinner != null) {
            spinner.setOnItemSelectedListener(this);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, categoryList);
        adapter.setDropDownViewResource
                (R.layout.spinner_item);
        if (spinner != null) {
            spinner.setAdapter(adapter);
        }
        categoryModelListId = dbGestiPedi.selectCategoryById(productsModelList.get(0).getCategory());

        assert spinner != null;
        selectValue(spinner, categoryModelListId.get(0).getName());
    }

    //Función que permite obtener la lista de categorias.
    public void obtenerLista() {
        categoryList = new ArrayList<>();

        for (CategoryModel category : categoryModelList) {
            categoryList.add(category.getName());
        }

    }

    //Función que permite cancelar la acción y cerrar la actividad.
    public void cancel(View view) {
        finish();
    }

    //Función que permite seleccionar una imagen de la galería.
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void selectImage(View view) {
        Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    // Función que permite establecer la imagen.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            StorageReference dataPath = storageReference.child("images").child(imageUri.getLastPathSegment());
            urlImage = dataPath.getPath();
            List<ProductsModel> repeatedImageList = dbGestiPedi.checkProductImage(urlImage);

            if (repeatedImageList.isEmpty()) {
                image.setImageURI(imageUri);
                pushedImage = false;
                btnSaveImage.setBackground(ResourcesCompat.getDrawable(res, R.drawable.buttons_style, null));

            } else {
                imageUri = null;
                Toast.makeText(getApplicationContext(), "La imagen seleccionada ya está asignada a un producto.", Toast.LENGTH_SHORT).show();
            }
        }
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

    //Función que permite la selección de categorías en el spinner.
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category = categoryModelList.get(position).getId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Función que permite mostrar la opción del spinner guaradada en la base de datos.
    private void selectValue(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
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

    //Función que permite regresar al menú principal al pulsar sobre el logotipo de la empresa.
    public void returnMainMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    //Función que permite guardar la imagen en Cloud Storage.
    public void pushImage(View view) {
        if (!pushedImage) {
            if (imageUri != null) {
                StorageReference lastImage = storageReference.child(productsModelList.get(0).getUrlImage());
                lastImage.delete();
                StorageReference dataPath = storageReference.child("images").child(imageUri.getLastPathSegment());
                dataPath.putFile(imageUri);
                pushedImage = true;
                btnSaveImage.setBackground(ResourcesCompat.getDrawable(res, R.drawable.button_disabled, null));

                dbGestiPedi.updateProductImage(id, imageUri.toString(), urlImage);
                Toast.makeText(getApplicationContext(), "Imagen guardada con exito.", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(getApplicationContext(), "No se ha seleccionado ninguna imagen o no se ha modificado la imagen.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "La imagen ya se ha guardado en la base de datos con anterioridad.", Toast.LENGTH_SHORT).show();
        }

    }
}