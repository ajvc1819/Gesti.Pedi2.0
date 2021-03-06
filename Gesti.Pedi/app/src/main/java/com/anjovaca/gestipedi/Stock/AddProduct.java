package com.anjovaca.gestipedi.Stock;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class AddProduct extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    ImageView image;
    EditText name, description, stock, price;
    DbGestiPedi dbGestiPedi;
    int category;
    List<CategoryModel> categoryModelList;
    ArrayList<String> categoryList;
    boolean login;
    int orderId;
    String rol;
    public static final String EXTRA_LOGED_IN =
            "com.example.android.twoactivities.extra.login";
    StorageReference storageReference;
    boolean pushedImage = false;
    private Button btnSaveImage;
    Resources res;
    String urlImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        storageReference = FirebaseStorage.getInstance().getReference();
        dbGestiPedi = new DbGestiPedi(getApplicationContext());
        categoryModelList = dbGestiPedi.getCategories();
        obtenerLista();
        setSpinner();
        getPreferences();
        image = findViewById(R.id.imgImagenProdA);
        name = findViewById(R.id.etNombreProdA);
        description = findViewById(R.id.etDescripcionProdA);
        stock = findViewById(R.id.etStockProdA);
        price = findViewById(R.id.etPrecioProdA);

        res = getResources();
        btnSaveImage = findViewById(R.id.btnPushImage);
        btnSaveImage.setBackground(ResourcesCompat.getDrawable(res, R.drawable.button_disabled, null));
    }

    //Funci??n que permite mostrar la imagen.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {

            imageUri = data.getData();
            StorageReference dataPath = storageReference.child("images").child(imageUri.getLastPathSegment());
            urlImage = dataPath.getPath();
            List<ProductsModel> repeatedImageList = dbGestiPedi.checkProductImage(urlImage);

            if(repeatedImageList.isEmpty()){
                image.setImageURI(imageUri);
                pushedImage = false;
                btnSaveImage.setBackground(ResourcesCompat.getDrawable(res, R.drawable.buttons_style, null));

            } else {
                imageUri = null;
                image.setImageURI(null);
                Toast.makeText(getApplicationContext(),"La imagen seleccionada ya est?? asignada a un producto.", Toast.LENGTH_SHORT).show();
                btnSaveImage.setBackground(ResourcesCompat.getDrawable(res, R.drawable.button_disabled, null));
            }


        }
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

    //Funci??n que establecer los datos que se mostrar??n en el Spinner.
    private void setSpinner() {
        Spinner spinner = findViewById(R.id.spnCategorias);
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

    //Funci??n que permite obtener la lista de categorias.
    public void obtenerLista() {
        categoryList = new ArrayList<>();

        for (CategoryModel category : categoryModelList) {
            categoryList.add(category.getName());
        }

    }

    //Funci??n que permite la selecci??n de im??genes de la galeria.
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void selectImage(View view) {
        Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    //Funci??n que permite a??adir nuevos productos a la base de datos.
    public void insertProduct(View view) {
        try {
            if (!imageUri.toString().isEmpty() && !name.getText().toString().isEmpty() && !description.getText().toString().isEmpty() && !stock.getText().toString().isEmpty() && !price.getText().toString().isEmpty()) {

                if(pushedImage){
                    int stockInt = Integer.parseInt(stock.getText().toString());
                    double priceDouble = Double.parseDouble(price.getText().toString());

                    dbGestiPedi.insertProduct(name.getText().toString(), description.getText().toString(), stockInt, priceDouble, imageUri.toString(), category, urlImage);
                    startActivity(new Intent(getApplicationContext(),StockActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "No se ha guardado la imagen.", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Falta alg??n campo por rellenar o se ha introducido un campo err??neo.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Falta alg??n campo por rellenar o se ha introducido un campo err??neo.", Toast.LENGTH_SHORT).show();
        }
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

    //Funci??n que permite obtener el valor del Spinner que se he seleccionado.
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category = categoryModelList.get(position).getId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Funci??n que permite regresar al men?? principal al pulsar sobre el logotipo de la empresa.
    public void returnMainMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    //Funci??n que permite guardar la imagen en Cloud Storage.
    public void pushImage(View view) {
        if(!pushedImage){
            if(imageUri != null){
                StorageReference dataPath = storageReference.child("images").child(imageUri.getLastPathSegment());
                dataPath.putFile(imageUri);
                pushedImage = true;
                btnSaveImage.setBackground(ResourcesCompat.getDrawable(res, R.drawable.button_disabled, null));
                Toast.makeText(getApplicationContext(),"Imagen guardada con exito.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),"No se ha seleccionado ninguna imagen.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),"La imagen seleccionada ya ha sido guardada con anterioridad.", Toast.LENGTH_SHORT).show();
        }


    }
}