package danielryansunjaya.finalyearproject.eventhotspot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.textfield.TextInputLayout;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import danielryansunjaya.finalyearproject.eventhotspot.models.EventModel;
import danielryansunjaya.finalyearproject.eventhotspot.models.UserModel;
import danielryansunjaya.finalyearproject.eventhotspot.ui.EventFragment;
import danielryansunjaya.finalyearproject.eventhotspot.ui.ProfileFragment;
import danielryansunjaya.finalyearproject.eventhotspot.utils.JavaMailAPI;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity{

    private static final int TIMER = 2000;
    private static final String TAG ="MainActivity";
    private String email, password;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseDatabase rtDB;
    FirebaseStorage storage;
    TextView signupText, login_btn_text, forgotPass_text, eventSelect_title, eventSelect_details;
    EditText insertEmail, insertPassword, insertID_forgotPass;
    LinearLayout listAllEventBtn, profileBtn, mapBtn;
    MotionLayout main_motionLayout, user_main_layout;
    LinearLayout loginLayout, fragmentContainer, fragmentContainer_Profile, dropDown_layout;
    ConstraintLayout mainLayout;
    RelativeLayout login_btn_layout, forgot_btn_layout, eventSelect_join_btn;
    LottieAnimationView login_btn_animation_loading, login_btn_animation_check,
            login_btn_animation_cross, forgot_btn_animation_loading,
            forgot_btn_animation_check, forgot_btn_animation_cross,
            eventSelect_btn_animation_loading, eventSelect_btn_animation_check,
            eventSelect_btn_animation_cross;

    CircleImageView profile_picture_main, profile_picture_fragment,
            logout_btn, user_profile_menu;

    ArrayList<EventModel> event_list_eventModellist;
    ArrayList<String> event_list;
    AutoCompleteTextView autoCompleteTextView;
    TextInputLayout textInputLayout;
    ArrayAdapter<String> arrayAdapter;


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
        storage = FirebaseStorage.getInstance();

        insertEmail = findViewById(R.id.insertStudentID);
        insertPassword = findViewById(R.id.insertPassword);
        loginLayout = findViewById(R.id.loginLayout);
        mainLayout = findViewById(R.id.mainLayout);
        profile_picture_main = findViewById(R.id.user_profile_picture);
        profile_picture_fragment = findViewById(R.id.static_profile_picture);

        main_motionLayout = findViewById(R.id.mainActivity_motionLayout);
        user_main_layout = findViewById(R.id.user_main_layout);
        login_btn_text = findViewById(R.id.login_button_text);
        login_btn_animation_loading = findViewById(R.id.login_button_animation_loading);
        login_btn_animation_check = findViewById(R.id.login_button_animation_check);
        login_btn_animation_cross = findViewById(R.id.login_button_animation_cross);
        login_btn_layout = findViewById(R.id.login_button_layout);
        login_btn_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertEmail.setActivated(false);
                insertPassword.setActivated(false);

                loginLayout.requestFocus();
                forgotPass_text.setVisibility(View.GONE);
                login_btn_text.setVisibility(View.GONE);
                login_btn_animation_loading.setVisibility(View.VISIBLE);
                login_btn_animation_loading.playAnimation();
                new Handler().postDelayed(this::checkInfo,3000);
            }

            private void checkInfo() {
                if(noEmptyField()){
                    userLogin();
                }else{
                    loginFailState();
                }
            }
        });

        forgotPass_text = findViewById(R.id.forgotPassText);
        String forgotTextv = "Forgot password?";
        SpannableString fss = new SpannableString(forgotTextv);
        ClickableSpan fss_forgotPass = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                // Start Alert Dialog to get ID and send email notification!
                forgotPassAlert();
            }

            @Override
            public void updateDrawState(TextPaint ds){
                super.updateDrawState(ds);
                ds.setColor(Color.WHITE);
            }
        };
        fss.setSpan(fss_forgotPass,0, forgotTextv.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        forgotPass_text.setText(fss);
        forgotPass_text.setMovementMethod(LinkMovementMethod.getInstance());


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
                ds.setColor(Color.WHITE);
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

        logout_btn = findViewById(R.id.user_profile_logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });

        user_profile_menu = findViewById(R.id.user_profile_menu_btn);
        user_profile_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOnMap = false;
                fragmentContainer.setVisibility(View.INVISIBLE);
                fragmentContainer_Profile.setVisibility(View.VISIBLE);
                main_motionLayout.transitionToState(R.id.show_profile_page);

                new Handler().postDelayed(this::continueAnimation,TIMER);
            }
            public void continueAnimation(){
                startAnimation();
            }
        });

        // Create Fragments
        fragmentContainer = findViewById(R.id.fragmentContainer);
        listAllEventBtn = findViewById(R.id.listAllEventBtn);
        listAllEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //sceneView.setVisibility(View.INVISIBLE);
                isOnMap = false;
                fragmentContainer.setVisibility(View.VISIBLE);
                fragmentContainer_Profile.setVisibility(View.INVISIBLE);
                //user_main_layout.transitionToStart();
                main_motionLayout.transitionToState(R.id.show_event_list);
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
                //user_main_layout.transitionToStart();
                main_motionLayout.transitionToState(R.id.back_to_end_from_event);
            }

        });

        fragmentContainer_Profile = findViewById(R.id.fragmentContainer_Profile);
        profileBtn = findViewById(R.id.profileBtn);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //sceneView.setVisibility(View.INVISIBLE);
                isOnMap = false;
                fragmentContainer.setVisibility(View.INVISIBLE);
                fragmentContainer_Profile.setVisibility(View.VISIBLE);
                //user_main_layout.transitionToStart();
                main_motionLayout.transitionToState(R.id.show_profile_page);

                new Handler().postDelayed(this::continueAnimation,TIMER);
            }

            public void continueAnimation(){
                startAnimation();
            }
        });

        autoCompleteTextView = findViewById(R.id.dropdown_list);
        textInputLayout = findViewById(R.id.dropdown_textInputLayout);
        dropDown_layout = findViewById(R.id.dropdown_Layout);
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
        university.setLocalPosition(new Vector3(-0.03f, -0.5f, -1.0f));
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
        textInputLayout.setEnabled(true);
        autoCompleteTextView.setText("");
        textInputLayout.setHint(name);

        String slice_name = name.replaceAll("Block ","");
        event_list = new ArrayList<>();
        event_list_eventModellist = new ArrayList<>();
        db.collection("eventList").whereEqualTo("location", slice_name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document: task.getResult()){
                                EventModel selectEvent_eventModel = document.toObject(EventModel.class);
                                event_list.add(selectEvent_eventModel.getTitle());
                                event_list_eventModellist.add(selectEvent_eventModel);
                            }


                            arrayAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.list_event_item_dropdown, event_list);
                            autoCompleteTextView.setAdapter(arrayAdapter);
                            autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                                    final View customLayout = getLayoutInflater().inflate(R.layout.selected_event_layout, null);
                                    builder.setView(customLayout);

                                    AlertDialog event_dialog = builder.create();
                                    event_dialog.getWindow().setBackgroundDrawableResource(R.drawable.eventselect_dialog_layout_bckg);
                                    event_dialog.show();

                                    TextView join_text = customLayout.findViewById(R.id.eventSelect_dialog_button_text);

                                    eventSelect_title = customLayout.findViewById(R.id.eventSelect_title);
                                    eventSelect_details = customLayout.findViewById(R.id.eventSelect_description);
                                    eventSelect_btn_animation_loading = customLayout.findViewById(R.id.eventSelect_dialog_button_animation_loading);
                                    eventSelect_btn_animation_check = customLayout.findViewById(R.id.eventSelect_dialog_button_animation_check);
                                    eventSelect_btn_animation_cross = customLayout.findViewById(R.id.eventSelect_dialog_button_animation_cross);
                                    eventSelect_join_btn = customLayout.findViewById(R.id.eventSelect_dialog_button_layout);
                                    eventSelect_join_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            joinEvent_FromDropDown(event_list_eventModellist.get(i));
                                            event_dialog.dismiss();
                                        }
                                    });

                                    ImageView eventSelect_cancel_btn = customLayout.findViewById(R.id.eventSelect_cancel_btn);
                                    eventSelect_cancel_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            event_dialog.dismiss();
                                        }
                                    });

                                    db.collection("studentsJoinEvents")
                                            .document(auth.getUid())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        DocumentSnapshot documentSnapshot = task.getResult();
                                                        List<String> userEventList = (List<String>) documentSnapshot.get("eventList");

                                                        if(userEventList.contains(adapterView.getItemAtPosition(i).toString())){
                                                            eventSelect_join_btn.setClickable(false);
                                                            join_text.setText("You Already Joined!");
                                                        }
                                                    }
                                                }
                                            });
                                    eventSelect_title.setText(event_list_eventModellist.get(i).getTitle());
                                    eventSelect_details.setText("Organizer: "+event_list_eventModellist.get(i).getOrganizer()+"\n"
                                                                +"PIC: "+event_list_eventModellist.get(i).getPic()+"\n"
                                                                +event_list_eventModellist.get(i).getEmail()+"\n\n"
                                                                +event_list_eventModellist.get(i).getDate()+"\n"
                                                                +event_list_eventModellist.get(i).getTime()+"\n"
                                                                +"Block "+event_list_eventModellist.get(i).getLocation()+"\n"
                                                                +"Ele Points: "+event_list_eventModellist.get(i).getElePoint());

                                }
                            });
                        }
                    }
                });

        /*WeakReference<MainActivity> weakActivity = new WeakReference<>(this);
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
                        });*/
    }

    private void joinEvent_FromDropDown(EventModel selectedEvent_eventModel){
        // Creating a hashmap to structure the data
        HashMap<String, Object> eventInfo=new HashMap<>();

        eventInfo.put("title", selectedEvent_eventModel.getTitle());
        eventInfo.put("organizer", selectedEvent_eventModel.getOrganizer());
        eventInfo.put("pic", selectedEvent_eventModel.getPic());
        eventInfo.put("email", selectedEvent_eventModel.getEmail());
        eventInfo.put("date", selectedEvent_eventModel.getDate());
        eventInfo.put("time", selectedEvent_eventModel.getTime());
        eventInfo.put("elePoint", selectedEvent_eventModel.getElePoint());
        eventInfo.put("location", selectedEvent_eventModel.getLocation());
        eventInfo.put("attended", false);
        eventInfo.put("completed", false);

        // Inserting event informations to "joinedEventList" collection
        db.collection("studentsJoinEvents")
                .document(Objects.requireNonNull(auth.getUid()))
                .collection("joinedEventList")
                .document(selectedEvent_eventModel.getTitle())
                .set(eventInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(TAG+" [addEventToProfile]","Successfully added/updated new event to joinEventList!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG+" [addEventToProfile]","Failed to added/updated new event to joinEventList!");
                    }
                });

        // Update array "eventList"
        db.collection("studentsJoinEvents")
                .document(auth.getUid())
                .collection("joinedEventList")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<String> event = new ArrayList<>();
                            for(QueryDocumentSnapshot document: task.getResult()){
                                EventModel eventModel = document.toObject(EventModel.class);
                                event.add(eventModel.getTitle());
                            }

                            HashMap<String, Object> hashEvent = new HashMap<>();
                            hashEvent.put("eventList", event);
                            db.collection("studentsJoinEvents")
                                    .document(auth.getUid())
                                    .set(hashEvent)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.i(TAG+" [addEventToProfile]","EventList array is successfully added/updated!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i(TAG+" [addEventToProfile]","Failed to added/updated eventList array!");
                                        }
                                    });
                        } else {
                            Toast.makeText(MainActivity.this, "Error"+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        sendEmailNotif(selectedEvent_eventModel.getTitle());

        if(!selectedEvent_eventModel.getEmail().isEmpty() && selectedEvent_eventModel.getEmail().contains("https")){
            // Open link for Registration
            Uri uri = Uri.parse(selectedEvent_eventModel.getEmail());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }else if (!selectedEvent_eventModel.getEmail().isEmpty() && selectedEvent_eventModel.getEmail().contains("@")){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { selectedEvent_eventModel.getEmail() });
            intent.putExtra(Intent.EXTRA_SUBJECT, "Request to join " + selectedEvent_eventModel.getTitle());
            intent.putExtra(Intent.EXTRA_TEXT, "I want to join this Event!");
            startActivity(intent);
        }

        Toast.makeText(MainActivity.this,"Event Joined!",Toast.LENGTH_SHORT).show();
    }

    private void sendEmailNotif(String title){
        rtDB.getReference()
                .child("login")
                .child(Objects.requireNonNull(auth.getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        assert userModel != null;
                        String sName = userModel.getName();
                        String sID = userModel.getStudentID();
                        String sEmail = userModel.getEmail();

                        String subject = "Successfully join "+title+"!";
                        String message = "Dear "+sName+" - ("+sID+"),\n"+"Your request to joining ["+title+"] event has been confirmed!";

                        JavaMailAPI javaMailAPI = new JavaMailAPI(MainActivity.this, sEmail, subject, message);
                        javaMailAPI.execute();
                        Log.i(TAG+" [sendEmailNotif]","Successfully send email notification for the joined event!");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG+" [sendEmailNotif]","Failed send email notification for the joined event!");
                        throw error.toException();
                    }
                });
    }

    /*private void buttonCLick(String name) {
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
    }*/

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

    private Boolean noEmptyField(){
        email = insertEmail.getText().toString() + "@ucsiuniversity.edu.my";
        password = insertPassword.getText().toString();

        if(TextUtils.isEmpty(insertEmail.getText().toString())){
            insertEmail.setActivated(true);
            //Toast.makeText(this, "Email is empty!",Toast.LENGTH_SHORT).show();
        }
        if(insertEmail.getText().toString().length()<10){
            insertEmail.setActivated(true);
            //Toast.makeText(this, "ID should be more than 10 characters!",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            insertPassword.setActivated(true);
            //Toast.makeText(this, "Password is empty!",Toast.LENGTH_SHORT).show();
        }
        if(password.length()<6){
            insertPassword.setActivated(true);
            //Toast.makeText(this, "Password should be more than 6 characters!",Toast.LENGTH_SHORT).show();
        }

        return !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) &&
                password.length() >= 6 && email.length() >= 10;
    }

    private void userLogin(){

        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            loginSuccessState();
                            Log.d(TAG+" [userLogin]", "Login Success!");
                        }else {
                            loginFailState();
                            insertEmail.setActivated(true);
                            insertPassword.setActivated(true);
                            forgotPass_text.setVisibility(View.VISIBLE);
                            //Toast.makeText(MainActivity.this, "Student ID or Password is wrong!",Toast.LENGTH_SHORT).show();
                            Log.d(TAG+" [userLogin]", "Login Failed!");
                        }
                    }
                });
    }

    private void forgotPassAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final View customLayout = getLayoutInflater().inflate(R.layout.forgot_password_layout, null);
        builder.setView(customLayout);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.forgotpass_dialog_layout_bckg);
        dialog.show();

        TextView submit_text = customLayout.findViewById(R.id.forgotPass_dialog_button_text);

        insertID_forgotPass = customLayout.findViewById(R.id.forgotPass_insertID);
        forgot_btn_animation_loading = customLayout.findViewById(R.id.forgotPass_dialog_button_animation_loading);
        forgot_btn_animation_check = customLayout.findViewById(R.id.forgotPass_dialog_button_animation_check);
        forgot_btn_animation_cross = customLayout.findViewById(R.id.forgotPass_dialog_button_animation_cross);
        forgot_btn_layout = customLayout.findViewById(R.id.forgotPass_dialog_button_layout);
        forgot_btn_layout.setOnClickListener(new View.OnClickListener() {
            Query query;
            String name, password, email, id;
            @Override
            public void onClick(View view) {
                id = insertID_forgotPass.getText().toString();
                submit_text.setVisibility(View.GONE);
                insertID_forgotPass.clearFocus();
                insertID_forgotPass.setActivated(false);
                forgot_btn_animation_loading.setVisibility(View.VISIBLE);
                forgot_btn_animation_loading.playAnimation();
                new Handler().postDelayed(this::checkID,TIMER);
            }

            private void checkID() {
                Log.d(TAG+"[Check ID]",id);
                query = rtDB.getReference()
                        .child("login")
                        .orderByChild("studentID")
                        .equalTo(id);
                ValueEventListener valueEventListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            String uid = ds.getKey();
                            Log.d(TAG+"[Check UID]",uid);
                            sendMail(uid);
                        }

                        if(!snapshot.exists()){
                            insertID_forgotPass.setActivated(true);
                            insertID_forgotPass.setText("");
                            insertID_forgotPass.setHint("ACCOUNT DOES NOT EXIST!");
                            insertID_forgotPass.setHintTextColor(ContextCompat.getColor(MainActivity.this, R.color.dialog_text_and_highlight_orange));

                            forgot_btn_animation_loading.setVisibility(View.GONE);
                            forgot_btn_animation_loading.pauseAnimation();
                            forgot_btn_animation_cross.setVisibility(View.VISIBLE);
                            forgot_btn_animation_cross.playAnimation();
                            new Handler().postDelayed(this::resetForgotState, TIMER+3000);
                        }
                    }

                    private void resetForgotState() {
                        submit_text.setVisibility(View.VISIBLE);
                        forgot_btn_animation_cross.setVisibility(View.GONE);
                        forgot_btn_animation_cross.pauseAnimation();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG+"[Alert Dialog]","Error: "+error.getMessage());
                    }
                };
                query.addListenerForSingleValueEvent(valueEventListener);

            }

            private void sendMail(String uid){
                assert uid != null;
                rtDB.getReference()
                        .child("login")
                        .child(uid)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if(task.isSuccessful()){
                                    UserModel userModel = task.getResult().getValue(UserModel.class);
                                    assert userModel != null;
                                    name = userModel.getName();
                                    email = userModel.getEmail();
                                    password = userModel.getPassword();
                                    new Handler().postDelayed(this::sendPass, TIMER);
                                }
                            }

                            private void sendPass() {

                                String subject = "Forgot Password!";
                                String message = "Dear "+name+"["+id+"],\n\n"+
                                        "You have requested {Forgot Password}!\n"+
                                        "Your password is:\t"+password+
                                        "\n\nBest regards,\n\nUCSI Event Hotspot";
                                JavaMailAPI javaMailAPI = new JavaMailAPI(MainActivity.this, email, subject, message);
                                javaMailAPI.execute();
                                new Handler().postDelayed(this::close, TIMER);
                            }

                            private void close() {
                                forgot_btn_animation_check.setVisibility(View.VISIBLE);
                                forgot_btn_animation_check.playAnimation();
                                new Handler().postDelayed(this::closeDialog,TIMER);
                            }

                            private void closeDialog() {
                                forgot_btn_animation_check.setVisibility(View.GONE);
                                forgot_btn_animation_check.pauseAnimation();
                                dialog.dismiss();
                            }
                        });
            }
        });
    }

    private void layoutPostLogin(){
        isLogin = true;
        isOnMap = true;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentLayout, new EventFragment());
        ft.commit();
        FragmentTransaction ft_profile = getSupportFragmentManager().beginTransaction();
        ft_profile.replace(R.id.fragmentLayout_Profile, new ProfileFragment());
        ft_profile.commit();

        rtDB.getReference()
                .child("login")
                .child(Objects.requireNonNull(auth.getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        assert userModel != null;
                        if(userModel.getImageURI()!=null){
                            Glide.with(MainActivity.this).load(userModel.getImageURI()).into(profile_picture_main);
                            Glide.with(MainActivity.this).load(userModel.getImageURI()).into(profile_picture_fragment);
                            Log.d(TAG+"[Load Profile Pic]","Successfully Load Profile Picture");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG+"[Load Profile Pic]","Error: "+error.getMessage());
                    }
                });

        loginLayout.setVisibility(View.INVISIBLE);
        mainLayout.setVisibility(View.VISIBLE);
        insertEmail.setText("");
        insertPassword.setText("");
        main_motionLayout.transitionToEnd();
        user_main_layout.transitionToEnd();
        new Handler().postDelayed(this::revertState, 3000);
    }

    private void loginSuccessState(){
        login_btn_animation_loading.setVisibility(View.GONE);
        login_btn_animation_loading.pauseAnimation();
        login_btn_animation_check.setVisibility(View.VISIBLE);
        login_btn_animation_check.playAnimation();
        new Handler().postDelayed(this::layoutPostLogin, 2000);
    }

    private void loginFailState(){
        login_btn_animation_loading.setVisibility(View.GONE);
        login_btn_animation_loading.pauseAnimation();
        login_btn_animation_cross.setVisibility(View.VISIBLE);
        login_btn_animation_cross.playAnimation();
        new Handler().postDelayed(this::revertState, 3000);
    }

    private void revertState(){
        login_btn_animation_cross.setVisibility(View.GONE);
        login_btn_animation_cross.pauseAnimation();
        login_btn_animation_check.setVisibility(View.GONE);
        login_btn_animation_check.pauseAnimation();
        login_btn_text.setVisibility(View.VISIBLE);
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