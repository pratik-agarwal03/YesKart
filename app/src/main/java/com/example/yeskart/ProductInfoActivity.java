package com.example.yeskart;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.Random;

public class ProductInfoActivity extends AppCompatActivity {

    static int order_quantity = 1;
    TextView name, desc, price, qty, order;
    URL url;
    ImageView img;
    int max = 13, min = 3;
    Button buy, minus, plus;
    TextView delivery_date;
    String email;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        name = findViewById(R.id.pName);
        buy = findViewById(R.id.Buy);
        minus = findViewById(R.id.minus);
        order = findViewById(R.id.order_qty);
        plus = findViewById(R.id.plus);
        desc = findViewById(R.id.pDesc);
        qty = findViewById(R.id.availableInStock);
        price = findViewById(R.id.pPrice);
        img = findViewById(R.id.pImg);
        delivery_date = findViewById(R.id.expected_date);
        Intent i = getIntent();
        final String product_name = i.getStringExtra("Name");
        final String product_desc = i.getStringExtra("Description");
        final String p = i.getStringExtra("Price");
        final String im = i.getStringExtra("Image");
        final String quantity = i.getStringExtra("Qty");
        if (quantity.equals("0")) {
            qty.setText("Out of Stock!");
        } else {
            qty.setText(quantity);
        }
        name.setText(product_name);
        desc.setText(product_desc);
        price.setText(p);
        email = SplashScreen.auth.getCurrentUser().getEmail();
        Picasso.get().load(im)
                .into(img);
        LocalDate currDate = LocalDate.now();
        Random rand = new Random();
        int days = rand.nextInt((max - min) + 1) + min;
        LocalDate deliveryBy = currDate.plusDays(days);
        Month month = deliveryBy.getMonth();
        int date = deliveryBy.getDayOfMonth();
        int year = deliveryBy.getYear();
        delivery_date.setText(date + " " + month.toString() + " " + year);

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProductInfoActivity.this, PaymentActivity.class);
                i.putExtra("Name", product_name);
                i.putExtra("Desc", product_desc);
                i.putExtra("Qty", order.getText().toString());
                i.putExtra("Price", p);
                i.putExtra("Image", im);
                i.putExtra("Email", email);
                startActivity(i);
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (order.getText().toString().equals("1")) {
                    Toast.makeText(ProductInfoActivity.this, "Cannot Reduce Quantity!", Toast.LENGTH_SHORT).show();
                } else if (quantity.equals("0")) {
                    Toast.makeText(ProductInfoActivity.this, "Currently Item Out of Stock!", Toast.LENGTH_SHORT).show();
                } else {
                    order_quantity -= 1;
                    order.setText(Integer.toString(order_quantity));
                }
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (order.getText().toString().equals(quantity)) {
                    Toast.makeText(ProductInfoActivity.this, "Maximum Available Quantity!", Toast.LENGTH_SHORT).show();
                } else {
                    order_quantity += 1;
                    order.setText(Integer.toString(order_quantity));
                }
            }
        });
    }
}