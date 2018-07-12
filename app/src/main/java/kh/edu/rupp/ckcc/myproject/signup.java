package kh.edu.rupp.ckcc.myproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity implements OnCompleteListener<AuthResult>
{
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private EditText email;
    private EditText password;
    private EditText usename;
    private ImageView imgprofile;


    Uri uriprofile;
    String profileimgurl;

    Profile profile=new Profile();

   private Button button1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_signup);

        email=findViewById(R.id.signup_email);
        password=findViewById(R.id.signup_password);
        usename=findViewById(R.id.signup_user);
        imgprofile=findViewById(R.id.pic_registration);

        button1= findViewById(R.id.btn_summit);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

    }

    //choose pic for profile
    public void choose_picture(View view)
    {
        // Open gallery app to select an image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    //button sign up function
    private void signup(){
       firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener( this);
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            // Sign in success, update UI with the signed-in user's information
            //FirebaseUser user = firebaseAuth.getCurrentUser();

            String e=email.getText().toString();
            String u=usename.getText().toString();
//            String i=imgprofile.setImageURI();


            Map<String,String>usermap = new HashMap<>();
            usermap.put("Username",u);
            usermap.put("Email",e);
 //           usermap.put("ImgUrl",i);

            db.collection("Profile").document(task.getResult().getUser().getUid()).set(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent intent = new Intent(signup.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });


        } else {
            // If sign in fails, display a message to the user.
            Toast.makeText(this, "signup with Firebase error.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Load selected image
        if(resultCode == RESULT_OK && requestCode == 1&& data.getData()!=null&&data!=null){
            try {
                // Set image to image view
                uriprofile=data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriprofile);
                imgprofile.setImageBitmap(bitmap);

                // Save image to Firebase Storage
                uploadImageToFirebaseStorage(bitmap);

            } catch (IOException e) {
                Toast.makeText(this, "Error while selecting profile image.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    //upload pic to storage
    private void uploadImageToFirebaseStorage(Bitmap bitmap){

        //get username 
        TextView userId=findViewById(R.id.signup_user);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference profileRef = storage.getReference().child("images").child("Profile").child(userId + ".jpg");
        profileRef.putFile(uriprofile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){

                } else {

                    Log.d("ckcc", "Upload profile image fail: " + task.getException());
                }
            }
        });
    }

}
