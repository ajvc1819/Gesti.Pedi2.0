package com.anjovaca.gestipedi.Order;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.anjovaca.gestipedi.DB.Models.ClientModel;
import com.anjovaca.gestipedi.DB.Models.OrderModel;
import com.anjovaca.gestipedi.R;

import java.text.DecimalFormat;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> implements View.OnClickListener {
    Context context;
    public List<OrderModel> orderModelList;
    public List<ClientModel> clientModelList;
    private View.OnClickListener listener;

    //Constructor que nos permite asignar la lista y el contexto que tendrá el RecyclerView.
    public OrderAdapter(Context context, List<OrderModel> orderList, List<ClientModel> clientList) {
        this.orderModelList = orderList;
        this.clientModelList = clientList;
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
        public TextView id, nameClient, date, state, total;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.tvIdPedido);
            nameClient = itemView.findViewById(R.id.tvNombreCliente);
            date = itemView.findViewById(R.id.tvFecha);
            state = itemView.findViewById(R.id.tvEstado);
            total = itemView.findViewById(R.id.tvTotal);
        }
    }

    //Función que permite inflar los elementos CardView que se mostrarán dentro del RecyclerView.
    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_order, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    //Función que permite rellenar los diferentes elementos que componen el CardView.
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String name = "";
        for (ClientModel client : clientModelList) {
            if (client.getId() == orderModelList.get(position).getIdClient()) {
                name = client.getEnterprise();
            }
        }

        holder.id.setText("NºPedido: " + orderModelList.get(position).getId());
        holder.nameClient.setText("Cliente: " + name);
        holder.date.setText(orderModelList.get(position).getDate());
        holder.state.setText(orderModelList.get(position).getState());
        DecimalFormat df = new DecimalFormat("#.00");
        holder.total.setText(df.format(orderModelList.get(position).getTotal()) + "€");
    }

    //Función que permite asignar un listener a la hora de hacer click en algunos de los elementos que se muestran en el RecyclerView.
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    //Función que permite obtener la cuenta de elementos que se mostrarán en el RecyclerView.
    @Override
    public int getItemCount() {
        return orderModelList.size();
    }

    //Función que permite filtrar la lista que se muestra en el RecyclerView por nombre de productos.
    public void filter(List<OrderModel> filterList) {
        this.orderModelList = filterList;
        notifyDataSetChanged();
    }
}
