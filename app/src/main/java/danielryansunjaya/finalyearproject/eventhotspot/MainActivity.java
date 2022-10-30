package danielryansunjaya.finalyearproject.eventhotspot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.filament.utils.Utils;
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
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity{

    private FirebaseFirestore db;

    private SceneView sceneView;
    private Scene scene;
    ModelRenderable base_renderable;
    ModelRenderable blockA_renderable;
    ModelRenderable blockB_renderable;
    ModelRenderable blockC_renderable;
    ModelRenderable blockD_renderable;
    ModelRenderable blockG_renderable;
    ViewRenderable viewRenderable;
    private Vector3 modelScale = new Vector3(0.3f,0.3f,0.3f);
    private TransformableNode university;
    private TransformationSystem transformationSystem;
    @Nullable private ObjectAnimator rotateAnimation = null;

    CompletableFuture<ModelRenderable> base_stage;
    CompletableFuture<ModelRenderable> blockA_stage;
    CompletableFuture<ModelRenderable> blockB_stage;
    CompletableFuture<ModelRenderable> blockC_stage;
    CompletableFuture<ModelRenderable> blockD_stage;
    CompletableFuture<ModelRenderable> blockG_stage;

    public int totalEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       db = FirebaseFirestore.getInstance();

        transformationSystem = new TransformationSystem(getResources().getDisplayMetrics(),new FootprintSelectionVisualizer());
        sceneView = findViewById(R.id.arFragment);
        sceneView.getScene().addOnPeekTouchListener(new Scene.OnPeekTouchListener() {
            @Override
            public void onPeekTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
                transformationSystem.onTouch(hitTestResult,motionEvent);
            }
        });

        sceneView.getScene().getCamera().setLocalPosition(new Vector3(0,-0.25f,0.5f));
        scene=sceneView.getScene();

        initModels();
    }

    private void initModels() {
        base_stage = ModelRenderable.builder().setSource(this,Uri.parse("models/base.glb")).setIsFilamentGltf(true).setAsyncLoadEnabled(true).build();
        blockA_stage = ModelRenderable.builder().setSource(this,Uri.parse("models/a.glb")).setIsFilamentGltf(true).setAsyncLoadEnabled(true).build();
        blockB_stage = ModelRenderable.builder().setSource(this,Uri.parse("models/b.glb")).setIsFilamentGltf(true).setAsyncLoadEnabled(true).build();
        blockC_stage = ModelRenderable.builder().setSource(this,Uri.parse("models/c.glb")).setIsFilamentGltf(true).setAsyncLoadEnabled(true).build();
        blockD_stage = ModelRenderable.builder().setSource(this,Uri.parse("models/d.glb")).setIsFilamentGltf(true).setAsyncLoadEnabled(true).build();
        blockG_stage = ModelRenderable.builder().setSource(this,Uri.parse("models/g.glb")).setIsFilamentGltf(true).setAsyncLoadEnabled(true).build();
        Log.d("MainActivity", "Model Successfully Built");

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
                        blockA_stage.thenAccept(model -> {
                            blockA_renderable = model;
                            addOtherNodetoUniversity("Block A",model);

                        });
                        blockB_renderable = blockB_stage.get();
                        blockB_stage.thenAccept(model -> {
                            blockB_renderable = model;
                            addOtherNodetoUniversity("Block B",model);

                        });
                        blockC_renderable = blockC_stage.get();
                        blockC_stage.thenAccept(model -> {
                            blockC_renderable = model;
                            addOtherNodetoUniversity("Block C",model);

                        });
                        blockD_renderable = blockD_stage.get();
                        blockD_stage.thenAccept(model -> {
                            blockD_renderable = model;
                            addOtherNodetoUniversity("Block D",model);

                        });
                        blockG_renderable = blockG_stage.get();
                        blockG_stage.thenAccept(model -> {
                            blockG_renderable = model;
                            addOtherNodetoUniversity("Block G",model);

                        });

                        Log.d("MainActivity", "Model Successfully Placed on Screen!");

                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                });

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
        university.setLocalRotation(Quaternion.multiply(
                Quaternion.axisAngle(new Vector3(0.5f, 0f, 0f), 70),
                Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 170)));
        university.setOnTouchListener(new Node.OnTouchListener() {
            @Override
            public boolean onTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
                stopAnimation();
                return false;
            }
        });
        transformationSystem.selectNode(university);
        scene.addChild(university);

        startAnimation();
    }

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

    private void addOtherNodetoUniversity(String name, ModelRenderable modelRenderable){
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
                nodeTap(name, otherBuilding);
            }
        });
        university.addChild(otherBuilding);

    }

    private void nodeTap(String name, Node parent) {
        WeakReference<MainActivity> weakActivity = new WeakReference<>(this);
        String nameTrim = name.replace("Block","").trim();
        Log.d("Node Tapped", "Node "+nameTrim+" Tap!");
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
                                                    Log.w("FirebaseStore_RetrieveData", "Error getting documents.", task.getException());
                                                }
                                            }
                                        });

                                activity.viewRenderable = viewRenderable;
                                make_infoCard(name, parent, viewRenderable);
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

    private void make_infoCard(String name, Node parent, ViewRenderable viewRenderable){
        Node infoCard = new Node();
        infoCard.setRenderable(viewRenderable);
        infoCard.setEnabled(true);
        infoCard.setParent(parent);
        infoCard.setName("InfoCard: "+name);
        infoCard.setLocalScale(modelScale);
        switch (name){
            case "Block A":
                infoCard.setLocalPosition(new Vector3(0.23f, 0.2f, -0.6f));
                break;
            case "Block B":
                infoCard.setLocalPosition(new Vector3(0.2f, 0.25f, -0.2f));
                break;
            case "Block C":
                infoCard.setLocalPosition(new Vector3(0.3f, 0.45f, 0.3f));
                break;
            case "Block D":
                infoCard.setLocalPosition(new Vector3(0.3f, 0.4f, 0.75f));
                break;
            case "Block G":
                infoCard.setLocalPosition(new Vector3(-0.4f, 0.65f, 0.5f));
                break;
        }

        Log.d("make_infoCard", "InfoCard "+name+" successfully created!");

    }

    private void startAnimation(){
        if(rotateAnimation != null){
            return;
        }
        // Add Ratate Animation to the 3D model
        rotateAnimation = createAnimator();
        rotateAnimation.setTarget(university);
        rotateAnimation.setDuration(5000);
        rotateAnimation.start();
    }

    private void stopAnimation(){
        if(rotateAnimation == null){
            return;
        }
        rotateAnimation.cancel();
        rotateAnimation = null;
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
