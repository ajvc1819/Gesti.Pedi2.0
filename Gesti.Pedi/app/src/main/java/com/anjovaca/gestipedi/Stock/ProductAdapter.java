package com.anjovaca.gestipedi.Stock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anjovaca.gestipedi.DB.Models.ProductsModel;
import com.anjovaca.gestipedi.R;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements View.OnClickListener {
    public List<ProductsModel> productsModelList;
    private View.OnClickListener listener;
    private final Context context;

    //Constructor que nos permite asignar la lista.
    public ProductAdapter(List<ProductsModel> productList, Context context) {
        this.productsModelList = productList;
        this.context = context;
    }

    //Función que permitirá la creación de eventos onClick a los elementos del RecyclerView.
    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    //Función que permite la inicialización de los diferentes elementos que se mostrarán en el RecyclerView.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView price;
        private final TextView stock;
        private final ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.tvPrecioProd);
            name = itemView.findViewById(R.id.tvNombreProd);
            stock = itemView.findViewById(R.id.tvStockProd);
            image = itemView.findViewById(R.id.imgProd);
        }
    }

    //Función que permite inflar los elementos CardView que se mostrarán dentro del RecyclerView.
    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_products, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    //Función que permite rellenar los diferentes elementos que componen el CardView.
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        holder.price.setText(productsModelList.get(position).getPrice() + "€");
        holder.name.setText(productsModelList.get(position).getName());
        holder.stock.setText("Stock: " + productsModelList.get(position).getStock());
        FirebaseStorage storageReference = FirebaseStorage.getInstance();
        StorageReference storageRef = storageReference.getReference();
        storageRef.child(productsModelList.get(position).getUrlImage()).getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context).load(uri.toString()).into(holder.image));

    }

    //Función que permite asignar un listener a la hora de hacer click en algunos de los elementos que se muestran en el RecyclerView.
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    //Función que permite obtener la cuenta de elementos que se mostrarán en el RecyclerView.
    @Override
    public int getItemCount() {
        return productsModelList.size();
    }

    //Función que permite filtrar la lista que se muestra en el RecyclerView por nombre de productos.
    public void filter(List<ProductsModel> filterList) {
        this.productsModelList = filterList;
        notifyDataSetChanged();
    }
}
