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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pranav_project.R;
import com.example.pranav_project.utils.MyConstants;
import com.example.pranav_project.utils.MySharedPreferences;
import com.example.pranav_project.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SecondStepVerifyActivity extends AppCompatActivity {

    private String permission[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private int PERMIISION_REQ_CODE=200;
    private static final int REQ_CODE=100;
    private Uri mUri;
    private String image_URL , CurrentUserID , clicked_user;

    private StorageReference storageReference;
    private CollectionReference user_clicked_referance;
    MySharedPreferences mySharedPreferences;
    private FirebaseAuth mAuth;
    private AlertDialog mDialog;

    @BindView(R.id.weater_dp)CircleImageView wProfilePic;
    @BindView(R.id.weater_signUp_name) EditText edtName;
    @BindView(R.id.weater_signUp_number) EditText edtPhn;
    @BindView(R.id.start) Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_step_verify);
        ButterKnife.bind(this);

        initialize2();


        request_function();

        wProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent ,REQ_CODE );
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString();
                String phn =edtPhn.getText().toString();

                updateToDB(name,phn);
            }
        }) ;
    }

    private void updateToDB(String name, String phn) {
        final Map<String,Object>map = new HashMap<>();
        map.put(MyConstants.NAME,name);
        map.put(MyConstants.PHONE,phn);

         user_clicked_referance
                .document(CurrentUserID)
                .update(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        
                        // save the profile pic links...
                         if(mUri==null)
                             Toast.makeText(SecondStepVerifyActivity.this,
                                     "select an profile image", Toast.LENGTH_SHORT).show();
                         else{
                             mDialog = Utils.getAlertDialog(SecondStepVerifyActivity.this,"creating ur profile..");
                             saveImageToTheStorageDatabase(map);
                         }
                             
                    }
                });

    }

    private void saveImageToTheStorageDatabase(final Map<String, Object> map) {
        UploadTask task = storageReference.child("profile")
                          .child(CurrentUserID).putFile(mUri);
        task.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {
                    task.getResult().getStorage().getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    image_URL = uri.toString();
                                    map.put(MyConstants.PROFILE_URL,image_URL);
                                    user_clicked_referance
                                            .document(CurrentUserID)
                                            .update(map);
                                    mDialog.dismiss();

                                    if(clicked_user.equals(MyConstants.WEATER))
                                      {
                                        startActivity(Utils.sendUseroWeaterHomeActcivity(getApplicationContext(),
                                                                                     WeaterHomeActivity.class));
                                        finish();
                                      }
                                    else if(clicked_user.equals(MyConstants.COOK))
                                      {
                                          startActivity(Utils.sendUserToCookActvity(getApplicationContext(),
                                                                                       CookActivity.class ));
                                          finish();
                                      }
                                }
                            });
                }
            }
        });
    }

    private void initialize2() {
        mAuth  = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        mySharedPreferences = MySharedPreferences.getInstance(this);
        storageReference = FirebaseStorage.getInstance().getReference();
        user_clicked_referance = FirebaseFirestore.getInstance()
                .collection(mySharedPreferences.getUserData(MyConstants.CURRENT_USER_CLICKED));
        clicked_user = mySharedPreferences.getUserData(MyConstants.CURRENT_USER_CLICKED) ;

    }

    // gallery permission
    private void request_function()
    {
        if(ActivityCompat.
                checkSelfPermission(this , permission[0])!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(SecondStepVerifyActivity.this , permission ,PERMIISION_REQ_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQ_CODE && resultCode==RESULT_OK){
            CropImage.activity(data.getData())
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .setOutputCompressQuality(50)
                    .start(this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(result!=null){
                mUri = result.getUri();
                wProfilePic.setImageURI(mUri);
            }
        }
    }
}
