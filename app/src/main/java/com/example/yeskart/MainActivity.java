package com.example.yeskart;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    static DrawerLayout drawerLayout;
    static ActionBarDrawerToggle actionBarDrawerToggle;
    static Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                            finish();
                            break;
                        case R.id.product_list:
                            startActivity(new Intent(MainActivity.this, CategoryActivity.class));
                            //finish();
                            break;
                        case R.id.Logout:
                            SplashScreen.auth.signOut();
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
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
                            Toast.makeText(MainActivity.this, "Help!", Toast.LENGTH_SHORT).show();
                    }
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
            });
        }

    }
}
