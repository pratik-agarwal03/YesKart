package com.example.yeskart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ImageView productImage;
    private EditText productNameEditText, productDescEditText, productPriceEditText, productQtyEditText;
    private String name, description, price, qty;
    private Button addProductButton;
    private Spinner productCategory;
    private ArrayAdapter<CharSequence> adapter;
    private String category, downloadImageURL;
    private Uri imageUri;
    private StorageReference productImageRef;
    private FirebaseFirestore db;
    private ProgressDialog loading;


    public void addProduct() {
        validateData();
    }

    public void validateData() {
        name = productNameEditText.getText().toString();
        description = productDescEditText.getText().toString();
        price = productPriceEditText.getText().toString();
        qty = productQtyEditText.getText().toString();

        if (imageUri == null) {
            Toast.makeText(AddProductActivity.this, "Plaese add image", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(AddProductActivity.this, "Plaese add name", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(AddProductActivity.this, "Plaese add description", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(AddProductActivity.this, "Plaese add price", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(qty)) {
            Toast.makeText(AddProductActivity.this, "Plaese add Quantity", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(category)) {
            Toast.makeText(AddProductActivity.this, "Plaese add category", Toast.LENGTH_SHORT).show();
        } else if (Integer.parseInt(price) == 0) {
            Toast.makeText(AddProductActivity.this, "Invalid price", Toast.LENGTH_SHORT).show();
        } else if (Integer.parseInt(qty) == 0) {
            Toast.makeText(AddProductActivity.this, "Invalid Quantity", Toast.LENGTH_SHORT).show();
        } else {
            loading.setTitle("Adding Product");
            loading.setMessage("Please wait...");
            loading.setCanceledOnTouchOutside(false);
            loading.show();

            storeProductInformation();
        }

    }

    public void storeProductInformation() {
        final StorageReference filePath = productImageRef.child(imageUri.getLastPathSegment() + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddProductActivity.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddProductActivity.this, "Image Uploaded successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadImageURL = task.getResult().toString();
                            Log.i("URL", downloadImageURL);
                            Toast.makeText(AddProductActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                            saveDetailsToDatabase();
                        }
                    }
                });
            }
        });
    }

    public void saveDetailsToDatabase() {
        HashMap<String, Object> product = new HashMap<>();

        product.put("name", name);
        product.put("description", description);
        product.put("price", price);
        product.put("qty", qty);
        product.put("category", category);
        product.put("imageUrl", downloadImageURL);
        product.put("seller", "me");
//        product.put("seller", Paper.book().read("email"));


        db = FirebaseFirestore.getInstance();
        db.collection("products")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddProductActivity.this, "Succesfully added product", Toast.LENGTH_SHORT).show();
                        Log.d("Msg", "DocumentSnapshot added with ID: " + documentReference.getId());
                        loading.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddProductActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        category = "Painting";
        productImage = findViewById(R.id.product_image);
        addProductButton = findViewById(R.id.add_product_btn);
        productNameEditText = findViewById(R.id.product_name);
        productDescEditText = findViewById(R.id.product_description);
        productPriceEditText = findViewById(R.id.product_price);
        productQtyEditText = findViewById(R.id.product_qty);

        productCategory = findViewById(R.id.product_category);
        adapter = ArrayAdapter.createFromResource(this, R.array.product_category_list, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productCategory.setAdapter(adapter);

        loading = new ProgressDialog(this);
        productCategory.setOnItemSelectedListener(this);

        productImageRef = FirebaseStorage.getInstance().getReference().child("Product images");


        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });
    }

    private void addImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            productImage.setImageURI(imageUri);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
