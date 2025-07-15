package com.example.referralapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.referralapp.data.model.HospitalInfo
import com.example.referralapp.databinding.ItemHospitalBinding
import com.example.referralapp.ui.adapter.HospitalAdapter.HospitalViewHolder

class HospitalAdapter : ListAdapter<HospitalInfo, HospitalViewHolder>(DIFF_CALLBACK) {

    var onItemClick: ((HospitalInfo) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalViewHolder {
        val binding =
            ItemHospitalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HospitalViewHolder(binding)
    }


    override fun onBindViewHolder(holder: HospitalViewHolder, position: Int) {
        val item = getItem(position)        // ListAdapter에서 제공하는 getItem 사용
        holder.bind(item)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(item)
        }
    }

    inner class HospitalViewHolder(
        private val binding: ItemHospitalBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(hospital: HospitalInfo) {
            binding.tvHospitalName.text = hospital.hospitalInfoName
            binding.tvAddress.text = hospital.hospitalInfoAddress
            binding.tvPhone.text = hospital.hospitalInfoContactPhone
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HospitalInfo>() {
            override fun areItemsTheSame(oldItem: HospitalInfo, newItem: HospitalInfo): Boolean {
                return oldItem.hospitalInfoId == newItem.hospitalInfoId
            }

            override fun areContentsTheSame(oldItem: HospitalInfo, newItem: HospitalInfo): Boolean {
                return oldItem == newItem
            }
        }
    }
}
