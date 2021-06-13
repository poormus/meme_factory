package muss.stein.memefactory.ui.dialog

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import muss.stein.memefactory.R
import muss.stein.memefactory.databinding.DialogMemeCreateOptionsBinding

class DialogMemeCreateOptions:DialogFragment(R.layout.dialog_meme_create_options) {

    lateinit var binding:DialogMemeCreateOptionsBinding

    fun getInstance(resource:Int):DialogMemeCreateOptions{
        val dialog=DialogMemeCreateOptions()
        val bundle=Bundle()
        bundle.putInt("resource",resource)
        dialog.arguments=bundle
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= DialogMemeCreateOptionsBinding.bind(view)
        val resource=arguments?.getInt("resource")

        binding.ivCreateMemeByAddinText.setOnClickListener {
            dialog?.dismiss()
            findNavController().navigate(
                DialogMemeCreateOptionsDirections.actionMemeTemplateFragmentToMemeCreateByTextFragment(
                    resource!!,null
                )
            )
        }
        binding.ivCreateMemeByDrawing.setOnClickListener {
            dialog?.dismiss()
            findNavController().navigate(
                DialogMemeCreateOptionsDirections.actionMemeTemplateFragmentToMemeCreateFragment(
                    resource!!
                )
            )



        }
    }
}