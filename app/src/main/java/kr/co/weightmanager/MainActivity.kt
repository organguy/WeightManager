package kr.co.weightmanager

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.rpc.context.AttributeContext
import io.realm.RealmList
import kr.co.weightmanager.databinding.ActivityMainBinding
import kr.co.weightmanager.maanger.RealmManager
import kr.co.weightmanager.realm.RmWeightData
import kr.co.weightmanager.util.OgLog

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    var  weightList = RealmList<RmWeightData>()
    private lateinit var todayWeightData: RmWeightData
    private var dailyDiff = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)

        initData()
        initView()
        initNavi()
        initChart()
    }

    fun initData(){
        weightList.addAll(RealmManager.getWeightResults())
        todayWeightData = RealmManager.getTodayWeightData()
        dailyDiff = RealmManager.getDailyDiff()
    }

    @SuppressLint("SetTextI18n")
    fun initView(){
        binding.tvDailyWeight.text = "${todayWeightData!!.weight}kg"
        binding.tvDailyDiff.text = String.format("%.1f", dailyDiff)

        if(dailyDiff < 0.0){
            binding.ivDailyDiff.setImageResource(R.drawable.ic_diff_arrow_down)
        }else if(dailyDiff == 0.0){
            binding.ivDailyDiff.setImageResource(R.drawable.ic_diff_arrow_equal)
        }else{
            binding.ivDailyDiff.setImageResource(R.drawable.ic_diff_arrow_up)
        }
    }

    private fun initNavi(){
        setSupportActionBar(binding.tbToolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24)

        initProfile()
    }

    private fun initProfile(){

        val navView = binding.nvNavigation
        val headerView = navView.getHeaderView(0)

        val ivNavProfile = headerView.findViewById<ImageView>(R.id.iv_nav_profile)
        val tvNavProfile = headerView.findViewById<TextView>(R.id.tv_nav_profile)
        val tvNavInfo = headerView.findViewById<TextView>(R.id.tv_nav_info)
        var authUser = FirebaseAuth.getInstance().currentUser

        if (authUser!!.photoUrl != null) {
            Glide.with(this)
                .load(authUser!!.photoUrl)
                .into(ivNavProfile)
        }
        tvNavProfile.text = authUser.displayName
        tvNavInfo.text = authUser.email
    }

    private fun initChart(){

        binding.bcChart.run {
            axisRight.isEnabled = false

            axisLeft.run {
                setDrawLabels(true)
                setDrawGridLines(false)
                axisMaximum = 90.0f
                axisMinimum = 75.0f
            }

            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                valueFormatter = MyXAxisFormatter()
            }

            description.isEnabled = false
            legend.isEnabled = false
        }


        var dataVals = ArrayList<BarEntry>()
        var colors = IntArray(weightList.size)

        var maxWeight = RealmManager.getMaxWeight()
        var minWeight = RealmManager.getMinWeight()

        OgLog.d("maxWeight : " + maxWeight!!.toDouble())
        OgLog.d("minWeight : " + minWeight!!.toDouble())

        for(i: Int in 0 until weightList.size){
            dataVals.add(BarEntry(i.toFloat(), weightList[i]!!.weight.toFloat()))

            when (weightList[i]!!.weight) {
                maxWeight -> {
                    colors[i] = R.color.chart_max
                }
                minWeight -> {
                    colors[i] = R.color.chart_min
                }
                else -> {
                    colors[i] = R.color.chart_normal
                }
            }
        }

        var barDataSet = BarDataSet(dataVals, "전체 현황")
        barDataSet.setColors(colors, this)

        var dataSets = ArrayList<IBarDataSet>()
        dataSets.add(barDataSet)

        var data = BarData(dataSets)
        //data.barWidth = 0.3f
        binding.bcChart.data = data
        binding.bcChart.invalidate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            android.R.id.home -> {
                binding.dlDrawer.openDrawer(GravityCompat.START)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

        if(binding.dlDrawer.isDrawerOpen(GravityCompat.START)){
            binding.dlDrawer.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }

    inner class MyXAxisFormatter : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {

            var dateTime = weightList[value.toInt()]!!.dateTime;
            var date = dateTime!!.substring(5, dateTime.length)

            return date
        }
    }
}