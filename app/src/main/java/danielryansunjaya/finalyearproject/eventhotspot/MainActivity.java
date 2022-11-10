package danielryansunjaya.finalyearproject.eventhotspot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.QuaternionEvaluator;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import danielryansunjaya.finalyearproject.eventhotspot.ui.EventFragment;


public class MainActivity extends AppCompatActivity{

    private static final int TIMER = 2000;
    private static final String TAG ="MainActivity";
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseDatabase rtDB;
    TextView signupText;
    EditText insertemail,insertpassword;
    Button loginBtn, listAllEventBtn, profileBtn, mapBtn;
    LinearLayout loginLayout, fragmentContainer, fragmentContainer_Profile;
    ConstraintLayout mainLayout;

    @Nullable private ObjectAnimator rotateAnimation = null;
    private Vector3 modelScale = new Vector3(0.3f,0.3f,0.3f);
    private TransformableNode university;
    private TransformationSystem transformationSystem;
    private SceneView sceneView;
    private Scene scene;
    ModelRenderable base_renderable;
    ModelRenderable blockA_renderable;
    ModelRenderable blockB_renderable;
    ModelRenderable blockC_renderable;
    ModelRenderable blockD_renderable;
    ModelRenderable blockG_renderable;
    ViewRenderable viewRenderable;

    CompletableFuture<ModelRenderable> base_stage;
    CompletableFuture<ModelRenderable> blockA_stage;
    CompletableFuture<ModelRenderable> blockB_stage;
    CompletableFuture<ModelRenderable> blockC_stage;
    CompletableFuture<ModelRenderable> blockD_stage;
    CompletableFuture<ModelRenderable> blockG_stage;

    public int totalEvent;
    public boolean isLogin = false;
    public boolean isOnMap = false;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        rtDB = FirebaseDatabase.getInstance();

        insertemail = findViewById(R.id.insertStudentID);
        insertpassword = findViewById(R.id.insertPassword);
        loginLayout = findViewById(R.id.loginLayout);
        mainLayout = findViewById(R.id.mainLayout);
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });
        signupText = findViewById(R.id.signupText);
        String signupTextv = "No Account? Sign-up Here!";
        SpannableString ss = new SpannableString(signupTextv);
        ClickableSpan ss_signupPage = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }

            @Override
            public void updateDrawState(TextPaint ds){
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
            }
        };
        ss.setSpan(ss_signupPage,20,24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signupText.setText(ss);
        signupText.setMovementMethod(LinkMovementMethod.getInstance());


        transformationSystem = new TransformationSystem(getResources().getDisplayMetrics(),new FootprintSelectionVisualizer());
        sceneView = findViewById(R.id.arFragment);
        sceneView.getScene().addOnPeekTouchListener(new Scene.OnPeekTouchListener() {
            @Override
            public void onPeekTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
                if(isLogin && isOnMap){
                    transformationSystem.onTouch(hitTestResult,motionEvent);
                }
            }
        });
        sceneView.getScene().getCamera().setLocalPosition(new Vector3(0,-0.5f,0.5f));
        //sceneView.getScene().getCamera().setLocalPosition(new Vector3(0,-0.25f,0.5f));
        scene=sceneView.getScene();
        initModels();

        // Create Fragments

        fragmentContainer = findViewById(R.id.fragmentContainer);
        listAllEventBtn = findViewById(R.id.listAllEventBtn);
        listAllEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragmentLayout, new EventFragment());
                ft.commit();

                //sceneView.setVisibility(View.INVISIBLE);
                isOnMap = false;
                fragmentContainer.setVisibility(View.VISIBLE);
                fragmentContainer_Profile.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(this::continueAnimation,TIMER);
            }

            public void continueAnimation(){
                startAnimation();
            }
        });

        mapBtn = findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sceneView.setVisibility(View.VISIBLE);
                isOnMap = true;
                fragmentContainer.setVisibility(View.INVISIBLE);
                fragmentContainer_Profile.setVisibility(View.INVISIBLE);
            }

        });

        fragmentContainer_Profile = findViewById(R.id.fragmentContainer_Profile);
        profileBtn = findViewById(R.id.profileBtn);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft_profile = getSupportFragmentManager().beginTransaction();
                ft_profile.replace(R.id.fragmentLayout_Profile, new ProfileFragment());
                ft_profile.commit();

                //sceneView.setVisibility(View.INVISIBLE);
                isOnMap = false;
                fragmentContainer.setVisibility(View.INVISIBLE);
                fragmentContainer_Profile.setVisibility(View.VISIBLE);

                new Handler().postDelayed(this::continueAnimation,TIMER);
            }

            public void continueAnimation(){
                startAnimation();
            }
        });
    }

    private void initModels() {
        base_stage = ModelRenderable.builder().setSource(this,Uri.parse("glbFiles/base.glb")).setIsFilamentGltf(true).setAsyncLoadEnabled(true).build();
        blockA_stage = ModelRenderable.builder().setSource(this,Uri.parse("glbFiles/a.glb")).setIsFilamentGltf(true).setAsyncLoadEnabled(true).build();
        blockB_stage = ModelRenderable.builder().setSource(this,Uri.parse("glbFiles/b.glb")).setIsFilamentGltf(true).setAsyncLoadEnabled(true).build();
        blockC_stage = ModelRenderable.builder().setSource(this,Uri.parse("glbFiles/c.glb")).setIsFilamentGltf(true).setAsyncLoadEnabled(true).build();
        blockD_stage = ModelRenderable.builder().setSource(this,Uri.parse("glbFiles/d.glb")).setIsFilamentGltf(true).setAsyncLoadEnabled(true).build();
        blockG_stage = ModelRenderable.builder().setSource(this,Uri.parse("glbFiles/g.glb")).setIsFilamentGltf(true).setAsyncLoadEnabled(true).build();
        Log.d( TAG+" [initModels]", "All Models Successfully Built!");

        loadModels();
    }

    public void loadModels(){
        WeakReference<MainActivity> weakActivity = new WeakReference<>(this);

        // Runs CompleteableFuture to Render 3D models of the University
        // Prevents multiple buildings not rendered onto the screen
        CompletableFuture.allOf(base_stage, blockA_stage, blockB_stage, blockC_stage,
                        blockD_stage, blockG_stage)
                .handle((notUsed, throwable)->{
                    if(throwable!=null){
                        Toast.makeText(this, "Unable to load model",
                                        Toast.LENGTH_LONG)
                                .show();
                        return null;
                    }
                    try {
                        base_renderable = base_stage.get();
                        base_stage.thenAccept(model -> {
                            MainActivity activity = weakActivity.get();
                            if(activity!=null){
                                activity.base_renderable = model;
                                addNodeToScene(model);
                            }
                        });

                        blockA_renderable = blockA_stage.get();
                        createBuildingNode("Block A", blockA_renderable, blockA_stage, new Vector3(0.23f, 0.2f, -0.6f));

                        blockB_renderable = blockB_stage.get();
                        createBuildingNode("Block B", blockB_renderable, blockB_stage, new Vector3(0.2f, 0.25f, -0.2f));

                        blockC_renderable = blockC_stage.get();
                        createBuildingNode("Block C", blockC_renderable, blockC_stage, new Vector3(0.3f, 0.45f, 0.3f));

                        blockD_renderable = blockD_stage.get();
                        createBuildingNode("Block D", blockD_renderable, blockD_stage,new Vector3(0.3f, 0.4f, 0.75f));

                        blockG_renderable = blockG_stage.get();
                        createBuildingNode("Block G", blockG_renderable, blockG_stage,new Vector3(-0.4f, 0.65f, 0.5f));


                        Log.d(TAG+" loadModels", "Model Successfully Placed on Screen!");

                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
        Log.d(TAG+" loadModels", "Model Successfully Loaded!");
    }

    private void createBuildingNode(String name, ModelRenderable modelRenderable, CompletableFuture<ModelRenderable> stage, Vector3 infoCardPosition){
        stage.thenAccept(model->{
           addOtherNodetoUniversity(name, modelRenderable, infoCardPosition);
        });
        Log.d(TAG+" [createBuildingNode]", "Building "+name+" Added to Base!");
    }


    private void addNodeToScene(ModelRenderable model) {
        university = new TransformableNode(transformationSystem);
        university.getRotationController().setEnabled(true);
        university.getTranslationController().setEnabled(false);
        university.getScaleController().setEnabled(true);
        university.getScaleController().setMaxScale(1f);
        university.getScaleController().setMinScale(0.7f);
        university.setWorldScale(modelScale);
        university.setParent(scene);
        university.setRenderable(model);
        university.setName("UCSI University");
        university.setLocalScale(modelScale);
        university.setLocalPosition(new Vector3(-0.03f, -0.3f, -1.0f));
        university.setOnTouchListener(new Node.OnTouchListener() {
            @Override
            public boolean onTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
                if(isLogin && isOnMap){
                    stopAnimation();
                }
                return false;
            }
        });
        //createBuilding("Block A", blockA_renderable, blockA_stage, new Vector3(0.23f, 0.2f, -0.6f));

        transformationSystem.selectNode(university);
        scene.addChild(university);
        Log.d(TAG+" [addNodeToScene", "Base Model Rendered!");
        startAnimation();
    }

    /*private Node createBuilding(String name, ModelRenderable modelRenderable, CompletableFuture<ModelRenderable> stage, Vector3 infoCardPosition){
        BuildingNode node = new BuildingNode(modelRenderable, stage, name, infoCardPosition, this);
        node.setParent(university);
        node.setRenderable(modelRenderable);
        node.setName(name);
        node.setLocalRotation(Quaternion.multiply(
                Quaternion.axisAngle(new Vector3(0.5f, 0f, 0f), 50),
                Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 170)));

        return node;
    }*/

    private static ObjectAnimator createAnimator() {
        Quaternion orientation1 = Quaternion.axisAngle(new Vector3(0f, 1.0f, 0.0f), 0);
        Quaternion orientation2 = Quaternion.axisAngle(new Vector3(0f, 1.0f, 0.0f), 120);
        Quaternion orientation3 = Quaternion.axisAngle(new Vector3(0f, 1.0f, 0.0f), 240);
        Quaternion orientation4 = Quaternion.axisAngle(new Vector3(0f, 1.0f, 0.0f), 360);

        ObjectAnimator rotateAnimation = new ObjectAnimator();
        rotateAnimation.setObjectValues(orientation1, orientation2, orientation3, orientation4);
        rotateAnimation.setPropertyName("localRotation");
        rotateAnimation.setEvaluator(new QuaternionEvaluator());
        rotateAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnimation.setRepeatMode(ObjectAnimator.RESTART);
        rotateAnimation.setInterpolator(new LinearInterpolator());

        return  rotateAnimation;
    }

    private void addOtherNodetoUniversity(String name, ModelRenderable modelRenderable, Vector3 infoCardPosition){
        Node otherBuilding = new Node();
        otherBuilding.setParent(university);
        otherBuilding.setRenderable(modelRenderable);
        otherBuilding.setName(name);
        otherBuilding.setLocalRotation(Quaternion.multiply(
                Quaternion.axisAngle(new Vector3(0.5f, 0f, 0f), 50),
                Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 170)));
        otherBuilding.setOnTapListener(new Node.OnTapListener() {
            @Override
            public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                if(isLogin && isOnMap){
                    nodeTap(name, otherBuilding, infoCardPosition);
                }
            }
        });
        //university.addChild(otherBuilding);
        Log.d(TAG+" [addOtherNodetoUniversity]", "Building "+name+" Rendered!");
    }

    private void nodeTap(String name, Node parent, Vector3 infoCardPosition) {
        WeakReference<MainActivity> weakActivity = new WeakReference<>(this);
        String nameTrim = name.replace("Block","").trim();
        Log.d(TAG+" [Node Tapped]", "Node "+nameTrim+" Tap!");
        totalEvent = 0;

        ViewRenderable.builder()
                .setView(this, R.layout.building_options)
                .build()
                .thenAccept(
                        (viewRenderable) -> {
                            MainActivity activity = weakActivity.get();
                            if(activity!=null){
                                TextView building_name = (TextView) viewRenderable.getView().findViewById(R.id.infoCard_text);
                                Button button = (Button) viewRenderable.getView().findViewById(R.id.infoCard_btn);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        buttonCLick(name);
                                    }
                                });

                                db.collection("eventList")
                                        .whereEqualTo("location", nameTrim)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){
                                                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                                        totalEvent+=1;
                                                    }
                                                    building_name.setText(name+" - Event Total: "+totalEvent);
                                                }else{
                                                    Log.w(TAG+" [FirebaseStore_RetrieveData]", "Error getting documents.", task.getException());
                                                }
                                            }
                                        });

                                activity.viewRenderable = viewRenderable;
                                make_infoCard(name, parent, viewRenderable, infoCardPosition);
                            }
                        })
                .exceptionally(
                        (throwable) -> {
                            throw new AssertionError("Could not load plane card view.", throwable);
                        });
    }

    private void buttonCLick(String name) {
        Toast.makeText(this,"Button "+name+" Clicked!",Toast.LENGTH_LONG).show();
    }

    private void make_infoCard(String name, Node parent, ViewRenderable viewRenderable, Vector3 infoCardPosition){
        Node infoCard = new Node();
        infoCard.setRenderable(viewRenderable);
        infoCard.setEnabled(true);
        infoCard.setParent(parent);
        infoCard.setName("InfoCard: "+name);
        infoCard.setLocalScale(modelScale);
        infoCard.setLocalPosition(infoCardPosition);
        Log.d(TAG+" [make_infoCard]", "InfoCard "+name+" Created!");
    }

    private void startAnimation(){
        if(rotateAnimation != null){
            return;
        }
        // Add Ratate Animation to the 3D model
        rotateAnimation = createAnimator();
        rotateAnimation.setTarget(university);
        rotateAnimation.setDuration(7000);
        rotateAnimation.start();
        Log.d(TAG+" [Rotate Animation]", "Animation Started!");
    }

    private void stopAnimation(){
        if(rotateAnimation == null){
            return;
        }
        rotateAnimation.cancel();
        rotateAnimation = null;
        Log.d(TAG+" [Rotate Animation]", "Animation Stopped!");
    }

    private void userLogin(){
        String email = insertemail.getText().toString() + "@ucsiuniversity.edu.my";
        String password = insertpassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Email is empty!",Toast.LENGTH_SHORT).show();
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

        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            isLogin = true;
                            isOnMap = true;
                            loginLayout.setVisibility(View.INVISIBLE);
                            mainLayout.setVisibility(View.VISIBLE);
                            Log.d(TAG+" [userLogin]", "Login Success!");
                        }else {
                            Toast.makeText(MainActivity.this, "Student ID or Password is wrong!",Toast.LENGTH_SHORT).show();
                            Log.d(TAG+" [userLogin]", "Login Failed!");
                        }
                    }
                });
    }


    @Override
    protected  void onResume(){
        super.onResume();
        try {
            sceneView.resume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sceneView.pause();
    }
}
