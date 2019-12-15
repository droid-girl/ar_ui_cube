package no.realitylab.uicube

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var arFragment: CustomArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = sceneform_fragment as CustomArFragment

        arFragment.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->
            if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                return@setOnTapArPlaneListener
            }
            val anchor = hitResult.createAnchor()
            placeObject(arFragment, anchor)
        }
    }

    private fun placeObject(fragment: ArFragment, anchor: Anchor) {
        MaterialFactory.makeOpaqueWithColor(this, Color(android.graphics.Color.LTGRAY))
            .thenAccept { material: Material? ->
                val cubeRenderable =
                    ShapeFactory.makeCube(Vector3(0.3f, 0.3f, 0.3f), Vector3(0.0f, 0.15f, 0.0f), material)

                addToScene(fragment, anchor, cubeRenderable)

            }
            .exceptionally {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.message).setTitle("Error")
                val dialog = builder.create()
                dialog.show()
                return@exceptionally null
            }
    }

    private fun addToScene(fragment: ArFragment, anchor: Anchor, renderable: Renderable) {
        arFragment.arSceneView.planeRenderer.isVisible = false
        val anchorNode = AnchorNode(anchor)
        val cubeNode = UiCubeRenderable(this,fragment.transformationSystem, renderable, "text", 0.3f, true)
        cubeNode.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
    }
}
