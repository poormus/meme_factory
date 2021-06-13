package muss.stein.memefactory.ui.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import muss.stein.memefactory.R
import muss.stein.memefactory.adapter.AdapterColorPalette
import muss.stein.memefactory.databinding.DialogInsertTextBinding

class InsertTextDialog(val listener:OnInsertTextDialogClick): DialogFragment(R.layout.dialog_insert_text),AdapterColorPalette.OnNoteColorAdapterClick {


    lateinit var binding:DialogInsertTextBinding
    lateinit var adapterColorPalette: AdapterColorPalette

    var textColor=Color.BLACK
    var textSize=18F

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= DialogInsertTextBinding.bind(view)
        initColorPalette()

        binding.btInsertText.setOnClickListener {
            val text=binding.etInsertText.text.toString()
            textSize=binding.sliderTextSize.value
            listener.onInsertDialogClick(text,textSize,textColor)
            dialog?.dismiss()
        }


    }

    private fun initColorPalette() {
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
        binding.rvDialogInsertTextColorPicker.apply {
            adapter = adapterColorPalette
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun onClick(colorRes: Int) {
        textColor=colorRes
        binding.tvTextColor.setTextColor(colorRes)
    }



    interface OnInsertTextDialogClick{
        fun onInsertDialogClick(text:String,textSize:Float,textColor:Int)
    }
}