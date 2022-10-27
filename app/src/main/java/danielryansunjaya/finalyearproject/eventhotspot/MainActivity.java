package danielryansunjaya.finalyearproject.eventhotspot;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private  SceneView backgroundSceneView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backgroundSceneView = findViewById(R.id.backgroundSceneView);

        loadModels();

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            backgroundSceneView.resume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        backgroundSceneView.pause();
    }


    public void loadModels(){
        CompletableFuture<ModelRenderable> ucsi = ModelRenderable
                .builder()
                .setSource(this, Uri.parse("models/ucsiwoe.glb"))
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build();


        CompletableFuture.allOf(ucsi)
                .handle((ok,ex)->{
                    try {
                        Node modelNode1 = new Node();
                        modelNode1.setRenderable(ucsi.get());
                        modelNode1.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
                        modelNode1.setLocalRotation(Quaternion.multiply(
                                Quaternion.axisAngle(new Vector3(0.5f, 0f, 0f), 70),
                                Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 170)));
                        modelNode1.setLocalPosition(new Vector3(-0.03f, 0f, -1.0f));
                        backgroundSceneView.getScene().addChild(modelNode1);



                    } catch (InterruptedException | ExecutionException ignore) {

                    }
                    return null;
                });
    }
}