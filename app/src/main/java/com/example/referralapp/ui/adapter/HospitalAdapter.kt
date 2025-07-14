package com.example.referralapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.referralapp.data.model.HospitalInfo

class HospitalAdapter : RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder>() {

    private val items = mutableListOf<HospitalInfo>()

    fun submitList(newList: List<HospitalInfo>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalViewHolder {
        val binding = ItemHospitalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HospitalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HospitalViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class HospitalViewHolder(
        private val binding: ItemHospitalBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(hospital: HospitalInfo) {
            binding.tvHospitalName.text = hospital.hospitalInfoName
            binding.tvAddress.text = hospital.hospitalInfoAddress
            binding.tvPhone.text = hospital.hospitalInfoContactPhone
        }
    }
}
