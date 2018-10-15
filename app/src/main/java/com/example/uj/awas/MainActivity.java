package com.example.uj.awas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    static boolean loginStatus = false;
    WebView wv;
    FirebaseDatabase database;
    DatabaseReference myRef, myPro, myBuild, myCSMC, myPublic;
    SharedPreferences sharedpreferences;
    static int projectcount;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG ="Success";


    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        *******************-----------------------*******************

        final JavaScriptInterface myJavaScriptInterface = new JavaScriptInterface(this);

        wv = findViewById(R.id.webview);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebChromeClient(new WebChromeClient());
        wv.loadUrl("file:///android_asset/index-2.html");
        wv.addJavascriptInterface(myJavaScriptInterface, "AndroidFunction");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Projects");
        myBuild = database.getReference("Builder");
        myCSMC = database.getReference("CSMC");
        myPublic = database.getReference("Public");
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        projectcount = sharedpreferences.getInt("projectval",0);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
//                    loginStatus = true;
                    Toast.makeText(MainActivity.this, "Already Loged In  "+firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                }
            }
        };





//        ******************-------------------***********

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }




//    *******************************/////////////////////////////





    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (wv.canGoBack()) {
                        wv.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }






    public class JavaScriptInterface{

        Context mContext;
        JavaScriptInterface(Context c) {
            mContext = c;
        }



        @android.webkit.JavascriptInterface
        public void registerbuilder(String email, String password, String id,String name,String cno,String gst,String ano,String oproject,String cproject) {
            myPro =  myBuild.child(name);
            myPro.child("ID").setValue(id);
            myPro.child("ContactNo").setValue(cno);
            myPro.child("Email").setValue(email);
            myPro.child("gst").setValue(gst);
            myPro.child("aadhaar").setValue(ano);
            myPro.child("ongoing").setValue(oproject);
            myPro.child("completed").setValue(cproject);;
            myPro.child("Name").setValue(name);
            createUser(email, password , password);
        }



        @android.webkit.JavascriptInterface
        public void livechat(){
//            loginStatus = false;
//            FirebaseAuth.getInstance().signOut();
//            wv.loadUrl("file:///android_asset/Login.html");
            Toast.makeText(getApplicationContext(), "Live Chat Started", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(getApplicationContext(),LiveActivity.class));
        }


        @android.webkit.JavascriptInterface
        public void registercsmc(String email,String password,String eid,String name,String cno,String des,String ano) {
            myPro =  myCSMC.child(name);
            myPro.child("EID").setValue(eid);
            myPro.child("Email").setValue(email);
            myPro.child("ContactNo").setValue(cno);
            myPro.child("designation").setValue(des);
            myPro.child("aadhaar").setValue(ano);
            myPro.child("Name").setValue(name);
            createUser(email, password , password);
        }



        @android.webkit.JavascriptInterface
        public void register(String email,String password) {
            myPublic = myPublic.push();
            myPublic.child("Email").setValue(email);
            myPublic.child("Password").setValue(password);
        }




        @android.webkit.JavascriptInterface

        public void showToast(){
            final String abc = mAuth.getCurrentUser().getEmail();
            // Read from the database


            Query query = myPro.orderByChild("Email").equalTo(abc);



            myBuild.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
//                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        String name = ds.child("name").getValue(String.class);
                        Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
//            setvalue(abc);
        }




        @android.webkit.JavascriptInterface
        public void test(){
//            Toast.makeText(MainActivity.this,"" , Toast.LENGTH_SHORT).show();
        }




        @android.webkit.JavascriptInterface
        public void setvalue(final String val){
            wv.post(new Runnable() {
                @Override
                public void run() {
                    wv.loadUrl("javascript:filldata("+val+")");
                }
            });
//            wv.loadUrl("javascript:filldata(val)");
        }





        @android.webkit.JavascriptInterface
        public void createUser(final String email, final String password, String cpass) {
            if (password.equals(cpass)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(getApplicationContext(), "Authentication Succeed.", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "createUserWithEmail:success");
//                                    register(email,password);
                                    FirebaseUser user = mAuth.getCurrentUser();
//                                    updateUI(user);
                                } else {
//                                     If sign in fails, display a message to the user.
//                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
                                }

                                // ...
                            }
                        });
            }else if(!password.equals(cpass)){
                Toast.makeText(MainActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
            }
        }



        @android.webkit.JavascriptInterface
        public void login(String email, String password){
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                    if (!task.isSuccessful()){

                        Toast.makeText(MainActivity.this, "Invalid Username or Password", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Loged in", Toast.LENGTH_SHORT).show();
                        wv.loadUrl("file:///android_asset/profile.html");
//                        loginStatus = true;
                    }
                }
            });
        }






        @android.webkit.JavascriptInterface
        public void logout(){
//            loginStatus = false;
            FirebaseAuth.getInstance().signOut();
            wv.loadUrl("file:///android_asset/Login.html");
            Toast.makeText(MainActivity.this, "LoggedOut", Toast.LENGTH_SHORT).show();

        }


        @android.webkit.JavascriptInterface
        public void addProject(String name, String location, String lat, String lot, String about, String builder, String sanctiondate, String start, String end){
            projectcount = projectcount+1;
            myPro =  myRef.child("MH00"+projectcount);
            myPro.child("End Date").setValue(end);
            myPro.child("Start Date").setValue(start);
            myPro.child("Sanction date").setValue(sanctiondate);
            myPro.child("Name of builder").setValue(builder);
            myPro.child("About").setValue(about);
            myPro.child("Longitude").setValue(lot);
            myPro.child("Latitude").setValue(lat);
            myPro.child("Location").setValue(location);
            myPro.child("Name").setValue(name);

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("projectval",projectcount);
            editor.apply();

            Toast.makeText(MainActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
        }
    }





//    ********************************////////////////////////////





    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//         Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
//        MenuItem logout = menu.findItem(R.id.action_logout);
        MenuItem addPro = menu.findItem(R.id.action_addProject);
        MenuItem invi = menu.findItem(R.id.action_inviteBuilder);
//        if (!loginStatus) {
//            logout.setVisible(false);
//            addPro.setVisible(false);
//            invi.setVisible(false);
//        if(loginStatus){
//            logout.setVisible(true);
            addPro.setVisible(true);
            invi.setVisible(true);
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_addProject) {
            wv.loadUrl("file:///android_asset/addProject.html");
        }
        if (id == R.id.action_inviteBuilder) {
            wv.loadUrl("file:///android_asset/addProject.html");
        }
//        if (id == R.id.action_logout) {
//            FirebaseAuth.getInstance().signOut();
//            Toast.makeText(MainActivity.this, "LoggedOut", Toast.LENGTH_SHORT).show();
////            loginStatus = false;
//        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_project) {
            Toast.makeText(this, "Projects", Toast.LENGTH_SHORT).show();
            wv.loadUrl("file:///android_asset/ProjectWall.html");
        } else if (id == R.id.nav_chatbot) {
            Toast.makeText(this, "Chatbot", Toast.LENGTH_SHORT).show();
            wv.loadUrl("file:///android_asset/chatbot.html");
        } else if (id == R.id.nav_analysis) {
            Toast.makeText(this, "Analysis", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_account) {
            if (!loginStatus){
                wv.loadUrl("file:///android_asset/Login.html");
            }else if(loginStatus){
                wv.loadUrl("file:///android_asset/profile.html");
            }
        } else if (id == R.id.nav_home) {
            wv.loadUrl("file:///android_asset/index-2.html");
        } else if (id == R.id.start_chat) {
            wv.loadUrl("file:///android_asset/live.html");
            wv.setWebChromeClient(new WebChromeClient());
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}