package okechukwu.nwagba.ng.com.photoblog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button createAccount;
    private Button hasAccount;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            startMainActivity();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.register_EmailED);
        password = findViewById(R.id.editText);
        confirmPassword = findViewById(R.id.register_PasswordED);
        createAccount = findViewById(R.id.create_account);
        hasAccount = findViewById(R.id.has_account);
        progressBar = findViewById(R.id.register_progressBar);

        mAuth = FirebaseAuth.getInstance();



        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Email = email.getText().toString();
                String Password = password.getText().toString();
                String ConfirmPassword = confirmPassword.getText().toString();

                if (!TextUtils.isEmpty(Email) && !TextUtils.isEmpty(Password) && !TextUtils.isEmpty(ConfirmPassword)){

                    if (Password.equals(ConfirmPassword)){

                        progressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()){

                                    startMainActivity();

                                }else {

                                    String e = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this,"Error: "+e,Toast.LENGTH_LONG).show();
                                }

                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });


                    }else {
                        Toast.makeText(RegisterActivity.this,"Passwords don't match",Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

    }

    private void startMainActivity() {
        Intent i = new Intent(RegisterActivity.this , MainActivity.class);
        startActivity(i);
        finish();
    }
}
