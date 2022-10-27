package danielryansunjaya.finalyearproject.eventhotspot;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    private SceneView sceneView;
    private Scene scene;
    private Renderable model;
    private ViewRenderable viewRenderable;
    private TransformationSystem transformationSystem;

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

        loadModels();

    }


    public void loadModels(){
        WeakReference<MainActivity> weakActivity = new WeakReference<>(this);
        ModelRenderable.builder()
                .setSource(this, Uri.parse("models/ucsismall.glb"))
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(model -> {
                    MainActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.model = model;
                        addNodeToScene(model);
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(
                            this, "Unable to load model", Toast.LENGTH_LONG).show();
                    return null;
                });

    }

    private void addNodeToScene(ModelRenderable model) {

        TransformableNode university = new TransformableNode(transformationSystem);
        university.getRotationController().setEnabled(true);
        university.getTranslationController().setEnabled(false);
        university.getScaleController().setEnabled(true);
        university.setParent(scene);
        university.setRenderable(this.model);
        university.setName("UCSI University");
        university.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
        university.setLocalRotation(Quaternion.multiply(
                Quaternion.axisAngle(new Vector3(0.5f, 0f, 0f), 70),
                Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 170)));
        university.setLocalPosition(new Vector3(-0.03f, 0f, -1.0f));
        transformationSystem.selectNode(university);
        scene.addChild(university);
    }


    private Node University(){
        Node blockA = new Node();
        blockA.setRenderable(blockA.getRenderable());

        Node blockB = new Node();
        blockB.setParent(blockA);
        blockB.setLocalPosition(
                new Vector3(0f,1f,2f)
        );
        blockB.setRenderable(blockB.getRenderable());

        Node blockC = new Node();
        blockC.setParent(blockB);
        blockC.setLocalPosition(
                new Vector3(0f,1.5f,2f)
        );
        blockC.setRenderable(blockC.getRenderable());

        Node blockD = new Node();
        blockD.setParent(blockC);
        blockD.setLocalPosition(
                new Vector3(0f,0f,2.5f)
        );
        blockD.setRenderable(blockD.getRenderable());

        Node blockG = new Node();
        blockG.setParent(blockC);
        blockG.setLocalPosition(
                new Vector3(2.5f,1f,1f)
        );
        blockG.setRenderable(blockG.getRenderable());

        return blockA;

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
