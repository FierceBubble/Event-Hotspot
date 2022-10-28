package danielryansunjaya.finalyearproject.eventhotspot;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.filament.utils.Utils;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity{

    private static final float INFO_CARD_Y_POS = 0.55f;
    private SceneView sceneView;
    private Scene scene;
    ModelRenderable blockA_renderable;
    ModelRenderable blockB_renderable;
    ModelRenderable blockC_renderable;
    ModelRenderable blockD_renderable;
    ModelRenderable blockG_renderable;
    ViewRenderable viewRenderable;
    private TransformableNode university;
    private TransformationSystem transformationSystem;

    CompletableFuture<ModelRenderable> blockA_stage;
    CompletableFuture<ModelRenderable> blockB_stage;
    CompletableFuture<ModelRenderable> blockC_stage;
    CompletableFuture<ModelRenderable> blockD_stage;
    CompletableFuture<ModelRenderable> blockG_stage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        CompletableFuture.allOf(blockA_stage, blockB_stage, blockC_stage,
                        blockD_stage, blockG_stage)
                .handle((notUsed, throwable)->{
                    if(throwable!=null){
                        Toast.makeText(this, "Unable to load model",
                                        Toast.LENGTH_LONG)
                                .show();
                        return null;
                    }
                    try {
                        blockA_renderable = blockA_stage.get();
                        blockA_stage.thenAccept(model -> {
                            MainActivity activity = weakActivity.get();
                            if(activity!=null){
                                activity.blockA_renderable = model;
                                addNodeToScene(model);
                            }
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

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                });

    }

    private void addOtherNodetoUniversity(String name, ModelRenderable modelRenderable){
        Node otherBuilding = new Node();
        otherBuilding.setParent(university);
        otherBuilding.setRenderable(modelRenderable);
        otherBuilding.setName(name);
        otherBuilding.setOnTapListener(new Node.OnTapListener() {
            @Override
            public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                nodeTap(name, otherBuilding);
            }
        });
        university.addChild(otherBuilding);

        /*Building building = new Building(name, 0, modelRenderable, this);
        building.setParent(otherBuilding);*/
    }

    private void nodeTap(String name, Node parent) {
        WeakReference<MainActivity> weakActivity = new WeakReference<>(this);
        Toast.makeText(this,"Node "+name+" Tap!",Toast.LENGTH_LONG).show();

        ViewRenderable.builder()
                .setView(this, R.layout.building_options)
                .build()
                .thenAccept(
                        (viewRenderable) -> {
                            MainActivity activity = weakActivity.get();
                            if(activity!=null){
                                TextView building_name = (TextView) viewRenderable.getView().findViewById(R.id.infoCard_text);
                                building_name.setText(name);
                                activity.viewRenderable = viewRenderable;
                                make_infoCard(name, parent, viewRenderable);
                            }

                        })
                .exceptionally(
                        (throwable) -> {
                            throw new AssertionError("Could not load plane card view.", throwable);
                        });
    }

    private void make_infoCard(String name, Node parent, ViewRenderable viewRenderable){
        Node infoCard = new Node();
        infoCard.setRenderable(viewRenderable);
        infoCard.setEnabled(true);
        infoCard.setParent(parent);
        infoCard.setName("InfoCard: "+name);
        infoCard.setLocalPosition(new Vector3(0.0f, INFO_CARD_Y_POS, 0.0f));
        Log.d("make_infoCard", "InfoCard "+name+" successfully created!");

    }

    private void addNodeToScene(ModelRenderable model) {
        university = new TransformableNode(transformationSystem);
        university.getRotationController().setEnabled(true);
        university.getTranslationController().setEnabled(false);
        university.getScaleController().setEnabled(true);
        university.getScaleController().setMaxScale(1f);
        university.getScaleController().setMinScale(0.7f);
        university.setParent(scene);
        university.setRenderable(model);
        university.setName("UCSI University");
        university.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
        university.setLocalRotation(Quaternion.multiply(
                Quaternion.axisAngle(new Vector3(0.5f, 0f, 0f), 70),
                Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 170)));
        university.setLocalPosition(new Vector3(-0.03f, 0f, -1.0f));
        transformationSystem.selectNode(university);
        scene.addChild(university);
    }

    private Node createBuilding(String name, Node parent, ModelRenderable modelRenderable, float scale){
        Building building = new Building(name, scale, modelRenderable, this);
        building.setParent(parent);
        building.setLocalPosition(new Vector3(0,0,0));

        return building;
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
