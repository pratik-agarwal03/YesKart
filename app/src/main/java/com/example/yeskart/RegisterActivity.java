package com.example.yeskart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText nameEditText, emailEditText, passwordEditText, moblieNoEditText, addressEditText;
    Button registerButton;
    //FirebaseAuth auth;
    RadioGroup type;
    RadioButton rButton;
    private ProgressDialog loading;
    private FirebaseFirestore db;

    public void createAccount() {
        String email, name, password, mobileNo, address;
        email = emailEditText.getText().toString().trim();
        name = nameEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        mobileNo = moblieNoEditText.getText().toString().trim();
        address = addressEditText.getText().toString().trim();
        int selectId = type.getCheckedRadioButtonId();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(RegisterActivity.this, "Please enter name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mobileNo)) {
            Toast.makeText(RegisterActivity.this, "Please enter mobileNo", Toast.LENGTH_SHORT).show();
        } else if (selectId == -1) {
            Toast.makeText(this, "Please select type!", Toast.LENGTH_SHORT).show();
        } else {
            loading.setTitle("Creating User");
            loading.setMessage("Please wait...");
            loading.setCanceledOnTouchOutside(false);
            loading.show();
            rButton = type.findViewById(selectId);
            Toast.makeText(this, rButton.getText().toString(), Toast.LENGTH_SHORT).show();
            boolean isBuyer = false;
            if (rButton.getText().toString().equals("Buyer")) {
                isBuyer = true;
            }
            validate(name, email, password, mobileNo, isBuyer, address);
        }
    }

    public void validate(final String name, final String email, final String password, final String mobileNo, final boolean isBuyer, final String address) {
        db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean isPresent = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    isPresent = true;
                                }
                                Log.d("Message", document.getId() + " => " + document.getData());
                            }

                            if (!isPresent) {
                                addUser(name, mobileNo, email, password, isBuyer, address);
                            } else {
                                Toast.makeText(RegisterActivity.this, "User already present with this email", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Somethong went wrong", Toast.LENGTH_SHORT).show();
                        }
                        loading.dismiss();
                    }
                });
//00574b
    }

    public void addUser(String name, String mobileNo, String mail, String pass, boolean isBuyer, String address) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("mobileNo", mobileNo);
        user.put("email", mail);
        user.put("password", pass);
        user.put("isBuyer", isBuyer);
        user.put("address",address);
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(RegisterActivity.this, "Succesfully added user", Toast.LENGTH_SHORT).show();
                        Log.d("Msg", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("tag", "Error adding document", e);
                    }
                });
        SplashScreen.auth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.register_full_name);
        emailEditText = findViewById(R.id.register_email);
        passwordEditText = findViewById(R.id.register_password);
        moblieNoEditText = findViewById(R.id.register_mobile_no);
        registerButton = findViewById(R.id.register_button);
        SplashScreen.auth = FirebaseAuth.getInstance();
        loading = new ProgressDialog(this);
        type = findViewById(R.id.radio);
        type.clearCheck();
        addressEditText = findViewById(R.id.register_address);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }
}
