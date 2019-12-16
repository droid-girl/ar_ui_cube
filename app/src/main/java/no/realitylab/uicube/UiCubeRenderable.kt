package no.realitylab.uicube

import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.widget.TextView
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem


class UiCubeRenderable(
    context: Context,
    transformationSystem: TransformationSystem,
    text: String,
    size: Float,
    private var billboarding: Boolean
): TransformableNode(transformationSystem), Node.OnTapListener {

    private var uiElement = Node()

    init {

        MaterialFactory.makeOpaqueWithColor(context, Color(Color.LTGRAY))
            .thenAccept { material: Material? ->
                renderable =
                    ShapeFactory.makeCube(Vector3(size, size, size),
                        Vector3(0.0f, size / 2.0f, 0.0f), material)
            }
            .exceptionally {
                println("Could not create a cube")
                return@exceptionally null
            }

        uiElement.setParent(this)
        uiElement.isEnabled = true
        uiElement.localPosition = Vector3(0.0f, size, 0.0f)

        ViewRenderable.builder()
            .setView(context, R.layout.label_layout)
            .build()
            .thenAccept { uiRenderable: ViewRenderable ->
                uiRenderable.isShadowCaster = false
                uiRenderable.isShadowReceiver = false
                uiElement.setRenderable(uiRenderable)
                val textView  = uiRenderable.view.findViewById<TextView>(R.id.title)
                textView.text = text
            }
            .exceptionally { throwable: Throwable? ->
                throw AssertionError(
                    "Could not create ui element",
                    throwable
                )
            }

        setOnTapListener(this)
    }

    override fun onUpdate(frameTime: FrameTime?) {
        if (billboarding) {
            scene?.let {
                val cameraPosition = it.camera.worldPosition
                val uiPosition: Vector3 = uiElement.worldPosition
                val direction = Vector3.subtract(cameraPosition, uiPosition)
                direction.y = 0.0f
                val lookRotation =
                    Quaternion.lookRotation(direction, Vector3.up())
                uiElement.worldRotation = lookRotation
            }
        }
    }

    override fun onTap(hitTestResult: HitTestResult?, motionEvent: MotionEvent?) {
        showUiElement(!uiElement.isEnabled)
    }

    fun showUiElement(show: Boolean) {
        uiElement.isEnabled = show
    }
}