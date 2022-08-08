package kr.co.weightmanager


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import io.realm.RealmList
import kr.co.weightmanager.databinding.ActivityMainBinding
import kr.co.weightmanager.dialog.AlarmDialog
import kr.co.weightmanager.dialog.GoalDialog
import kr.co.weightmanager.dialog.LogoutDialog
import kr.co.weightmanager.interfaces.InsertGoalListener
import kr.co.weightmanager.interfaces.OnLogoutListener
import kr.co.weightmanager.interfaces.SetAlarmListener
import kr.co.weightmanager.maanger.PropertyManager
import kr.co.weightmanager.maanger.RealmManager
import kr.co.weightmanager.realm.RmWeightData
import kr.co.weightmanager.util.VersionCheck
import kotlin.math.min


class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    var  weightList = RealmList<RmWeightData>()
    private lateinit var todayWeightData: RmWeightData
    private var dailyDiff = 0.0

    lateinit var menuItemGoal: MenuItem
    lateinit var menuItemAlarm: MenuItem
    lateinit var menuItemVersion: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
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
        binding.tvDailyWeight.text = "${todayWeightData.weight}kg"
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
        initNavItem()
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
                .load(authUser.photoUrl)
                .into(ivNavProfile)
        }
        tvNavProfile.text = authUser.displayName
        tvNavInfo.text = authUser.email
    }

    private fun initNavItem(){

        val navView = binding.nvNavigation
        val navMenu = navView.menu

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_goal -> showGoalSettingDialog()

                R.id.item_alarm -> showAlarmSettingDialog()

                R.id.item_logout -> showLogoutDialog()

            }

            true
        }

        menuItemGoal = navMenu.findItem(R.id.item_goal)
        menuItemAlarm = navMenu.findItem(R.id.item_alarm)
        menuItemVersion = navMenu.findItem(R.id.item_version)

        initNavItemGoal()
        initNavItemAlarm()
        iniNavItemVersion()
    }

    private fun initNavItemGoal(){
        var goal = PropertyManager.getGoal()

        if(!TextUtils.isEmpty(goal)){
            menuItemGoal.title = "${getString(R.string.menu_item_goal)}     -      ${goal}kg"
        }else{
            menuItemGoal.title = getString(R.string.menu_item_goal)
        }
    }

    private fun initNavItemAlarm(){
        var alarmTime = PropertyManager.getAlarm()

        if(!TextUtils.isEmpty(alarmTime)){

            var hour = alarmTime!!.split(",")[0]
            var min = alarmTime.split(",")[1]

            menuItemAlarm.title = "${getString(R.string.menu_item_alram)}     -     $hour 시 $min 분"
        }else{
            menuItemAlarm.title = getString(R.string.menu_item_alram)
        }
    }

    private fun iniNavItemVersion(){
        menuItemVersion.title = "${getString(R.string.menu_item_version)}     -     ${BuildConfig.VERSION_NAME}"

        var badgeVersion = menuItemVersion.actionView as TextView
        badgeVersion.gravity = Gravity.CENTER_VERTICAL
        badgeVersion.setTypeface(null, Typeface.BOLD)
        badgeVersion.setTextColor(getColor(android.R.color.holo_red_dark))
        badgeVersion.setText(R.string.new_version)

        val currentVersion = VersionCheck(BuildConfig.VERSION_NAME)
        val marketVersion = VersionCheck(PropertyManager.getVersion()!!)
        val compare: Int = marketVersion.compareTo(currentVersion)

        if (compare == 1) { // marketVersion > currentVersion
            badgeVersion.visibility = View.VISIBLE
        } else { // currentVersion = marketVersion
            badgeVersion.visibility = View.GONE
        }
    }

    private fun initChart(){

        var goalWeight = PropertyManager.getGoal()
        var maxWeight = RealmManager.getMaxWeight()
        var minWeight = RealmManager.getMinWeight()

        var axisMinWeight = if(!TextUtils.isEmpty(goalWeight)){
            min(goalWeight!!.toDouble(), minWeight.toDouble()).toInt()
        }else{
            minWeight.toDouble().toInt()
        }

        axisMinWeight = (axisMinWeight / 5) * 5

        var axisMaxWeight = maxWeight.toDouble().toInt()
        if(axisMaxWeight % 5 != 0){
            axisMaxWeight = ((axisMaxWeight / 5) + 1) * 5
        }

        binding.bcChart.run {
            axisRight.isEnabled = false

            axisLeft.run {
                setDrawLabels(true)
                setDrawGridLines(false)
                axisMaximum = axisMaxWeight.toFloat()
                axisMinimum = axisMinWeight.toFloat()
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

    private fun showGoalSettingDialog(){
        val goalDialog = GoalDialog()
        goalDialog.isCancelable = false
        goalDialog.setOnGoalListener(object : InsertGoalListener {
            override fun onResult(weight: String) {
                updateGoal(weight)
            }
        })
        goalDialog.show(supportFragmentManager, "dialog")
    }

    fun updateGoal(weight: String){
        PropertyManager.setGoal(weight)
        var goal = PropertyManager.getGoal()

        if(!TextUtils.isEmpty(goal)){
            menuItemGoal.title = "${getString(R.string.menu_item_goal)} - $goal"
        }else{
            menuItemGoal.title = getString(R.string.menu_item_goal)
        }
    }

    private fun showAlarmSettingDialog(){
        val alarmDialog = AlarmDialog()
        alarmDialog.isCancelable = false
        alarmDialog.setOnAlarmListener(object : SetAlarmListener {
            override fun onResult(hour: Int, min: Int) {
                updateAlarm(hour, min)
            }
        })
        alarmDialog.show(supportFragmentManager, "dialog")
    }

    fun updateAlarm(hour: Int, min: Int){
        var alarmTime = "$hour,$min"
        PropertyManager.setAlarm(alarmTime)
    }

    private fun showLogoutDialog(){
        val logoutDialog = LogoutDialog()
        logoutDialog.isCancelable = false
        logoutDialog.setOnLogoutListener(object : OnLogoutListener{
            override fun onResult() {
                doLogout()
            }
        })
        logoutDialog.show(supportFragmentManager, "dialog")
    }

    fun doLogout(){

        FirebaseAuth.getInstance().signOut()

        val opt = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val client = GoogleSignIn.getClient(this, opt)
        client.signOut().addOnCompleteListener {
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, R.string.msg_re_logout, Toast.LENGTH_SHORT).show()
        }
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

            var dateTime = weightList[value.toInt()]!!.dateTime
            var date = dateTime!!.substring(5, dateTime.length)

            return date
        }
    }
}
