package com.example.pranav_project.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.example.pranav_project.R;
import com.example.pranav_project.utils.MyConstants;
import com.example.pranav_project.utils.MySharedPreferences;
import com.example.pranav_project.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.primitives.Booleans;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.btn_1) Button manager;
    @BindView(R.id.btn_2) Button weater;
    @BindView(R.id.btn_3) Button cook;
    private AlertDialog mDialog;

    private FirebaseAuth mAuth;

    private MySharedPreferences mySharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        initializations();

        manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call_login_of_manager();
                mySharedPreferences.setUserData(MyConstants.CURRENT_USER_CLICKED,MyConstants.MANAGER);
            }
        });

        weater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySharedPreferences.setUserData(MyConstants.CURRENT_USER_CLICKED,MyConstants.WEATER);
                call_login(MyConstants.WEATER);
            }
        });

        cook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call_login(MyConstants.COOK);
                mySharedPreferences.setUserData(MyConstants.CURRENT_USER_CLICKED,MyConstants.COOK);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        if(mySharedPreferences.getUserData(MyConstants.WEATER).equals("1"))
            {
                startActivity(Utils.sendUseroWeaterHomeActcivity(getApplicationContext(),WeaterHomeActivity.class));    finish();
            }
        else
            if(mySharedPreferences.getUserData(MyConstants.MANAGER).equals("1"))     // send to Manager
            {
                startActivity(Utils.sendUserToManagerActivity(getApplicationContext(),ManagerHomeActivity.class));    finish();
            }
            else
                if(mySharedPreferences.getUserData(MyConstants.COOK).equals("1"))
                {
                    startActivity(Utils.sendUserToCookActvity(getApplicationContext(),CookActivity.class));    finish();
                }
    }


    private void call_login_of_manager() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Login Here");

        View view = LayoutInflater.from(this).inflate(R.layout.login_1 , null , false);
        getLoginOfTHeManager(view);

        builder.setView(view);
        builder.show();
    }


    private void call_login(final String loginUserName) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Login");
        View view  =LayoutInflater.from(this).inflate(R.layout.weater_login , null , false);

        System.out.println(" loginUserName ......"+ loginUserName);
        doLoginForUser(view , loginUserName);
        builder.setView(view);

        final TextView userSignUpTV = view.findViewById(R.id.user_signUp_TV);
        userSignUpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_Weater_SignUp_UI(loginUserName);
            }
        });

        builder.show();
    }

    // login for weater and manager
    private void doLoginForUser(View view , final String loginUserName) {
        final EditText email = view.findViewById(R.id.weater_login_email);
        final EditText pass = view.findViewById(R.id.weater_login_pass);
        final Button loginButton = view.findViewById(R.id.weater_login_button);

        System.out.println("loginUserName........ "+loginUserName);

       loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mEmail = email.getText().toString().trim();
                String mPass = pass.getText().toString().trim();

                   if(TextUtils.isEmpty(mEmail)){
                    email.setError("must enter a valid email..");
                    return ;
                }
                else if(TextUtils.isEmpty(mPass)){
                    pass.setError("minimum length hsould be 7");
                    return ;
                }
                else
                {
                    mDialog = Utils.getAlertDialog(LoginActivity.this , "Signing in..");
                    mDialog.show();
                    mAuth.signInWithEmailAndPassword(mEmail , mPass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    mDialog.dismiss();

                                    if(task.isSuccessful()) {
                                        mySharedPreferences.setUserData(loginUserName , "1");
                                        Toast.makeText(LoginActivity.this,
                                                "login completed successfully", Toast.LENGTH_SHORT).show();

                                          if(loginUserName.equals(MyConstants.COOK))
                                            {
                                                startActivity(Utils.sendUserToCookActvity(getApplicationContext(),
                                                        CookActivity.class));
                                                System.out.println("sending to COOK activity..........");


                                             finish();
                                            }
                                           else
                                                 {
                                                     startActivity(Utils.sendUseroWeaterHomeActcivity(getApplicationContext()
                                                             ,WeaterHomeActivity.class));
                                                     System.out.println("sending to weater Ctiviyt.....");

                                             finish();
                                                  }
                                    }
                                    else
                                        Toast.makeText(LoginActivity.this, "error in logon", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });
    }

// sign up Ui for cook and weater
    private void open_Weater_SignUp_UI(String loginUserName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign Up");
        View view = LayoutInflater.from(this).inflate(R.layout.weater_signup_ui , null , false);

        signUpProcessOfWeater(view , loginUserName);

        builder.setView(view);
        builder.show();
    }

    // manager login
    private void getLoginOfTHeManager(View view) {
        final EditText email = view.findViewById(R.id.id_login_email);
        final EditText pass = view.findViewById(R.id.id_login_pass);
        final Button loginBtn = view.findViewById(R.id.id_login_login);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                doLoginForManager(email , pass);
            }
        });
    }

    // signUp processs of the weater  and cook
    private void signUpProcessOfWeater(View view,final String clickedUserName) {

        final EditText wEmail = view.findViewById(R.id.weater_signUp_email);
        final EditText wPass = view.findViewById(R.id.weater_signUp_pass);
        final EditText wConPass = view.findViewById(R.id.weater_signUp_confirm_pass);
        Button   wSignUp = view.findViewById(R.id.weater_signUp_button);

        wSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = wEmail.getText().toString().trim();
                final String pass = wPass.getText().toString().trim();
                final String cpass = wConPass.getText().toString().trim();

               if(TextUtils.isEmpty(email)
                       ||TextUtils.isEmpty(pass)||TextUtils.isEmpty(cpass) ){
                   Toast.makeText(LoginActivity.this, "must enter all values", Toast.LENGTH_SHORT).show();
                   return;
               }
               else if(!pass.equals(cpass)){
                   wConPass.setError("password doesnt match");
                   return;
               }
               else{
                   mDialog = Utils.getAlertDialog(LoginActivity.this , "signing in");
                   mDialog.show();
                      mAuth.createUserWithEmailAndPassword(email , pass)
                              .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                  @Override
                                  public void onComplete(@NonNull Task<AuthResult> task) {

                                      if(task.isSuccessful())
                                      {
                                          Map<String,String> map = new LinkedHashMap<>();
                                          map.put(MyConstants.EMAIL,email);
                                          map.put(MyConstants.PASSWORD, pass);

                                          FirebaseFirestore.getInstance().collection(clickedUserName)
                                                  .document(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())
                                                  .set(map)
                                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                      @Override
                                                      public void onComplete(@NonNull Task<Void> task) {
                                                          if(task.isSuccessful())
                                                          {
                                                              Intent intent = new Intent(getApplicationContext(),SecondStepVerifyActivity.class);
                                                              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                              startActivity(intent);
                                                              finish();
                                                          }
                                                      }
                                                  });

                                      }
                                      else   // iff
                                          Utils.toast(LoginActivity.this,"account not created");
                                  }
                              });
               }
            }
        });
    }

//    private void saveImageFirst( final String email, final String phone,final String signUpUserName) {
//
//        UploadTask task = storageReference.putFile(mUri);
//        task.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                if(task.isSuccessful()){
//                    task.getResult().getStorage().getDownloadUrl()
//                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    image_URL = uri.toString();
//
//                                    Map<String ,String> map = new HashMap<>();
//                                    map.put(MyConstants.EMAIL , email);
//
//                                    saveDataToFirebase(map , signUpUserName);
//                                }
//                            });
//                }
//                else
//                   Utils.toast(LoginActivity.this,"couldnt upload image");
//            }
//        });
//    }

    // manager login
    private void doLoginForManager(EditText email, EditText pass) {
        String mEmail = email.getText().toString().trim();
        String mPass = pass.getText().toString().trim();

        if(TextUtils.isEmpty(mEmail)){
            email.setError("cannot be empty");
            return;
        }
        else if(TextUtils.isEmpty(mPass ) || mPass.length()<6){
            pass.setError("password must be 7 digit lenght");
            return;
        }
        else
        {
            mDialog = Utils.getAlertDialog(this , "Logging in..");
            mDialog.show();
               mAuth.signInWithEmailAndPassword(mEmail , mPass)
                       .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {

                               if(task.isSuccessful()){
                                   mySharedPreferences.setUserData(MyConstants.MANAGER , "1");
                                                                                    // sending user to manager actcity
                                   startActivity(Utils.sendUserToManagerActivity(getApplicationContext() , ManagerHomeActivity.class));
                                   finish();
                                   mDialog.dismiss();
                               }
                               else{
                                   Utils.toast(LoginActivity.this ,"Something wen wrong while sign in..");
                                   mDialog.dismiss();
                               }
                           }
                       });
        }
    }

    private void initializations() {

        mySharedPreferences = MySharedPreferences.getInstance(this);
        mAuth  = FirebaseAuth.getInstance();

    }



//    private void saveDataToFirebase(final Map<String, String> map,final String signUpUserName) {
//
//         weaterCookRef =FirebaseFirestore.getInstance().collection(signUpUserName+"");
//
//        CurrentUserID = mAuth.getCurrentUser().getUid();
//         weaterCookRef.document(CurrentUserID).set(map)
//                 .addOnCompleteListener(new OnCompleteListener<Void>() {
//                     @Override
//                     public void onComplete(@NonNull Task<Void> task) {
//
//                        mDialog.dismiss();
//                         if(task.isSuccessful())
//                         {
//                             mySharedPreferences.setUserData(signUpUserName,"1");
//
//                             mySharedPreferences.setUserData(MyConstants.NAME , map.get(MyConstants.NAME));
//                             mySharedPreferences.setUserData(MyConstants.PROFILE_URL , map.get(MyConstants.PROFILE_URL));
//
//                            Utils. toast(LoginActivity.this,"data saved");
//
//                              if(signUpUserName.equals(MyConstants.WEATER))    // sending to weater
//                                   {
//                                        startActivity(Utils.sendUseroWeaterHomeActcivity(getApplicationContext(),WeaterHomeActivity.class));  finish();
//                                   }
//                                  else    // sending to cook act
//                                  {
//                                      startActivity(Utils.sendUserToCookActvity(getApplicationContext(), CookActivity.class));   finish();
//                                  }
//                         }
//                         else
//                            Utils. toast(LoginActivity.this,"data not saved");
//                     }
//                 });
//    }


}
