package com.example.yeskart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity implements MyAdapter.OnItemClickListener {

    static DrawerLayout drawerLayout;
    static ActionBarDrawerToggle actionBarDrawerToggle;
    static Toolbar toolbar;
    static ArrayList<String> productNames, productDescriptions, prices, images, qty;
    RecyclerView recyclerView;
    ProgressDialog loadingBar;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerLayout1);
        NavigationView navigationView = findViewById(R.id.nav_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_open);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (SplashScreen.auth.getCurrentUser() != null) {
            navigationView.inflateMenu(R.menu.menu_logout);
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.Home:
                            startActivity(new Intent(CategoryActivity.this, MainActivity.class));
                            finish();
                            break;
                        case R.id.product_list:
                            startActivity(new Intent(CategoryActivity.this, CategoryActivity.class));
                            finish();
                            break;
                        case R.id.Logout:
                            SplashScreen.auth.signOut();
                            startActivity(new Intent(CategoryActivity.this, MainActivity.class));
                            finish();
                            break;
                    }
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
            });
        } else {
            navigationView.inflateMenu(R.menu.navigation_menu);
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.login:
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.SignUp:
                            Intent intent1 = new Intent(getApplicationContext(), RegisterActivity.class);
                            startActivity(intent1);
                            break;
                        case R.id.Help:
                            Toast.makeText(CategoryActivity.this, "Help!", Toast.LENGTH_SHORT).show();
                    }
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
            });
        }

        recyclerView = findViewById(R.id.my_recycler_view1);

        productDescriptions = new ArrayList<>();
        productNames = new ArrayList<>();
        prices = new ArrayList<>();
        images = new ArrayList<>();
        qty = new ArrayList<>();
        loadingBar = new ProgressDialog(this);
        fetchProducts();
    }

    public void fetchProducts() {

        loadingBar.setTitle("Getting all products");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                productNames.add(document.getData().get("name").toString());
                                productDescriptions.add(document.getData().get("description").toString());
                                prices.add(document.getData().get("price").toString());
                                images.add(document.getData().get("imageUrl").toString());
                                qty.add(document.getData().get("qty").toString());
                                Log.d("products", document.getId() + " => " + document.getData().get("name").toString());
                                Log.d("products", document.getId() + " => " + document.getData().get("description").toString());
                                Log.d("products", document.getId() + " => " + document.getData().get("imageUrl").toString());
                                Log.d("products", document.getId() + " => " + document.getData());
                            }

                            loadingBar.dismiss();

                            myAdapter = new MyAdapter(CategoryActivity.this, productNames, productDescriptions, prices, images);
                            recyclerView.setAdapter(myAdapter);
                            myAdapter.setOnItemClickListener(CategoryActivity.this);
                            recyclerView.setLayoutManager(new LinearLayoutManager(CategoryActivity.this));
                        } else {
                            loadingBar.dismiss();
                            Log.w("error", "Error getting documents.", task.getException());
                        }


                    }
                });
    }


    @Override
    public void onItemClick(int position) {
        //Toast.makeText(this, prices.get(position), Toast.LENGTH_SHORT).show();
        Intent i = new Intent(CategoryActivity.this, ProductInfoActivity.class);
        i.putExtra("Name", productNames.get(position));
        i.putExtra("Description", productDescriptions.get(position));
        i.putExtra("Price", prices.get(position));
        i.putExtra("Image", images.get(position));
        i.putExtra("Qty", qty.get(position));
        startActivity(i);
    }
}
