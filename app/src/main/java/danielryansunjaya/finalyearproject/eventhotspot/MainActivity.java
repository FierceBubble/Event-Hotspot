package danielryansunjaya.finalyearproject.eventhotspot;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
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

public class MainActivity extends AppCompatActivity {

    private SceneView sceneView;
    private Scene scene;
    private ModelRenderable blockA_renderable;
    private ModelRenderable blockB_renderable;
    private ModelRenderable blockC_renderable;
    private ModelRenderable blockD_renderable;
    private ModelRenderable blockG_renderable;
    private ViewRenderable viewRenderable;
    private TransformableNode university;
    private TransformationSystem transformationSystem;

    /*CompletableFuture<ModelRenderable> blockA_stage;
    CompletableFuture<ModelRenderable> blockB_stage;
    CompletableFuture<ModelRenderable> blockC_stage;
    CompletableFuture<ModelRenderable> blockD_stage;
    CompletableFuture<ModelRenderable> blockG_stage;*/

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

        /*initModels();
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
                        blockB_renderable = blockB_stage.get();
                        blockC_renderable = blockC_stage.get();
                        blockD_renderable = blockD_stage.get();
                        blockG_renderable = blockG_stage.get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                });*/
        loadModels();

    }

    /*private void initModels() {
        blockA_stage = ModelRenderable.builder().setSource(this,Uri.parse("models/a.glb")).setIsFilamentGltf(true).setAsyncLoadEnabled(true).build();
        blockB_stage = ModelRenderable.builder().setSource(this,Uri.parse("models/b.glb")).setIsFilamentGltf(true).setAsyncLoadEnabled(true).build();
        blockC_stage = ModelRenderable.builder().setSource(this,Uri.parse("models/c.glb")).setIsFilamentGltf(true).setAsyncLoadEnabled(true).build();
        blockD_stage = ModelRenderable.builder().setSource(this,Uri.parse("models/d.glb")).setIsFilamentGltf(true).setAsyncLoadEnabled(true).build();
        blockG_stage = ModelRenderable.builder().setSource(this,Uri.parse("models/g.glb")).setIsFilamentGltf(true).setAsyncLoadEnabled(true).build();
        Log.d("MainActivity", "Model Successfully Built");
    }*/


    public void loadModels(){
        WeakReference<MainActivity> weakActivity = new WeakReference<>(this);
        ModelRenderable.builder()
                .setSource(this, Uri.parse("models/a.glb"))
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(model -> {
                    MainActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.blockA_renderable = model;
                        addNodeToScene(model);
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(
                            this, "Unable to load model", Toast.LENGTH_LONG).show();
                    return null;
                });

        ModelRenderable.builder()
                .setSource(this, Uri.parse("models/b.glb"))
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(model -> {
                    MainActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.blockB_renderable = model;
                        addOtherNodetoUniversity("Block B",model);
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(
                            this, "Unable to load model B", Toast.LENGTH_LONG).show();
                    return null;
                });

        ModelRenderable.builder()
                .setSource(this, Uri.parse("models/c.glb"))
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(model -> {
                    MainActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.blockC_renderable = model;
                        addOtherNodetoUniversity("Block C",model);
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(
                            this, "Unable to load model C", Toast.LENGTH_LONG).show();
                    return null;
                });

        ModelRenderable.builder()
                .setSource(this, Uri.parse("models/d.glb"))
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(model -> {
                    MainActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.blockD_renderable = model;
                        addOtherNodetoUniversity("Block D",model);
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(
                            this, "Unable to load model D", Toast.LENGTH_LONG).show();
                    return null;
                });

        ModelRenderable.builder()
                .setSource(this, Uri.parse("models/g.glb"))
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(model -> {
                    MainActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.blockG_renderable = model;
                        addOtherNodetoUniversity("Block G",model);
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(
                            this, "Unable to load model G", Toast.LENGTH_LONG).show();
                    return null;
                });

        //activity.blockB_renderable = blockB_renderable;
        //addOtherBuilding("Block B", blockB_renderable, university, "models/b.glb");



    }

    /*private  void addOtherBuilding(String name, ModelRenderable modelRenderable, TransformableNode parent, String source){
        WeakReference<MainActivity> weakActivity = new WeakReference<>(this);
        ModelRenderable.builder()
                .setSource(this, Uri.parse(source))
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(blockB_renderable -> {
                    MainActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.blockB_renderable = blockB_renderable;
                        addOtherNodetoUniversity(name,blockB_renderable);
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(
                            this, "Unable to load model B", Toast.LENGTH_LONG).show();
                    return null;
                });

        //createBuilding(name, parent, modelRenderable,0);
    }*/

    private void addOtherNodetoUniversity(String name, ModelRenderable modelRenderable){
        Node otherBuilding = new Node();
        otherBuilding.setParent(university);
        otherBuilding.setRenderable(modelRenderable);
        otherBuilding.setName(name);
        university.addChild(otherBuilding);
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

    private Node createUniversity(){
        Node base = new Node();

        createBuilding("Block A", base, blockA_renderable,0);
        createBuilding("Block B", base, blockB_renderable,0);
        createBuilding("Block C", base, blockC_renderable,0);
        createBuilding("Block D", base, blockD_renderable,0);
        createBuilding("Block G", base, blockG_renderable,0);
        return base;
    }

    private Node createBuilding(String name, Node parent, ModelRenderable modelRenderable, float scale){
        Building building = new Building(name, scale, modelRenderable, this);
        building.setParent(parent);
        building.setLocalPosition(new Vector3(0,0,0));

        return building;
    }


    /*private TransformableNode University(){
        TransformableNode blockA = new TransformableNode(transformationSystem);
        blockA.setRenderable(blockA_renderable);

        Node blockB = new Node();
        blockB.setParent(blockA);
        blockB.setLocalPosition(
                new Vector3(0f,0.1f,0.2f)
        );
        blockB.setRenderable(blockB_renderable);

        Node blockC = new Node();
        blockC.setParent(blockB);
        blockC.setLocalPosition(
                new Vector3(0f,0.3f,0.5f)
        );
        blockC.setRenderable(blockC_renderable);

        Node blockD = new Node();
        blockD.setParent(blockC);
        blockD.setLocalPosition(
                new Vector3(0f,0f,1f)
        );
        blockD.setRenderable(blockD_renderable);

        Node blockG = new Node();
        blockG.setParent(blockC);
        blockG.setLocalPosition(
                new Vector3(1.5f,0.5f,0.3f)
        );
        blockG.setRenderable(blockG_renderable);

        return blockA;
    }*/

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
