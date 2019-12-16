package no.realitylab.uicube

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
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
            addToScene(anchor)
        }
    }

    private fun addToScene(anchor: Anchor) {
        arFragment.arSceneView.planeRenderer.isVisible = false
        val anchorNode = AnchorNode(anchor)
        val cubeNode = UiCubeRenderable(this,arFragment.transformationSystem, "text",
            0.3f, true)
        cubeNode.setParent(anchorNode)
        arFragment.arSceneView.scene.addChild(anchorNode)
    }
}
