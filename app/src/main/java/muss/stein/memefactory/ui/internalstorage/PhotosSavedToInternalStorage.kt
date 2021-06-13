package muss.stein.memefactory.ui.internalstorage

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import muss.stein.memefactory.BuildConfig
import muss.stein.memefactory.R
import muss.stein.memefactory.adapter.InternalStoragePhotoAdapter
import muss.stein.memefactory.data.InternalStoragePhoto
import muss.stein.memefactory.databinding.FragmentInternalStoragePhotosBinding
import java.io.FileNotFoundException


class PhotosSavedToInternalStorage : Fragment(R.layout.fragment_internal_storage_photos),
    InternalStoragePhotoAdapter.InternalStorageAdapterClick {

    lateinit var internalStoragePhotoAdapter: InternalStoragePhotoAdapter
    lateinit var binding: FragmentInternalStoragePhotosBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInternalStoragePhotosBinding.bind(view)
        internalStoragePhotoAdapter = InternalStoragePhotoAdapter(this)
        setupInternalStorageRecyclerView()
        loadPhotosFromInternalStorageIntoRecyclerView()

    }


    private fun setupInternalStorageRecyclerView() = binding.rvInternalStoragePhotos.apply {
        adapter = internalStoragePhotoAdapter
        layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
    }

    private fun loadPhotosFromInternalStorageIntoRecyclerView() {
        lifecycleScope.launch {
            val photos = loadPhotosFromInternalStorage()
            internalStoragePhotoAdapter.submitList(photos)
        }
    }

    private fun deletePhotoFromInternalStorage(filename: String): Boolean {
        return try {
            requireActivity().deleteFile(filename)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun loadPhotosFromInternalStorage(): List<InternalStoragePhoto> {
        return withContext(Dispatchers.IO) {
            val files = requireActivity().filesDir.listFiles()
            files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
                val bytes = it.readBytes()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                InternalStoragePhoto(it.name, bmp)
            } ?: listOf()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onShareClick(fileName: String, bitmap: Bitmap) {
        val path: String = MediaStore.Images.Media.insertImage(
            requireActivity().contentResolver,
            bitmap,
            "Image I want to share",
            null
        )
        val uri = Uri.parse(path)
        try {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.type = "image/jpeg"
            requireContext().startActivity(Intent.createChooser(shareIntent, "Share"))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }


    }

    override fun onDeleteClick(fileName: String) {
        val isDeleted = deletePhotoFromInternalStorage(fileName)
        loadPhotosFromInternalStorageIntoRecyclerView()
        if (isDeleted) {
            Toast.makeText(requireContext(), "Photo deleted", Toast.LENGTH_SHORT).show()
        }
    }
}