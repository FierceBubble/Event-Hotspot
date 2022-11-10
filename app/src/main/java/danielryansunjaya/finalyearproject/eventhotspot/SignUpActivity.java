package danielryansunjaya.finalyearproject.eventhotspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;


import danielryansunjaya.finalyearproject.eventhotspot.models.UserModel;
import danielryansunjaya.finalyearproject.eventhotspot.utils.JavaMailAPI;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase rtDB;
    EditText inputName, inputID, inputProgramme, inputPassword, inputPassConfirm;

    private static final int TIMER = 2000;
    TextView signup_btn_text;
    RelativeLayout signup_btn_layout;
    LottieAnimationView signup_btn_animation_loading,
            signup_btn_animation_check, signup_btn_animation_cross;

    private String name, id, email, programme, password, passConfirm;


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

        signup_btn_text = findViewById(R.id.signup_button_text);
        signup_btn_animation_loading = findViewById(R.id.signup_button_animation_loading);
        signup_btn_animation_check = findViewById(R.id.signup_button_animation_check);
        signup_btn_animation_cross = findViewById(R.id.signup_button_animation_cross);
        signup_btn_layout = findViewById(R.id.signup_button_layout);
        signup_btn_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup_btn_text.setVisibility(View.GONE);
                signup_btn_animation_loading.setVisibility(View.VISIBLE);
                signup_btn_animation_loading.playAnimation();

                new Handler().postDelayed(this::insertInfo,TIMER);
            }

            public void insertInfo(){
                if(noEmptyFields()){
                    signup_btn_animation_loading.pauseAnimation();
                    signup_btn_animation_loading.setVisibility(View.GONE);
                    signup_btn_animation_check.setVisibility(View.VISIBLE);
                    signup_btn_animation_check.playAnimation();
                    new Handler().postDelayed(this::revertState,TIMER+3000);
                }else{
                    signup_btn_animation_loading.pauseAnimation();
                    signup_btn_animation_loading.setVisibility(View.GONE);
                    signup_btn_animation_cross.setVisibility(View.VISIBLE);
                    signup_btn_animation_cross.playAnimation();
                    new Handler().postDelayed(this::revertState,TIMER+3000);
                }

            }

            public void revertState(){
                signup_btn_animation_check.pauseAnimation();
                signup_btn_animation_check.setVisibility(View.GONE);
                signup_btn_animation_cross.pauseAnimation();
                signup_btn_animation_cross.setVisibility(View.GONE);
                signup_btn_text.setVisibility(View.VISIBLE);
            }
        });



    }

    public Boolean noEmptyFields(){
        name = inputName.getText().toString();
        id = inputID.getText().toString();
        email = inputID.getText().toString();
        programme = inputProgramme.getText().toString();
        password = inputPassword.getText().toString();
        passConfirm = inputPassConfirm.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Name is empty!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(id)){
            Toast.makeText(this, "Student is empty!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(programme)){
            Toast.makeText(this, "Programme is empty!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Password is empty!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.length()<6){
            Toast.makeText(this, "Password should be more than 6 characters!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!passConfirm.equals(password)){
            Toast.makeText(this, "Confirm Password is not the same!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void createNewAccount(){


        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            int elePoints = 0;
                            UserModel UserModel=new UserModel(name,id,email+"@ucsiuniversity.edu.my",programme, elePoints);
                            assert user != null;
                            String uid= user.getUid();

                            rtDB.getReference().child("login").child(uid).setValue(UserModel);

                            finish();

                            Toast.makeText(SignUpActivity.this, "SignUp Success!",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(SignUpActivity.this, "Error: "+task.getException(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        notifyUser();
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
    }

}