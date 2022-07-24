package kr.co.weightmanager

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import io.realm.RealmList
import kr.co.weightmanager.databinding.ActivityMainBinding
import kr.co.weightmanager.maanger.RealmManager
import kr.co.weightmanager.realm.RmWeightData

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    var  weightList = RealmList<RmWeightData>()

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
    }

    fun initView(){

    }

    fun initNavi(){
        setSupportActionBar(binding.tbToolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24)
    }

    fun initChart(){
        var dataVals = ArrayList<BarEntry>()

        for(i: Int in 0 until weightList.size){
            dataVals.add(BarEntry(i.toFloat(), weightList[i]!!.weight.toFloat()))
        }

        var barDataSet = BarDataSet(dataVals, "전체 현황")
        var dataSets = ArrayList<IBarDataSet>()
        dataSets.add(barDataSet)

        var data = BarData(dataSets)
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
}