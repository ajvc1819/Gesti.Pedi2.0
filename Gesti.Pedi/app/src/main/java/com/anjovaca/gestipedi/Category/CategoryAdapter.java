package com.anjovaca.gestipedi.Category;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anjovaca.gestipedi.DB.Models.CategoryModel;
import com.anjovaca.gestipedi.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> implements View.OnClickListener{
    Context context;
    public List<CategoryModel> categoryModelList;
    private View.OnClickListener listener;

    //Constructor que nos permite asignar la lista y el contexto que tendrá el RecyclerView.
    public CategoryAdapter(Context context, List<CategoryModel> categoryModelList) {
        this.categoryModelList = categoryModelList;
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvNombreCatR);
        }
    }

    //Función que permite inflar los elementos CardView que se mostrarán dentro del RecyclerView.
    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_recycler_item, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    //Función que permite rellenar los diferentes elementos que componen el CardView.
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        holder.name.setText(categoryModelList.get(position).getName());
    }

    //Función que permite asignar un listener a la hora de hacer click en algunos de los elementos que se muestran en el RecyclerView.
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    //Función que permite obtener la cuenta de elementos que se mostrarán en el RecyclerView.
    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }
}
