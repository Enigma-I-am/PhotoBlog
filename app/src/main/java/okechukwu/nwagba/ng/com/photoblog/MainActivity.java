package okechukwu.nwagba.ng.com.photoblog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {


    private Toolbar mainToolbar;
    private FloatingActionButton fab;
    private FirebaseFirestore fbfs;

    private String currentUserId;

    private BottomNavigationView mainBottomnav;

    private HomeFragment homeFragment;
    private AccountFragment accountFragment;
    private NotificationFragment notificationFragment;




//  Authentication variable
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Photo Blog");

//      Instance of User Authentication
        mAuth = FirebaseAuth.getInstance();
        fbfs = FirebaseFirestore.getInstance();

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(i);
                Toast.makeText(MainActivity.this, "Fab clicked ", Toast.LENGTH_SHORT).show();
            }
        });






        mainBottomnav = findViewById(R.id.mainbutton_nav);


        homeFragment = new HomeFragment();
        notificationFragment = new NotificationFragment();
        accountFragment = new AccountFragment();

        replaceFragment(homeFragment);


        mainBottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.home_bot_nav:
                        replaceFragment(homeFragment);
                        return true;
                    case R.id.noti_bot_nav:
                        replaceFragment(notificationFragment);
                        return true;

                    case R.id.account_bot_nav:
                        replaceFragment(accountFragment);
                        return  true;

                        default:
                            return false;
                }



            }
        });







    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_button:
                logout();
                return true;
            case R.id.setting_button:
                goToSetupActivity();
                return true;
              default:
                  return false;
        }
     }

     private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container,fragment);
        fragmentTransaction.commit();


     }

//  method with intent to go to Setup activity
    private void goToSetupActivity() {
        Intent i = new Intent(MainActivity.this,SetupActivity.class);
        startActivity(i);
    }


    //  A private method to log user out of the app using instance of user auth object
    private void logout() {
        mAuth.signOut();
        sendtologin();
    }

//    method with  Intent to go to the login activity
    private void sendtologin() {
        Intent i = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        Check if user is logged in and if user is not logged in (== null) take the user to the login activity
        if (user == null){
            sendtologin();
        }else{

            currentUserId = mAuth.getCurrentUser().getUid();

            fbfs.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()){
//                      If there is no firestore record of user take user to Setup activity
                        if (!task.getResult().exists()){
                            Intent i = new Intent(MainActivity.this,SetupActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }else {

                        String error = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error :"+error, Toast.LENGTH_SHORT).show();

                    }

                }
            });


        }
    }

}
