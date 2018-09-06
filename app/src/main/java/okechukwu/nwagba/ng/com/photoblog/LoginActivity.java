package okechukwu.nwagba.ng.com.photoblog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private EditText EmailET;
    private EditText PasswordDT;
    private Button Login;
    private Button Register;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        EmailET = findViewById(R.id.EmailED);
        PasswordDT = findViewById(R.id.PasswordED);
        Login = findViewById(R.id.LoginBT);
        Register = findViewById(R.id.RegisterBT);
        progressBar = findViewById(R.id.progressBar);


        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


// Get strings in email and password edittexts
                String email = EmailET.getText().toString();
                String password = PasswordDT.getText().toString();


//              If the email and password edittexts are not empty it performs task in code block
                if (!TextUtils.isEmpty(email) &&    !TextUtils.isEmpty(password)){


                    progressBar.setVisibility(View.VISIBLE);
//                  the Auth object has a method to signin with email and password where you pass in the values from edittexts
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
//                          When the task is complete if it is successful go to the main activity else make a toast showing the error
                            if (task.isSuccessful()) {
                                sendtoMainActivity();
                            }else{
                                progressBar.setVisibility(View.INVISIBLE);
                                String e = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this,"Error Logging In:"+e,Toast.LENGTH_LONG).show();

                            }
                        }
                    });

                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this,"Please Enter E-mail and Password",Toast.LENGTH_LONG).show();

                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
//        If user is logged in take user to the mmain activity
        if (user != null){
            sendtoMainActivity();
        }
    }

    private void sendtoMainActivity() {
        Intent i = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }
}
