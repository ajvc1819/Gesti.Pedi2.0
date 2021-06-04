package com.anjovaca.gestipedi.Order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.anjovaca.gestipedi.DB.Models.OrderDetailModel;
import com.anjovaca.gestipedi.DB.Models.ProductsModel;
import com.anjovaca.gestipedi.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    Context context;
    public List<OrderDetailModel> orderDetailModelList;
    public List<ProductsModel> productsModelList;
    public int idOrder;
    public int stock;

    //Constructor que nos permite asignar la lista y el contexto que tendrá el RecyclerView.
    public OrderDetailAdapter(Context context, List<OrderDetailModel> orderList, List<ProductsModel> productsModelList, int idOrder) {
        this.orderDetailModelList = orderList;
        this.productsModelList = productsModelList;
        this.context = context;
        this.idOrder = idOrder;
    }

    //Función que permite inflar los elementos CardView que se mostrarán dentro del RecyclerView.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_order_detail, parent, false);
        return new ViewHolder(view);
    }

    //Función que permite rellenar los diferentes elementos que componen el CardView.
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = "", image = "";

        for (ProductsModel products : productsModelList) {
            if (products.getId() == orderDetailModelList.get(position).getIdProduct()) {
                name = products.getName();
                image = products.getImage();
                stock = products.getStock();
            }
        }

        holder.idDetail.setText(Integer.toString(orderDetailModelList.get(position).getId()));
        holder.quantity.setText(Integer.toString(orderDetailModelList.get(position).getQuantity()));
        holder.nameProd.setText(name);
        holder.price.setText(orderDetailModelList.get(position).getPrice() + "€");
        FirebaseStorage storageReference = FirebaseStorage.getInstance();
        StorageReference storageRef = storageReference.getReference();
        storageRef.child(productsModelList.get(position).getUrlImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri.toString()).into(holder.imageProd);
            }
        });
    }

    //Función que permite obtener la cuenta de elementos que se mostrarán en el RecyclerView.
    @Override
    public int getItemCount() {
        return orderDetailModelList.size();
    }

    //Función que permite la inicialización de los diferentes elementos que se mostrarán en el RecyclerView.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameProd, quantity, price, idDetail;
        public ImageView imageProd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idDetail = itemView.findViewById(R.id.tvIdDetail);
            nameProd = itemView.findViewById(R.id.tvNombreSho);
            quantity = itemView.findViewById(R.id.tvCantidadSho);
            price = itemView.findViewById(R.id.tvTotalSho);
            imageProd = itemView.findViewById(R.id.imgProdSho);
        }
    }
}
