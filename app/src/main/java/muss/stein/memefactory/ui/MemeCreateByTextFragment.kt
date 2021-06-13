package muss.stein.memefactory.ui

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.theartofdev.edmodo.cropper.CropImage
import muss.stein.memefactory.R
import muss.stein.memefactory.databinding.FragmentMemeCreateByTextBinding
import muss.stein.memefactory.ui.dialog.InsertTextDialog
import java.io.IOException
import java.util.*
import kotlin.math.atan2
import kotlin.math.hypot


class MemeCreateByTextFragment : Fragment(R.layout.fragment_meme_create_by_text),
    InsertTextDialog.OnInsertTextDialogClick {

    lateinit var binding: FragmentMemeCreateByTextBinding
    private val args: MemeCreateByTextFragmentArgs by navArgs()

    val textViewAdded: MutableList<View> = mutableListOf()
    val imageViewAdded: MutableList<View> = mutableListOf()

    //touch listener
    private var xDelta = 0
    private var yDelta = 0

    //rotate listener
    var viewRotation = 0
    var fingerRotation = 0
    var newFingerRotation = 0

    //resize listener
    var downx = 0
    var downy = 0
    var upx = 0
    var upy = 0
    private var bitmap: Bitmap? = null
    var imageView: ImageView? = null
    var scaleView: ImageView? = null

    var centerX = 0
    var centerY = 0
    var startX = 0
    var startY = 0
    var startR = 0
    var startScale = 0


    //imageCrop
    private var picImageContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }

    }
    private lateinit var picImageLauncher: ActivityResultLauncher<Any?>


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMemeCreateByTextBinding.bind(view)
        if (args.resource == -1) {
            val uri = args.uri?.toUri()
            val bitmap =
                MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri)
            binding.ivMemeCreateText.setImageBitmap(bitmap)

        } else {
            binding.ivMemeCreateText.setBackgroundResource(args.resource)
        }



        binding.btAddTextView.setOnClickListener {
            InsertTextDialog(this).show(childFragmentManager, "insertTextDialog")

        }
        binding.btSaveView.setOnClickListener {
            imageViewAdded.forEach { view ->
                val removeView = view.findViewById<ImageView>(R.id.ivRemoveIvCreated)
                val scaleView = view.findViewById<ImageView>(R.id.ivCreatedEnlarge)
                val removeSelection = view.findViewById<ImageView>(R.id.ivRemoveSelectionView)
                removeView.visibility = View.INVISIBLE
                scaleView.visibility = View.INVISIBLE
                removeSelection.visibility = View.INVISIBLE
            }
            textViewAdded.forEach { view ->
                val delete = view.findViewById<ImageView>(R.id.ivDeleteTvCreated)
                val rotate = view.findViewById<ImageView>(R.id.ivRotateTvCreated)
                val remove = view.findViewById<ImageView>(R.id.ivRemoveSettings)
                delete.visibility = View.INVISIBLE
                rotate.visibility = View.INVISIBLE
                remove.visibility = View.INVISIBLE
            }
            val signature = screenShot(binding.rlIvHolder)
            savePhotoToInternalStorage(UUID.randomUUID().toString(), signature!!)
            Toast.makeText(requireContext(), "saved", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
        binding.btAddImageView.setOnClickListener {
            picImageLauncher.launch(null)
        }
        picImageLauncher = registerForActivityResult(picImageContract) {

            val viewToInflate = requireActivity().layoutInflater.inflate(
                R.layout.custom_image_view,
                binding.rlMemeByText,
                false
            )
            imageViewAdded.add(viewToInflate)
            val imageView = viewToInflate.findViewById<ImageView>(R.id.IvCreated)
            val removeView = viewToInflate.findViewById<ImageView>(R.id.ivRemoveIvCreated)
            val scaleView = viewToInflate.findViewById<ImageView>(R.id.ivCreatedEnlarge)
            val removeSelection = viewToInflate.findViewById<ImageView>(R.id.ivRemoveSelectionView)
            it?.let { uri ->
                imageView.setImageURI(uri)

                scaleView!!.setOnTouchListener(onTouchResizeListener)
                removeSelection.setOnClickListener {
                    removeView.visibility = View.INVISIBLE
                    scaleView.visibility = View.INVISIBLE
                    removeSelection.visibility = View.INVISIBLE
                }
                viewToInflate.setOnClickListener {
                    removeView.visibility = View.VISIBLE
                    scaleView.visibility = View.VISIBLE
                    removeSelection.visibility = View.VISIBLE
                }
                viewToInflate.setOnTouchListener(onTouchListener)
                removeView.setOnClickListener {
                    binding.rlIvHolder.removeView(viewToInflate)
                }
                binding.rlIvHolder.addView(viewToInflate)
            }


        }


    }

    private val onTouchResizeListener = View.OnTouchListener { view, event ->
        view.performClick()
        val owner = view.parent as ViewGroup
        val imageView = owner.findViewById<ImageView>(R.id.IvCreated)
        val scaleView = owner.findViewById<ImageView>(R.id.ivCreatedEnlarge)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // calculate center of image
                centerX = ((imageView!!.left + imageView.right) / 2f).toInt()
                centerY = ((imageView.top + imageView.bottom) / 2f).toInt()

                // recalculate coordinates of starting point
                startX = (event.rawX - scaleView.x + centerX).toInt()
                startY = (event.rawY - scaleView.y + centerY).toInt()

                // get starting distance and scale
                startR = hypot(
                    (event.rawX - startX).toDouble(),
                    (event.rawY - startY).toDouble()
                ).toInt()
                startScale = imageView.scaleX.toInt()
            }
            MotionEvent.ACTION_MOVE -> {

                val newR = hypot(
                    (event.rawX - startX).toDouble(),
                    (event.rawY - startY).toDouble()
                );

                // set new scale
                val newScale = newR / startR * startScale;
                owner.scaleX = newScale.toFloat()
                owner.scaleY = newScale.toFloat()
                imageView!!.scaleX = newScale.toFloat()
                imageView.scaleY = newScale.toFloat()

                // move handler image
//                scaleView!!.setX((centerX + imageView!!.getWidth()/2f * newScale).toFloat());
//                scaleView!!.setY((centerY + imageView!!.getHeight()/2f * newScale).toFloat())
            }
            MotionEvent.ACTION_UP -> {
                // calculate center of image
                centerX = ((imageView!!.left + imageView.right) / 2f).toInt()
                centerY = ((imageView.top + imageView.bottom) / 2f).toInt()

                // recalculate coordinates of starting point
                startX = (event.rawX - scaleView.x + centerX).toInt()
                startY = (event.rawY - scaleView.y + centerY).toInt()

                // get starting distance and scale
                startR = hypot(
                    (event.rawX - startX).toDouble(),
                    (event.rawY - startY).toDouble()
                ).toInt()
                startScale = imageView.scaleX.toInt()
            }
        }
        true

    }
    private val onTouchListener = View.OnTouchListener { view, event ->
        view.performClick()
        val x = event.rawX
        val y = event.rawY
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                val lParams = view.layoutParams as RelativeLayout.LayoutParams
                xDelta = (x - lParams.leftMargin).toInt()
                yDelta = (y - lParams.topMargin).toInt()
            }
            MotionEvent.ACTION_UP -> {

            }
            MotionEvent.ACTION_MOVE -> {
                val layoutParams = view.layoutParams as RelativeLayout.LayoutParams
                layoutParams.leftMargin = (x - xDelta).toInt()
                layoutParams.topMargin = (y - yDelta).toInt()
                layoutParams.rightMargin = 0;
                layoutParams.bottomMargin = 0
                view.layoutParams = layoutParams
            }
        }
        binding.rlMemeByText.invalidate()
        true
    }
    private val onTouchRotateListener = View.OnTouchListener { view, event ->
        view.performClick()
        val x = event.rawX
        val y = event.rawY
        val owner = view.parent as ViewGroup
        val xc = binding.rlMemeByText.width / 2;
        val yc = binding.rlMemeByText.height / 2


        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                viewRotation = owner.rotation.toInt();
                fingerRotation =
                    Math.toDegrees(atan2((x - xc).toDouble(), (yc - y).toDouble())).toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                newFingerRotation = Math.toDegrees(
                    atan2(
                        (x - xc).toDouble(),
                        (yc - y).toDouble()
                    )
                ).toInt();
                owner.rotation = ((viewRotation + newFingerRotation - fingerRotation).toFloat());
            }
            MotionEvent.ACTION_UP -> {
                fingerRotation = 0
                newFingerRotation = 0
            }

        }
        true

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onInsertDialogClick(text: String, textSize: Float, textColor: Int) {
        val viewToInflate = requireActivity().layoutInflater.inflate(
            R.layout.custom_text_view,
            binding.rlMemeByText,
            false
        )
        textViewAdded.add(viewToInflate)
        val tv = viewToInflate.findViewById<TextView>(R.id.tvCreated)
        val delete = viewToInflate.findViewById<ImageView>(R.id.ivDeleteTvCreated)
        val rotate = viewToInflate.findViewById<ImageView>(R.id.ivRotateTvCreated)
        val remove = viewToInflate.findViewById<ImageView>(R.id.ivRemoveSettings)

        tv.text = text
        tv.textSize = textSize
        tv.setTextColor(textColor)


        delete.setOnClickListener {
            binding.rlIvHolder.removeView(viewToInflate)
        }
        remove.setOnClickListener {
            delete.visibility = View.INVISIBLE
            rotate.visibility = View.INVISIBLE
            remove.visibility = View.INVISIBLE
        }

        rotate.setOnTouchListener(onTouchRotateListener)


        binding.rlIvHolder.addView(viewToInflate)
        viewToInflate.setOnTouchListener(onTouchListener)
        viewToInflate.setOnClickListener {
            delete.visibility = View.VISIBLE
            rotate.visibility = View.VISIBLE
            remove.visibility = View.VISIBLE
        }

    }

    private fun savePhotoToInternalStorage(filename: String, bmp: Bitmap): Boolean {
        return try {
            requireActivity().openFileOutput("$filename.jpg", Context.MODE_PRIVATE).use { stream ->
                if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap.")
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }


    private fun screenShot(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


}
