package dev.tran.nam.chart.chartsong.view.main.chart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import dev.tran.nam.chart.chartsong.BR
import dev.tran.nam.chart.chartsong.R
import dev.tran.nam.chart.chartsong.databinding.AdapterWeekChartBinding
import nam.tran.data.executor.AppExecutors
import nam.tran.data.model.WeekChart
import tran.nam.common.DataBoundListAdapter
import tran.nam.common.DataBoundViewHolder

class WeekChartAdapter constructor(appExecutors: AppExecutors, private val dataBindingComponent: DataBindingComponent
                                    ,var position : Int = 0, val week: (WeekChart, Int) -> Unit)
    : DataBoundListAdapter<WeekChart,AdapterWeekChartBinding>(appExecutors,object : DiffUtil.ItemCallback<WeekChart>(){
    override fun areItemsTheSame(oldItem: WeekChart, newItem: WeekChart): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: WeekChart, newItem: WeekChart): Boolean {
        return oldItem.name == newItem.name
    }
}) {

    override fun createBinding(parent: ViewGroup): AdapterWeekChartBinding {
        val binding : AdapterWeekChartBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.adapter_week_chart,
            parent,
            false,
            dataBindingComponent
        )
        return binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<AdapterWeekChartBinding> {
        val hoder = super.onCreateViewHolder(parent, viewType)
        val binding = hoder.binding
        binding.root.setOnClickListener {
            binding.week?.run {
                val positionChoose = hoder.adapterPosition
                if (position != positionChoose){
                    getItem(position).isChoose = false
                    position = positionChoose
                    getItem(position).isChoose = true
                    week.invoke(this,position)
                }
            }
        }
        return hoder
    }

    override fun bind(binding: AdapterWeekChartBinding, item: WeekChart) {
        binding.week = item
    }

    fun getPositionSelect() : Int{
        return position
    }
}