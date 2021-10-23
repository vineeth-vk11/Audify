package com.audify;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.audify.LoginUI.LoginMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    AppUpdateManager appUpdateManager;
    private static final int RC_APP_UPDATE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());

        checkVersion();
    }

    private void checkVersion(){
        db.collection("Version").document("Version").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                int minimumVersionCode = (int)(long)task.getResult().get("minimumVersionCode");
                int currentVersion = BuildConfig.VERSION_CODE;

                if(currentVersion<minimumVersionCode){
                    appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
                        @Override
                        public void onSuccess(AppUpdateInfo result) {
                            if(result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                                    result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                                try {
                                    appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, SplashActivity.this, RC_APP_UPDATE);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
                else {
                    redirect();
                }
            }
        });
    }

    private void redirect(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), LoginMainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_APP_UPDATE && resultCode != RESULT_OK){
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            redirect();
        }
    }
}