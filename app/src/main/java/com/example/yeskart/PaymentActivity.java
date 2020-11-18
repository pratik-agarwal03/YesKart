package com.example.yeskart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {
    FirebaseFirestore db;
    TextView name, desc, qty, price, deliver, total;
    String q;
    ImageView img;
    String n;
    String cust_name = "";
    String address = "";
    Button order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        db = FirebaseFirestore.getInstance();
        total = findViewById(R.id.total);
        img = findViewById(R.id.productImage);
        name = findViewById(R.id.productName);
        desc = findViewById(R.id.productDesc);
        deliver = findViewById(R.id.deliveryAdd);
        qty = findViewById(R.id.quantity);
        price = findViewById(R.id.cost);
        order = findViewById(R.id.order);
        Intent i = getIntent();
        n = i.getStringExtra("Name");
        String d = i.getStringExtra("Desc");
        q = i.getStringExtra("Qty");
        String p = i.getStringExtra("Price");
        String im = i.getStringExtra("Image");
        String mail = i.getStringExtra("Email");
        db.collection("users").
                whereEqualTo("email", mail).
                get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //Toast.makeText(PaymentActivity.this, task.getResult().toString(), Toast.LENGTH_LONG).show();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Toast.makeText(PaymentActivity.this, document.getString("email").toString(), Toast.LENGTH_LONG).show();
                            Map v = document.getData();
                            cust_name = v.get("name").toString();
                            address = v.get("address").toString();
                            deliver.setText(cust_name + "\n" + address);
                        }
                    }
                });
        name.setText(n);
        desc.setText(d);
        qty.setText(q);
        price.setText(p);
        Picasso.get().load(im)
                .into(img);
        int t = Integer.parseInt(q) * Integer.parseInt(p);
        total.setText(Integer.toString(t));
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("products")
                        .whereEqualTo("name", n).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<Object, Integer> mp = new HashMap<>();
                                Map v = document.getData();
                                int mQ = Integer.valueOf((String) v.get("qty")) - Integer.valueOf(q);
                                v.put("qty", mQ);
                                Toast.makeText(PaymentActivity.this, document.getId(), Toast.LENGTH_SHORT).show();
                                db.collection("products").document(document.getId()).set(v);
                            }
                        }
                    }
                });
                Intent i = new Intent(PaymentActivity.this, MainActivity.class);
                Toast.makeText(PaymentActivity.this, "Your order has been booked!", Toast.LENGTH_LONG).show();
                startActivity(i);
            }
        });
    }
}