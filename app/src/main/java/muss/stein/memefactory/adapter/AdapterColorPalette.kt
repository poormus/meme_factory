package muss.stein.memefactory.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import muss.stein.memefactory.R
import muss.stein.memefactory.databinding.ItemColorPaletteBinding


class AdapterColorPalette(
    private val colorResList:List<Int>,
    private val listener:OnNoteColorAdapterClick
):RecyclerView.Adapter<AdapterColorPalette.ViewHolder>() {

    interface OnNoteColorAdapterClick{
        fun onClick(colorRes:Int)
    }

    inner class ViewHolder(val binding: ItemColorPaletteBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(colorResource:Int){
            val drawable= ResourcesCompat.getDrawable(binding.root.resources, R.drawable.circle,null)
            drawable?.let {
                val wrappedDrawable = DrawableCompat.wrap(it)
                DrawableCompat.setTint(wrappedDrawable, colorResource)
                binding.viewAdapterNoteColor.background=wrappedDrawable
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=ItemColorPaletteBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val colorRes=colorResList[position]
        holder.bind(colorRes)
        holder.binding.root.setOnClickListener {

            listener.onClick(colorRes)

        }

    }

    override fun getItemCount(): Int {
        return colorResList.size
    }

    private fun animateIv(holder: ViewHolder) {
        val pulsate = ObjectAnimator.ofPropertyValuesHolder(
            holder.binding.viewAdapterNoteColor,
            PropertyValuesHolder.ofFloat("scaleX", 1.2f),
            PropertyValuesHolder.ofFloat("scaleY", 1.2f)
        )
        pulsate.apply {
            repeatCount = 3
            repeatMode = ObjectAnimator.REVERSE
        }
        pulsate.start()
    }
}