package muss.stein.memefactory.ui

import alertDialogShow
import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import muss.stein.memefactory.R
import muss.stein.memefactory.adapter.AdapterColorPalette
import muss.stein.memefactory.databinding.FragmentMemeCreateBinding
import muss.stein.memefactory.paint.PaintView
import muss.stein.memefactory.ui.dialog.InsertTextDialog
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.math.atan2


class MemeCreateFragment : Fragment(R.layout.fragment_meme_create),
    AdapterColorPalette.OnNoteColorAdapterClick{



    lateinit var binding: FragmentMemeCreateBinding
    private val args: MemeCreateFragmentArgs by navArgs()
    lateinit var adapterColorPalette: AdapterColorPalette
    lateinit var paintView: PaintView



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMemeCreateBinding.bind(view)
        initPalette()

        paintView = requireActivity().findViewById(R.id.paintViewCanvas)
        paintView.init( args.resource)


        binding.ivSaveDraw.setOnClickListener {
            val signature = screenShot(binding.llPaintViewContainer)
            savePhotoToInternalStorage(UUID.randomUUID().toString(), signature!!)
            Toast.makeText(requireContext(), "saved", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
        binding.ivClearDraw.setOnClickListener {
            alertDialogShow(requireContext()){
                paintView.clear()
            }.show()
        }
        binding.ivDrawPen.setOnClickListener {
            binding.cvBrushSize.visibility=View.VISIBLE
        }
        binding.brushSize1.setOnClickListener {
            binding.cvBrushSize.visibility=View.GONE
            paintView.brushSize(5)
        }
        binding.brushSize2.setOnClickListener {
            binding.cvBrushSize.visibility=View.GONE
            paintView.brushSize(10)
        }
        binding.brushSize3.setOnClickListener {
            binding.cvBrushSize.visibility=View.GONE
            paintView.brushSize(30)
        }
        binding.brushSize4.setOnClickListener {
            binding.cvBrushSize.visibility=View.GONE
            paintView.brushSize(40)
        }



    }



    private fun initPalette() {
        val colorList = listOf(
            Color.GREEN,
            Color.RED,
            Color.rgb(200, 100, 120),
            Color.rgb(100, 200, 0),
            Color.LTGRAY,
            Color.CYAN,
            Color.BLACK,
            Color.rgb(10, 130, 200),
            Color.rgb(200, 200, 20),
            Color.rgb(200, 0, 200)
        )
        adapterColorPalette = AdapterColorPalette(colorList, this)
        binding.rvColor.apply {
            adapter = adapterColorPalette
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

    }
    override fun onClick(colorRes: Int) {
        val drawable= AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_draw_24)
        drawable?.let {
            val wrappedDrawable = DrawableCompat.wrap(it).apply {
                setTint(colorRes)
            }
            binding.ivDrawPen.background=wrappedDrawable
        }
        paintView.pen(colorRes)
    }
    private fun savePhotoToInternalStorage(filename: String, bmp: Bitmap): Boolean {
        return try {
            requireActivity().openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream ->
                if(!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap.")
                }
            }
            true
        } catch(e: IOException) {
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