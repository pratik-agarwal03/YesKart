package com.example.yeskart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> productNames, productDescriptions, prices, images;
    private OnItemClickListener listener;

    public MyAdapter(Context ct, ArrayList<String> productNames, ArrayList<String> productDescriptions, ArrayList<String> prices, ArrayList<String> images) {
        this.context = ct;
        this.productNames = productNames;
        this.productDescriptions = productDescriptions;
        this.prices = prices;
        this.images = images;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.product_item_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.pn.setText(productNames.get(position));
        holder.pd.setText(productDescriptions.get(position));
        holder.price.setText(prices.get(position));
        Picasso.get()
                .load(images.get(position))
                .into(holder.pI);
    }

    @Override
    public int getItemCount() {
        return prices.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView pn, pd, price;
        ImageView pI;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            pn = (TextView) itemView.findViewById(R.id.my_product_name);
            pd = (TextView) itemView.findViewById(R.id.my_product_description);
            price = (TextView) itemView.findViewById(R.id.my_product_price);
            pI = (ImageView) itemView.findViewById(R.id.my_product_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(position);
                    }
                }
            });
        }

    }

}
