package danielryansunjaya.finalyearproject.eventhotspot.models;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;

import danielryansunjaya.finalyearproject.eventhotspot.MainActivity;
import danielryansunjaya.finalyearproject.eventhotspot.R;

public class BuildingNode extends Node implements Node.OnTapListener{

    private final ModelRenderable modelRenderable;
    private final CompletableFuture<ModelRenderable> stage;
    private final String name;
    private final Vector3 infoCard_Position;
    private final Context context;

    private Node infoCard;
    public int totalEvent;

    FirebaseFirestore db;

    public BuildingNode(ModelRenderable modelRenderable, CompletableFuture<ModelRenderable> stage, String name, Vector3 infoCard_Position, Context context) {
        this.modelRenderable = modelRenderable;
        this.stage = stage;
        this.name = name;
        this.infoCard_Position = infoCard_Position;
        this.context = context;

        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
        if (infoCard == null) {
            return;
        }
        infoCard.setEnabled(!infoCard.isEnabled());
    }

    /*private void nodeTap(String name, Node parent, Vector3 infoCardPosition) {
        String nameTrim = name.replace("Block","").trim();
        Log.d("Node Tapped", "Node "+nameTrim+" Tap!");
        totalEvent = 0;

        ViewRenderable.builder()
                .setView(context, R.layout.building_options)
                .build()
                .thenAccept(
                        (viewRenderable) -> {

                                TextView building_name = (TextView) viewRenderable.getView().findViewById(R.id.infoCard_text);
                                Button button = (Button) viewRenderable.getView().findViewById(R.id.infoCard_btn);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

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
                                make_infoCard(name, parent, viewRenderable, infoCardPosition);

                        })
                .exceptionally(
                        (throwable) -> {
                            throw new AssertionError("Could not load plane card view.", throwable);
                        });
    }*/
}
