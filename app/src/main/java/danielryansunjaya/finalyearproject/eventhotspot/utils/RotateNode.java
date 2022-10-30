package danielryansunjaya.finalyearproject.eventhotspot.utils;

import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;
import androidx.annotation.Nullable;

import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.QuaternionEvaluator;
import com.google.ar.sceneform.math.Vector3;

public class RotateNode extends Node {
    @Nullable private ObjectAnimator rotateAnimation = null;
    private float lastSpeedMultiplier = 1.0f;
    private final animationSettings settings;

    public RotateNode(animationSettings settings){
        this.settings = settings;
    }

    @Override
    public void onUpdate(FrameTime frameTime){
        super.onUpdate(frameTime);

        if (rotateAnimation==null){
            return;
        }


    }

}
