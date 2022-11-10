package danielryansunjaya.finalyearproject.eventhotspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;


import danielryansunjaya.finalyearproject.eventhotspot.models.UserModel;
import danielryansunjaya.finalyearproject.eventhotspot.utils.JavaMailAPI;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG ="SignUpActivity";
    FirebaseAuth auth;
    FirebaseDatabase rtDB;
    Query query;
    ValueEventListener valueEventListener;
    EditText inputName, inputID, inputProgramme, inputPassword, inputPassConfirm;

    private static final int TIMER = 2000;
    TextView signup_btn_text;
    RelativeLayout signup_btn_layout;
    LottieAnimationView signup_btn_animation_loading,
            signup_btn_animation_check, signup_btn_animation_cross,
            name_warning, id_warning, programme_warning,
            password_warning, passConfirm_warning;

    private String name, id, email, programme, password;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        rtDB = FirebaseDatabase.getInstance();

        inputName = findViewById(R.id.inputName);
        inputID = findViewById(R.id.inputID);
        inputProgramme = findViewById(R.id.inputProgramme);
        inputPassword = findViewById(R.id.inputPassword);
        inputPassConfirm = findViewById(R.id.inputPassConfirm);

        name_warning = findViewById(R.id.name_field_warning);
        id_warning = findViewById(R.id.id_field_warning);
        programme_warning = findViewById(R.id.programme_field_warning);
        password_warning = findViewById(R.id.password_field_warning);
        passConfirm_warning = findViewById(R.id.confirmPass_field_warning);

        signup_btn_text = findViewById(R.id.signup_button_text);
        signup_btn_animation_loading = findViewById(R.id.signup_button_animation_loading);
        signup_btn_animation_check = findViewById(R.id.signup_button_animation_check);
        signup_btn_animation_cross = findViewById(R.id.signup_button_animation_cross);
        signup_btn_layout = findViewById(R.id.signup_button_layout);
        signup_btn_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name_warning.setVisibility(View.GONE);
                name_warning.pauseAnimation();
                id_warning.setVisibility(View.GONE);
                id_warning.pauseAnimation();
                programme_warning.setVisibility(View.GONE);
                programme_warning.pauseAnimation();
                password_warning.setVisibility(View.GONE);
                password_warning.pauseAnimation();
                passConfirm_warning.setVisibility(View.GONE);
                passConfirm_warning.pauseAnimation();

                inputID.setHint("");
                inputPassword.setHint("");
                inputPassConfirm.setHint("");

                signup_btn_text.setVisibility(View.GONE);
                signup_btn_animation_loading.setVisibility(View.VISIBLE);
                signup_btn_animation_loading.playAnimation();

                new Handler().postDelayed(this::insertInfo,TIMER);
            }

            public void insertInfo(){
                if(noEmptyFields()){
                    createAccount();
                }else{
                    failCreateAccount();
                }
            }
        });
    }


    public Boolean noEmptyFields(){
        name = inputName.getText().toString();
        id = inputID.getText().toString();
        email = inputID.getText().toString()+"@ucsiuniversity.edu.my";
        programme = inputProgramme.getText().toString();
        password = inputPassword.getText().toString();
        String passConfirm = inputPassConfirm.getText().toString();

        if(TextUtils.isEmpty(name)){
            name_warning.setVisibility(View.VISIBLE);
            name_warning.playAnimation();
        }
        if(TextUtils.isEmpty(id)){
            id_warning.setVisibility(View.VISIBLE);
            id_warning.playAnimation();
        }
        if(id.length()<10){
            inputID.setText("");
            inputID.setHint("ID too short!");
            inputID.setHintTextColor(ContextCompat.getColor(this, R.color.highlights_orange));
            id_warning.setVisibility(View.VISIBLE);
            id_warning.playAnimation();
        }
        if(TextUtils.isEmpty(programme)){
            programme_warning.setVisibility(View.VISIBLE);
            programme_warning.playAnimation();

        }
        if(TextUtils.isEmpty(password)){
            password_warning.setVisibility(View.VISIBLE);
            password_warning.playAnimation();
        }
        if(password.length()<6){
            inputPassword.setText("");
            inputPassword.setHint("Password too short!");
            inputPassword.setHintTextColor(ContextCompat.getColor(this, R.color.highlights_orange));
            password_warning.setVisibility(View.VISIBLE);
            password_warning.playAnimation();
        }
        if(!passConfirm.equals(password)){
            inputPassConfirm.setText("");
            inputPassConfirm.setHint("They are different!");
            inputPassConfirm.setHintTextColor(ContextCompat.getColor(this, R.color.highlights_orange));
            passConfirm_warning.setVisibility(View.VISIBLE);
            passConfirm_warning.playAnimation();
        }

        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(id)||
                TextUtils.isEmpty(programme)||TextUtils.isEmpty(password)||
                TextUtils.isEmpty(passConfirm)||id.length()<10||
                password.length()<6||!passConfirm.equals(password)){
            Toast.makeText(this, "Please recheck fields!",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }


    public void createAccount(){
        query = rtDB.getReference().child("login").orderByChild("studentID").equalTo(id);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    insertingDB();
                }else{
                    failCreateAccount();
                    id_warning.setVisibility(View.VISIBLE);
                    id_warning.playAnimation();
                    inputID.setText("");
                    inputID.setHint("ACCOUNT EXIST!");
                    inputID.setHintTextColor(ContextCompat.getColor(SignUpActivity.this, R.color.highlights_orange));

                    Toast.makeText(SignUpActivity.this, "Account with the ID exist!\nPlease login instead!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG,"Error: "+error.getMessage());
            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    public void insertingDB(){
        query.removeEventListener(valueEventListener);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            int elePoints = 0;
                            UserModel UserModel=new UserModel(name,id,email+"@ucsiuniversity.edu.my",programme, password, elePoints);
                            assert user != null;
                            String uid= user.getUid();

                            rtDB.getReference().child("login").child(uid).setValue(UserModel);
                            notifyUser();
                        }
                    }
                });
    }


    public void notifyUser(){
        String subject = "Account created successfully!";
        String message = "Dear "+name+",\n\n"+
                "Your account has been created successfully with the details as such:"+
                "\nName:\t"+name+
                "\nStudent ID:\t"+id+
                "\nEmail:\t"+email+
                "\nProgramme:\t"+programme+
                "\nPassword:\t"+password+
                "\nThank you for using 'Event Hotspot' Mobile Application made by: Daniel Ryan Sunjaya (1001851873)."+
                "\n\nBest regards,\n\nUCSI Event Hotspot";
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, email, subject, message);
        javaMailAPI.execute();

        Log.i(TAG+"[notifyUser]", "Email Notification Successfully sent!");

        signup_btn_animation_loading.pauseAnimation();
        signup_btn_animation_loading.setVisibility(View.GONE);
        signup_btn_animation_check.setVisibility(View.VISIBLE);
        signup_btn_animation_check.playAnimation();

        new Handler().postDelayed(this::closeActivity,TIMER+8000);

    }

    public void failCreateAccount(){
        signup_btn_animation_loading.pauseAnimation();
        signup_btn_animation_loading.setVisibility(View.GONE);
        signup_btn_animation_cross.setVisibility(View.VISIBLE);
        signup_btn_animation_cross.playAnimation();
        new Handler().postDelayed(this::revertState,TIMER+3000);
    }

    public void revertState(){
        signup_btn_animation_check.pauseAnimation();
        signup_btn_animation_check.setVisibility(View.GONE);
        signup_btn_animation_cross.pauseAnimation();
        signup_btn_animation_cross.setVisibility(View.GONE);
        signup_btn_text.setVisibility(View.VISIBLE);
    }

    private void closeActivity() {
        signup_btn_animation_check.pauseAnimation();
        signup_btn_animation_check.setVisibility(View.GONE);
        signup_btn_animation_cross.pauseAnimation();
        signup_btn_animation_cross.setVisibility(View.GONE);
        signup_btn_text.setVisibility(View.VISIBLE);
        Toast.makeText(SignUpActivity.this, "SignUp Success!",Toast.LENGTH_SHORT).show();
        finish();
    }
}