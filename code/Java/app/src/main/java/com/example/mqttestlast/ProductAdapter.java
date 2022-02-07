package com.example.mqttestlast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {


    private Context mCtx;
    private List<Product> productList;

    public ProductAdapter(Context mCtx, List<Product> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.product_list, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.name.setText(product.getName());
        holder.dateA.setText(product.getDateA());
        holder.timeB.setText(product.getTimeB());
        holder.amount.setText(product.getAmount());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView name, dateA, timeB, amount;

        public ProductViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            dateA = itemView.findViewById(R.id.dateA);
            timeB = itemView.findViewById(R.id.timeB);
            amount = itemView.findViewById(R.id.amount);
        }
    }
}
