package muss.stein.memefactory.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import muss.stein.memefactory.data.InternalStoragePhoto
import muss.stein.memefactory.databinding.ItemPhotoBinding


class InternalStoragePhotoAdapter(
    private val listener:InternalStorageAdapterClick
) : ListAdapter<InternalStoragePhoto, InternalStoragePhotoAdapter.PhotoViewHolder>(Companion) {

    interface InternalStorageAdapterClick{
        fun onShareClick(fileName:String,bitmap:Bitmap)
        fun onDeleteClick(fileName: String)
    }

    inner class PhotoViewHolder(val binding: ItemPhotoBinding): RecyclerView.ViewHolder(binding.root)

    companion object : DiffUtil.ItemCallback<InternalStoragePhoto>() {
        override fun areItemsTheSame(oldItem: InternalStoragePhoto, newItem: InternalStoragePhoto): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: InternalStoragePhoto, newItem: InternalStoragePhoto): Boolean {
            return oldItem.name == newItem.name && oldItem.bmp.sameAs(newItem.bmp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = currentList[position]
        holder.binding.apply {
            ivPhoto.setImageBitmap(photo.bmp)

            val aspectRatio = photo.bmp.width.toFloat() / photo.bmp.height.toFloat()
            ConstraintSet().apply {

                setDimensionRatio(ivPhoto.id, aspectRatio.toString())

            }
            ivDrawShare.setOnClickListener {
                listener.onShareClick(photo.name,photo.bmp)
            }
            ivDrawDelete.setOnClickListener {
                listener.onDeleteClick(photo.name)
            }

        }
    }
}