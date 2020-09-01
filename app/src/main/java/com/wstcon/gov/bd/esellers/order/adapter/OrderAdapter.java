package com.wstcon.gov.bd.esellers.order.adapter;

import android.content.Context;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wstcon.gov.bd.esellers.R;
import com.wstcon.gov.bd.esellers.networking.RetrofitClient;
import com.wstcon.gov.bd.esellers.order.orderModel.CustomerOrder;
import com.wstcon.gov.bd.esellers.order.orderModel.OrderDetails;
import com.wstcon.gov.bd.esellers.order.orderModel.OrderDetailsRes;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private static final String TAG = "OrderAdapter ";
    private List<CustomerOrder>orderList;
    private Context context;
    private OrderDetailsClick detailsClick;

    public OrderAdapter(List<CustomerOrder> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
        detailsClick= (OrderDetailsClick) context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.single_order_row,parent,false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        final CustomerOrder order=orderList.get(position);

        switch (order.getOrderStatus()){
            case "Delivered":
                holder.statusTV.setBackgroundColor(Color.parseColor("#33cc33"));
                break;
            case "pending":
                holder.statusTV.setBackgroundColor(Color.parseColor("#ff0000"));
                break;
            case "Shipped":
                holder.statusTV.setBackgroundColor(Color.parseColor("#ffbf00"));
                break;
            case "In Process":
                holder.statusTV.setBackgroundColor(Color.parseColor("#ff4000"));
                break;
        }

        holder.statusTV.setText(order.getOrderStatus());
        holder.dateTV.setText(order.getCreatedAt());
        holder.idTV.setText("#"+order.getId());
        holder.priceTV.setText("$"+order.getOrderTotal());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, ""+order.getId(), Toast.LENGTH_SHORT).show();
                getOrderDetails(order.getId());
            }
        });

    }

    private void getOrderDetails(Integer id) {
        Call<OrderDetailsRes> call= RetrofitClient.getInstance().getApiInterface().getOrderDetails(id);
        call.enqueue(new Callback<OrderDetailsRes>() {
            @Override
            public void onResponse(Call<OrderDetailsRes> call, Response<OrderDetailsRes> response) {
                if (response.isSuccessful()){
                    OrderDetails details=response.body().getOrderDetails();
                    Log.e(TAG, "onResponse: "+details.getOrder().getOrderTotal() );
                    detailsClick.onDetailsClick(details);
//                    context.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }else
                    Log.e(TAG, "onResponse: else"+response.code() );
            }

            @Override
            public void onFailure(Call<OrderDetailsRes> call, Throwable t) {
                Log.e(TAG, "onFailure: "+t.getLocalizedMessage() );
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.e(TAG, "getItemCount: "+orderList.size() );
        return orderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearLayout;
        private TextView priceTV, dateTV, idTV, statusTV;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout=itemView.findViewById(R.id.orderLL);
            priceTV=itemView.findViewById(R.id.orderPriceTV);
            dateTV=itemView.findViewById(R.id.dateTV);
            idTV=itemView.findViewById(R.id.order_idTV);
            statusTV =itemView.findViewById(R.id.statusTV);
        }
    }

    public void updateList(List<CustomerOrder> orderList){
        this.orderList=orderList;
        notifyDataSetChanged();
    }

    public interface OrderDetailsClick{
        void onDetailsClick(OrderDetails details);
    }
}
