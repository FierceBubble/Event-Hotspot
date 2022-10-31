package danielryansunjaya.finalyearproject.eventhotspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;


import danielryansunjaya.finalyearproject.eventhotspot.models.UserModel;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase rtDB;
    EditText inputName, inputID, inputEmail, inputProgramme, inputPassword, inputPassConfirm;
    Button submit;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        rtDB = FirebaseDatabase.getInstance();

        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        inputID = findViewById(R.id.inputID);
        inputProgramme = findViewById(R.id.inputProgramme);
        inputPassword = findViewById(R.id.inputPassword);
        inputPassConfirm = findViewById(R.id.inputPassConfirm);
        submit = findViewById(R.id.signupBtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
            }
        });



    }

    private void createNewAccount(){
        String name = inputName.getText().toString();
        String id = inputID.getText().toString();
        String email = inputEmail.getText().toString();
        String programme = inputProgramme.getText().toString();
        String password = inputPassword.getText().toString();
        String passConfirm = inputPassConfirm.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Name is empty!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(id)){
            Toast.makeText(this, "Student is empty!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Email is empty!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(programme)){
            Toast.makeText(this, "Programme is empty!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Password is empty!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length()<6){
            Toast.makeText(this, "Password should be more than 6 characters!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!passConfirm.equals(password)){
            Toast.makeText(this, "Confirm Password is not the same!",Toast.LENGTH_SHORT).show();
            return;
        }


        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            UserModel UserModel=new UserModel(name,id,email,programme);
                            String uid= user.getUid();

                            rtDB.getReference().child("login").child(uid).setValue(UserModel);

                            finish();

                            Toast.makeText(SignUpActivity.this, "SignUp Success!",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(SignUpActivity.this, "Error: "+task.getException(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}