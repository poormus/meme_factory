package muss.stein.memefactory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import muss.stein.memefactory.databinding.AdapterTemplateBinding

class TemplateAdapter(
    private val templates: List<Int>,
    private val listener: OnTemplateAdapterClick
) :
    RecyclerView.Adapter<TemplateAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: AdapterTemplateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(template: Int) {
            binding.ivTemplate.setBackgroundResource(template)
        }

    }

    interface OnTemplateAdapterClick {
        fun onAdapterClick(resource: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterTemplateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val template = templates[position]
        holder.bind(template)
        holder.itemView.setOnClickListener {
            listener.onAdapterClick(template)
        }
    }

    override fun getItemCount(): Int {
        return templates.size
    }
}