package fr.bigsis.android.helpers;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import fr.bigsis.android.activity.EventListActivity;

public class UploadImageHelper extends AppCompatActivity {

    private Uri imageProfileUri;
    private StorageReference mStroageReference;
    private int STORAGE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setPhoto();
            }
        }
    }

    private void setPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = UploadImageHelper.this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mStroageReference = FirebaseStorage.getInstance().getReference("imagesEvent");

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageProfileUri = data.getData();

            Intent i = getIntent();
            String id = i.getStringExtra("ID_EVENT_PHOTO");
            final StorageReference imgReference = mStroageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageProfileUri));

            String link = imgReference.toString();
            if (imageProfileUri != null) {
                imgReference.putFile(imageProfileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("events")
                                .document(id).update("imageEvent", link).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(UploadImageHelper.this, EventListActivity.class));
                            }
                        });
                    }
                });
            }
        }
    }
}
