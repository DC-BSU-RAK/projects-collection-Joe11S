package com.example.finalassessmentnot_calculator // Replace with your actual package name

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var leftTotalMass = 0
    private var rightTotalMass = 0

    private lateinit var rodView: View

    private lateinit var leftPanAssembly: View

    private lateinit var rightPanAssembly: View

    private lateinit var beamView: View
    private lateinit var leftPan: GridLayout
    private lateinit var rightPan: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rodView = findViewById(R.id.rodView)
        leftPanAssembly = findViewById(R.id.leftPanAssembly)
        rightPanAssembly = findViewById(R.id.rightPanAssembly)
        leftPan = findViewById(R.id.leftPan)
        rightPan = findViewById(R.id.rightPan)

        val resetAllButton = findViewById<Button>(R.id.resetAllButton)
        val trayScrollView = findViewById<View>(R.id.trayScrollView)
        val infoButton = findViewById<Button>(R.id.infoButton)

        infoButton.setOnClickListener {
            showInstructionsModal()
        }

        // Optional: Show it automatically the very first time the app opens!
        // showInstructionsModal()

        // Grab all our new tray items
        val trayItems = listOf<View>(
            findViewById(R.id.itemStrawberry),
            findViewById(R.id.itemLemon),
            findViewById(R.id.itemApple),
            findViewById(R.id.itemBanana),
            findViewById(R.id.weight1),
            findViewById(R.id.weight10),
            findViewById(R.id.weight100),
            findViewById(R.id.weight1000)
        )

        // Make the master tray items draggable
        trayItems.forEach { setupDraggable(it) }

        // Setup drop targets for the pans
        setupPanDropTarget(leftPan, isLeft = true)
        setupPanDropTarget(rightPan, isLeft = false)

        // Setup the tray area as a drop target to "trash" or reset specific weights
        trayScrollView.setOnDragListener { _, event ->
            if (event.action == DragEvent.ACTION_DROP) {
                val draggedView = event.localState as View
                val sourceParent = draggedView.parent as? ViewGroup

                // If it came from a pan, remove it from the view and subtract the weight
                if (sourceParent == leftPan || sourceParent == rightPan) {
                    val mass = draggedView.tag.toString().toInt()
                    sourceParent.removeView(draggedView)

                    if (sourceParent == leftPan) leftTotalMass -= mass
                    else rightTotalMass -= mass

                    updateScaleUI()
                }
                true
            } else {
                true
            }
        }

        // Global Reset Button
        resetAllButton.setOnClickListener {
            leftPan.removeAllViews()
            rightPan.removeAllViews()
            leftTotalMass = 0
            rightTotalMass = 0
            updateScaleUI()
        }
    }

    private fun setupDraggable(view: View) {
        view.setOnLongClickListener { v ->
            val clipText = v.tag.toString()
            val item = ClipData.Item(clipText)
            val dragData = ClipData(clipText, arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
            val shadow = View.DragShadowBuilder(v)

            v.startDragAndDrop(dragData, shadow, v, 0)

            // If the item being dragged is already inside a pan, hide it momentarily
            if (v.parent == leftPan || v.parent == rightPan) {
                v.visibility = View.INVISIBLE
            }
            true
        }
    }

    private fun setupPanDropTarget(pan: GridLayout, isLeft: Boolean) {
        pan.setOnDragListener { _, event ->
            val draggedView = event.localState as? View ?: return@setOnDragListener false

            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val massValue = draggedView.tag.toString().toInt()
                    val sourceParent = draggedView.parent as? ViewGroup

                    // If dropped in the same pan it started in, just make it visible again
                    if (sourceParent == pan) {
                        draggedView.visibility = View.VISIBLE
                        return@setOnDragListener true
                    }

                    // If moving from one pan to another, deduct from the original pan
                    if (sourceParent == leftPan) {
                        leftPan.removeView(draggedView)
                        leftTotalMass -= massValue
                    } else if (sourceParent == rightPan) {
                        rightPan.removeView(draggedView)
                        rightTotalMass -= massValue
                    }

                    // If it came from the infinite tray below, clone it. Otherwise, use the existing view.
                    val viewToAdd = if (sourceParent == leftPan || sourceParent == rightPan) {
                        draggedView
                    } else {
                        createClone(draggedView, massValue)
                    }

                    pan.addView(viewToAdd)
                    if (isLeft) leftTotalMass += massValue else rightTotalMass += massValue

                    viewToAdd.visibility = View.VISIBLE
                    updateScaleUI()
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    // If the drop failed (dropped in empty space), return it to its pan
                    if (!event.result && (draggedView.parent == leftPan || draggedView.parent == rightPan)) {
                        draggedView.visibility = View.VISIBLE
                    }
                    true
                }
                DragEvent.ACTION_DRAG_STARTED,
                DragEvent.ACTION_DRAG_ENTERED,
                DragEvent.ACTION_DRAG_EXITED -> true
                else -> false
            }
        }
    }

    private fun createClone(original: View, mass: Int): View {
        // Check if the item being dragged is an image (fruit) or text (standard weight)
        return if (original is ImageView) {
            ImageView(this).apply {
                layoutParams = ViewGroup.MarginLayoutParams(100, 100).apply {
                    setMargins(4, 4, 4, 4)
                }
                // Copy the exact image from the tray
                setImageDrawable(original.drawable)
                scaleType = ImageView.ScaleType.FIT_CENTER
                tag = mass.toString()
                setupDraggable(this) // Make the clone draggable too
            }
        } else {
            // Fallback for the standard number weights
            TextView(this).apply {
                layoutParams = ViewGroup.MarginLayoutParams(100, 100).apply {
                    setMargins(4, 4, 4, 4)
                }
                background = original.background
                text = (original as? TextView)?.text
                gravity = android.view.Gravity.CENTER
                textSize = 10f
                tag = mass.toString()
                if (original.id == R.id.weight1000) setTextColor(android.graphics.Color.WHITE)
                setupDraggable(this) // Make the clone draggable too
            }
        }
    }

    private fun updateScaleUI() {

        val difference = leftTotalMass - rightTotalMass

        // Calculate the rotation angle (capped at 30 degrees so items don't "fall out")
        val targetRotation = (difference * -0.05f).coerceIn(-30f, 30f)

        // To keep the pans attached to the ends of the rod, we calculate how far
        // the end of the rod moves up or down using trigonometry.
        // Math: vertical_offset = radius * sin(angle)

        // Get half the width of the rod (the radius from the center pivot)
        val rodRadius = rodView.width / 2f

        // Convert the rotation to radians for the Math.sin function
        val angleRadians = Math.toRadians(targetRotation.toDouble())

        // Calculate the vertical shift
        val verticalOffset = (rodRadius * Math.sin(angleRadians)).toFloat()

        val animationDuration = 500L

        // 1. Rotate the rod
        rodView.animate()
            .rotation(targetRotation)
            .setDuration(animationDuration)
            .start()

        // 2. Translate the Left Pan Assembly UP or DOWN
        // If targetRotation is negative (counter-clockwise), the left side goes DOWN (+Y).
        leftPanAssembly.animate()
            .translationY(-verticalOffset)
            .setDuration(animationDuration)
            .start()

        // 3. Translate the Right Pan Assembly UP or DOWN
        // If targetRotation is negative (counter-clockwise), the right side goes UP (-Y).
        rightPanAssembly.animate()
            .translationY(verticalOffset)
            .setDuration(animationDuration)
            .start()
    }

    private fun showInstructionsModal() {
        // Inflate the custom layout we just created
        val dialogView = layoutInflater.inflate(R.layout.dialog_instructions, null)

        // Build the Dialog
        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Make the background of the dialog window transparent so our custom rounded corners (if we add them) show up
        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))

        // Set up the "Got it!" button inside the dialog
        val btnGotIt = dialogView.findViewById<Button>(R.id.btnGotIt)
        btnGotIt.setOnClickListener {
            dialog.dismiss() // Closes the modal
        }

        dialog.show()
    }
}