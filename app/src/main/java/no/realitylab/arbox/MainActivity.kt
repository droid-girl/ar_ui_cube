package no.realitylab.arbox

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
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var arFragment: ArFragment
    var selectedRenderable: Renderable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = sceneform_fragment as ArFragment

        arFragment.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->
            if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                return@setOnTapArPlaneListener
            }
            val anchor = hitResult.createAnchor()
            placeObject(arFragment, anchor)
        }
    }

    private fun placeObject(fragment: ArFragment, anchor: Anchor) {
        MaterialFactory.makeOpaqueWithColor(this, Color(android.graphics.Color.RED))
            .thenAccept { material: Material? ->
                val redSphereRenderable =
                    ShapeFactory.makeCube(Vector3(0.5f, 0.5f, 0.5f), Vector3.zero(), material)
                addToScene(fragment, anchor, redSphereRenderable)

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
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(fragment.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        node.scaleController.isEnabled = false
        node.setOnTapListener { hitTestResult, motionEvent ->
            hitTestResult.node?.let {
                if (selectedRenderable != node.renderable) {
                    MaterialFactory.makeOpaqueWithColor(this, Color(android.graphics.Color.RED))
                        .thenAccept { material: Material? ->
                            selectedRenderable?.material = material
                        }

                }
                MaterialFactory.makeOpaqueWithColor(this, Color(android.graphics.Color.TRANSPARENT))
                    .thenAccept { material: Material? ->
                        selectedRenderable = node.renderable
                        node.renderable?.material = material
                    }
            }
        }
        fragment.arSceneView.scene.addChild(anchorNode)
        node.select()
    }
}
