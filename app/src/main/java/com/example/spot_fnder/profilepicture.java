package com.example.spot_fnder;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class profilepicture extends AppCompatActivity {
    private static final int PREMISION_CODE=1234;
    private static final int CAPTURE_CODE=1001;
    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView pfpic;
    Button choose,upload,film;
    private StorageReference storageReference;
    FirebaseAuth authprofile;
    private FirebaseUser firebaseUser;
    private Uri uriImage;
    private Uri image;
    private static final int REQUEST_CODE = 22;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profilepicture);
        pfpic = findViewById(R.id.imageView_profile_dp);
        upload=findViewById(R.id.upload_pic_button);
        choose=findViewById(R.id.upload_pic_choose_button);
        film=findViewById(R.id.filmpic);
        authprofile=FirebaseAuth.getInstance();
        firebaseUser=authprofile.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference("Display pics");

        Uri uri = firebaseUser.getPhotoUrl();

        Picasso.with(profilepicture.this).load(uri).into(pfpic);

        film.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED){
                    String [] premission={Manifest.permission.CAMERA};

                    requestPermissions(premission,PREMISION_CODE);
                }else {
                    opencammera();
                }
            }
        });
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
                    String [] premission ={Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(premission,PREMISION_CODE);
                }else{
                    openFileChosser();
                }
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uploadpic();
            }
        });

    }

    private void opencammera() {
        ContentValues values =new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From cammera");
        uriImage = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent camintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camintent.putExtra(MediaStore.EXTRA_OUTPUT,uriImage);
        startActivityForResult(camintent,CAPTURE_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PREMISION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                opencammera();
            } else {
                Toast.makeText(profilepicture.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void Uploadpic() {
        if (uriImage!=null){
            StorageReference fileRefrence = storageReference.child(authprofile.getCurrentUser().getUid()+'.'+
                    getFileExtantion(uriImage));
            fileRefrence.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRefrence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloaduri =uri;
                            firebaseUser = authprofile.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(downloaduri).build();
                            firebaseUser.updateProfile(profileUpdates);
                        }
                    });
                    Intent intent = new Intent(profilepicture.this,Mainpage.class);
                    startActivity(intent);
                }

            });
        }

    }
    private String getFileExtantion(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime =MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChosser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);





        if ( resultCode == RESULT_OK) {
            uriImage = data.getData();
            pfpic.setImageURI(uriImage);
        }

    }

}