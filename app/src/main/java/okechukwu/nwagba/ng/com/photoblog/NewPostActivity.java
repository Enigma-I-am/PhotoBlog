package okechukwu.nwagba.ng.com.photoblog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import id.zelory.compressor.Compressor;


public class NewPostActivity extends AppCompatActivity {

    private static final int MAX_LENGTH = 100;
    private Toolbar toolbar;
    private Button postButton;
    private EditText blogPost;
    private ImageView photoBlog;
    private ProgressBar postProgressBar;

    private Uri ImageUri = null;
    private String CurrentUser_id;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    private Bitmap compressedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        toolbar = findViewById(R.id.post_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Post");

        postProgressBar =findViewById(R.id.post_progress);
        blogPost = findViewById(R.id.content);
        photoBlog = findViewById(R.id.photo);
        postButton = findViewById(R.id.post_button);

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        CurrentUser_id = mAuth.getCurrentUser().getUid();

        postProgressBar.setVisibility(View.INVISIBLE);




        photoBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .start(NewPostActivity.this);
            }
        });




        postButton.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 final String blogContent = blogPost.getText().toString();

                 if (!TextUtils.isEmpty(blogContent) && ImageUri != null){

                     postProgressBar.setVisibility(View.VISIBLE);

                     final String RandomName = random();

                     final StorageReference filePath = storageReference.child("post_images").child(RandomName+".jpg");

                     final StorageReference thumbFilePath = storageReference.child("post_images/thumbs").child(RandomName+".jpg");

                     final UploadTask uploadTask = filePath.putFile(ImageUri);


//                     //                                       This creates a file for the image using the image uri
//                     File newImageFile = new File(ImageUri.getAuthority());
//
//                     try {
////                                           This line compresses the file (newImageFile) using the compressor library
//                         compressedImageFile = new Compressor(NewPostActivity.this).compressToBitmap(newImageFile);
//                     } catch (IOException e) {
//                         e.printStackTrace();
//                     }
//
//
//                     ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                     compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                     byte[] Thumbdata = baos.toByteArray();
//
////                                       This is the task to upload the thumbnail
//                     UploadTask uploadthumbtask = storageReference.child("post_images/thumbs").child(RandomName+"jpg")
//                             .putBytes(Thumbdata);
//
//                     uploadthumbtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                         @Override
//                         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//
//
//                         }
//                     }).addOnFailureListener(new OnFailureListener() {
//                         @Override
//                         public void onFailure(@NonNull Exception e) {
//
//
//
//                         }
//                     });




                     uploadTask.addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {

                             String error = e.getMessage();
                             Toast.makeText(NewPostActivity.this, "Error: "+error, Toast.LENGTH_SHORT).show();


                         }
                     }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                             Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                 @Override
                                 public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                                     if (!task.isSuccessful()){
                                         throw task.getException();
                                     }

                                     return filePath.getDownloadUrl();
                                 }
                             }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Uri> task) {

                                     if (task.isSuccessful()){



                                         Uri DownloadUri = task.getResult();

                                         Map<String,Object> Userpost = new HashMap<>();
                                         Userpost.put("ImageUrl",DownloadUri.toString());
                                         Userpost.put("Description",blogContent);
                                         Userpost.put("user", CurrentUser_id);
                                         Userpost.put("time",FieldValue.serverTimestamp());

                                         firebaseFirestore.collection("Posts").add(Userpost).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                             @Override
                                             public void onComplete(@NonNull Task<DocumentReference> task) {

                                                   if (task.isSuccessful()){

                                                       Toast.makeText(NewPostActivity.this, "Post Added", Toast.LENGTH_SHORT).show();
                                                       Intent i = new Intent(NewPostActivity.this,MainActivity.class);
                                                       startActivity(i);
                                                       finish();


                                                   }else{

                                                       String Error = task.getException().getMessage();
                                                       Toast.makeText(NewPostActivity.this, "Error: "+Error, Toast.LENGTH_SHORT).show();

                                                   }

                                                   postProgressBar.setVisibility(View.INVISIBLE);
                                             }
                                         });

                                     }else {
                                         postProgressBar.setVisibility(View.INVISIBLE);

                                         String Error = task.getException().getMessage();
                                         Toast.makeText(NewPostActivity.this, "Error: "+Error, Toast.LENGTH_SHORT).show();

                                     }


                                 }
                             });

                         }
                     });



                 }


            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                ImageUri = result.getUri();

                photoBlog.setImageURI(ImageUri );

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }


//    This method generates random strings
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
