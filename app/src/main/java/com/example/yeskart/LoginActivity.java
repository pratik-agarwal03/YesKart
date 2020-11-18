package com.example.yeskart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    static DrawerLayout drawerLayout;
    static ActionBarDrawerToggle actionBarDrawerToggle;
    static Toolbar toolbar;
    String email, password;
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private FirebaseFirestore db;
    private ProgressDialog loadingBar;
    private TextView sellerTextView, buyerTextView;
    private boolean isBuyer;

    public void login() {
        db = FirebaseFirestore.getInstance();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();


        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Please Enter Email");

        } else if (TextUtils.isEmpty(password)) {
            //Toast.makeText(LoginActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
            passwordEditText.setError("Please Enter Password!");
        } else {
            loadingBar.setTitle("Logging you");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            db.collection("users")
                    .whereEqualTo("email", email)
                    .whereEqualTo("password", password)
                    .whereEqualTo("isBuyer", isBuyer)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            boolean isCorrect = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    Log.i("Msg", "Success");
                                    isCorrect = true;
                                    SplashScreen.auth.signInWithEmailAndPassword(email, password);
                                    if (isBuyer) {
                                        Intent intent = new Intent(LoginActivity.this, CategoryActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(LoginActivity.this, AddProductActivity.class);
                                        startActivity(intent);
                                    }

                                }
                            }

                            if (!isCorrect) {
                                Toast.makeText(LoginActivity.this, "Invalid Credientials", Toast.LENGTH_SHORT).show();
                            }

                            loadingBar.dismiss();

                        }
                    });
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerLayout1);
        NavigationView navigationView = findViewById(R.id.nav_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_open);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        if (SplashScreen.auth.getCurrentUser() != null) {
            navigationView.inflateMenu(R.menu.menu_logout);
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.Home:
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            break;
                        case R.id.product_list:
                            startActivity(new Intent(LoginActivity.this, CategoryActivity.class));
                            break;
                        case R.id.Logout:
                            SplashScreen.auth.signOut();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
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
                        case R.id.Home:
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            break;
                        case R.id.login:
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.SignUp:
                            Intent intent1 = new Intent(getApplicationContext(), RegisterActivity.class);
                            startActivity(intent1);
                            break;
                        case R.id.Help:
                            Toast.makeText(LoginActivity.this, "Help!", Toast.LENGTH_SHORT).show();
                    }
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
            });
        }

        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        buyerTextView = findViewById(R.id.buyer);
        sellerTextView = findViewById(R.id.seller);
        loadingBar = new ProgressDialog(this);

        isBuyer = true;

        sellerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellerTextView.setVisibility(View.INVISIBLE);
                loginButton.setText("Login as Seller");
                isBuyer = false;
                buyerTextView.setVisibility(View.VISIBLE);
            }
        });

        buyerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyerTextView.setVisibility(View.INVISIBLE);
                loginButton.setText("Login as Buyer");
                isBuyer = true;
                sellerTextView.setVisibility(View.VISIBLE);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }
}
