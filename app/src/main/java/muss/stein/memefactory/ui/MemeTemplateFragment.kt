package muss.stein.memefactory.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.theartofdev.edmodo.cropper.CropImage
import muss.stein.memefactory.R
import muss.stein.memefactory.adapter.TemplateAdapter
import muss.stein.memefactory.databinding.FragmentMemeTemplateBinding
import muss.stein.memefactory.ui.dialog.DialogMemeCreateOptions
import muss.stein.memefactory.ui.dialog.InsertTextDialog
import muss.stein.memefactory.util.Constants.TEMPLATE_LIST

class MemeTemplateFragment : Fragment(R.layout.fragment_meme_template),
    TemplateAdapter.OnTemplateAdapterClick {

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
    lateinit var binding: FragmentMemeTemplateBinding
    lateinit var templateAdapter: TemplateAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMemeTemplateBinding.bind(view)
        setRv()
        binding.fabComposeNewMeme.setOnClickListener {
            picImageLauncher.launch(null)
        }
        picImageLauncher = registerForActivityResult(picImageContract) {

            if(it==null){
                Toast.makeText(requireContext(), "no image selected", Toast.LENGTH_SHORT).show()
            }else{
                findNavController().navigate(MemeTemplateFragmentDirections.actionMemeTemplateFragmentToMemeCreateByTextFragment(
                    -1,it.toString()
                ))
            }

        }

    }

    private fun setRv() {

        templateAdapter = TemplateAdapter(TEMPLATE_LIST, this)
        binding.rvMemeTemplate.apply {
            adapter = templateAdapter
            addOnScrollListener(onScrollListener)
        }
    }

    override fun onAdapterClick(resource: Int) {
        DialogMemeCreateOptions().getInstance(resource)
            .show(childFragmentManager, "memeCreateOptions")
    }
    val onScrollListener=object :RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if(dy>10 && binding.fabComposeNewMeme.isExtended){
                binding.fabComposeNewMeme.shrink()
            }
            if(dy<-10 && !binding.fabComposeNewMeme.isExtended){
                binding.fabComposeNewMeme.extend()
            }
            if(!recyclerView.canScrollVertically(-1)){
                binding.fabComposeNewMeme.extend()
            }
        }
    }


}