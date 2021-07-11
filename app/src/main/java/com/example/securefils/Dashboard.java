package com.example.securefils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Dashboard extends AppCompatActivity {

    private Button upload, showAllBtn;
    private ImageView imageView;
    private ProgressBar progressBar;
    Uri pdfUri;

    private String getUID(){
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mUser != null) {
            String strUID = mUser.getUid();
            if(!TextUtils.isEmpty(strUID)) {
                return strUID;
            }
        }
        return "";
    }

    String strUID = getUID();

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("File/" + strUID);
    private StorageReference reference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        upload = findViewById(R.id.upload);
        showAllBtn = findViewById(R.id.showAllBtn);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);

        progressBar.setVisibility(View.INVISIBLE);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPdf();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pdfUri != null)
                uploadToFireBase(pdfUri);
                else
                    Toast.makeText(Dashboard.this, "Select a file", Toast.LENGTH_LONG).show();
            }
        });

        showAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, Myfiles.class));
            }
        });
    }


    private void selectPdf() {
        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {

            pdfUri = data.getData();
            imageView.setImageURI(pdfUri);

        } else {
            Toast.makeText(Dashboard.this, "Please Select a File", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadToFireBase(Uri uri){

        StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        FileModel fileModel = new FileModel(uri.toString());
                        String modelId = root.push().getKey();
                        root.child(modelId).setValue(fileModel);

                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(Dashboard.this, "Upload Successfully", Toast.LENGTH_LONG).show();
                        imageView.setImageResource(R.drawable.ic_add);

                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(Dashboard.this, "Upload Failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getFileExtension(Uri mUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
}