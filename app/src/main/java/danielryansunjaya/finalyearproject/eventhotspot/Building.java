package danielryansunjaya.finalyearproject.eventhotspot;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.TextView;

import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

public class Building extends Node implements Node.OnTapListener{
    private final String name;
    private final float scale;
    private final ModelRenderable modelRenderable;
    private final Context context;

    private Node infoCard;
    private static final float INFO_CARD_Y_POS = 0.55f;

    public Building(String name, float scale, ModelRenderable modelRenderable, Context context) {
        this.name = name;
        this.scale = scale;
        this.modelRenderable = modelRenderable;
        this.context = context;
    }

    @Override
    public void onActivate(){
        if(getScene()==null){
            throw new IllegalStateException("Scene is null!");
        }

        if (infoCard == null) {
            infoCard = new Node();
            infoCard.setParent(this);
            infoCard.setEnabled(false);
            infoCard.setLocalPosition(new Vector3(0.0f, scale * INFO_CARD_Y_POS, 0.0f));

            ViewRenderable.builder()
                    .setView(context, R.layout.building_detail_cardview)
                    .build()
                    .thenAccept(
                            (modelRenderable) -> {
                                infoCard.setRenderable(modelRenderable);
                                TextView buildingtext = (TextView) modelRenderable.getView().findViewById(R.id.infoCard_text);
                                buildingtext.setText(name);
                            })
                    .exceptionally(
                            (throwable) -> {
                                throw new AssertionError("Could not load plane card view.", throwable);
                            });
        }
    }

    // Creating infoCard when user tap on a building
    @Override
    public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
        if (infoCard == null) {
            return;
        }

        infoCard.setEnabled(!infoCard.isEnabled());
    }
}
