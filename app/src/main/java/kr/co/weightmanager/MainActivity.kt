package kr.co.weightmanager


import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
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
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
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
import kr.co.weightmanager.util.OgLog
import kr.co.weightmanager.util.VersionCheck
import java.util.*
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.round


class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    var  weightList = RealmList<RmWeightData>()
    private lateinit var todayWeightData: RmWeightData
    private var yesterdayWeightData: RmWeightData? = null
    private var dailyDiff = 0.0
    private var weeklyWeight = 0.0
    private var weeklyDiff = 0.0
    private var monthlyWeight = 0.0
    private var monthlyDiff = 0.0
    private var remainWeight = 0.0

    lateinit var menuItemGoal: MenuItem
    lateinit var menuItemAlarm: MenuItem
    lateinit var menuItemVersion: MenuItem

    internal var alarmManager: AlarmManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        initView()
        initNavi()
        initChart()
    }

    private fun initData(){
        weightList.addAll(RealmManager.getWeightResults())
        todayWeightData = RealmManager.getTodayWeightData()
        yesterdayWeightData = RealmManager.getYesterdayWeightData()

        if(yesterdayWeightData == null){
            yesterdayWeightData = todayWeightData
        }

        dailyDiff = RealmManager.getDailyDiff()
        weeklyWeight = RealmManager.getWeekAvgWeight()
        weeklyDiff = todayWeightData.weight - weeklyWeight
        weeklyDiff = round(weeklyDiff * 10) / 10
        monthlyWeight = RealmManager.getMonthAvgWeight()
        monthlyDiff = todayWeightData.weight - monthlyWeight
        monthlyDiff = round(monthlyDiff * 10) / 10

        if(!TextUtils.isEmpty(PropertyManager.getGoal())){
            remainWeight = todayWeightData.weight - PropertyManager.getGoal()!!.toDouble();
        }else{
            showGoalSettingDialog()
        }

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager?
    }

    @SuppressLint("SetTextI18n")
    fun initView(){

        OgLog.d("initView start")

        binding.tvTodayWeight.text = "${String.format("%.1f", todayWeightData.weight)}kg"

        binding.tvDailyWeight.text = "${String.format("%.1f", yesterdayWeightData!!.weight)}kg"
        binding.tvDailyDiff.text = String.format("%.1f", dailyDiff)

        if(dailyDiff < 0.0){
            binding.ivDailyDiff.setImageResource(R.drawable.ic_diff_arrow_down)
        }else if(dailyDiff == 0.0){
            binding.ivDailyDiff.setImageResource(R.drawable.ic_diff_arrow_equal)
        }else{
            binding.ivDailyDiff.setImageResource(R.drawable.ic_diff_arrow_up)
        }

        binding.tvWeeklyWeight.text = "${String.format("%.1f", weeklyWeight)}kg"
        binding.tvWeeklyDiff.text = String.format("%.1f", weeklyDiff)

        if(weeklyDiff < 0.0){
            binding.ivWeeklyDiff.setImageResource(R.drawable.ic_diff_arrow_down)
        }else if(weeklyDiff == 0.0){
            binding.ivWeeklyDiff.setImageResource(R.drawable.ic_diff_arrow_equal)
        }else{
            binding.ivWeeklyDiff.setImageResource(R.drawable.ic_diff_arrow_up)
        }

        binding.tvMonthlyWeight.text = "${String.format("%.1f", monthlyWeight)}kg"
        binding.tvMonthlyDiff.text = String.format("%.1f", monthlyDiff)

        if(monthlyDiff < 0.0){
            binding.ivMonthlyDiff.setImageResource(R.drawable.ic_diff_arrow_down)
        }else if(monthlyDiff == 0.0){
            binding.ivMonthlyDiff.setImageResource(R.drawable.ic_diff_arrow_equal)
        }else{
            binding.ivMonthlyDiff.setImageResource(R.drawable.ic_diff_arrow_up)
        }

        if(!TextUtils.isEmpty(PropertyManager.getGoal())){
            binding.tvRemain.text = "Remain : ${String.format("%.1f", remainWeight)}kg"
        }else{
            binding.tvRemain.text = "Remain : ??.?kg"
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

        //val ivNavProfile = headerView.findViewById<ImageView>(R.id.iv_nav_profile)
        val tvNavProfile = headerView.findViewById<TextView>(R.id.tv_nav_profile)
        val tvNavInfo = headerView.findViewById<TextView>(R.id.tv_nav_info)
        val authUser = FirebaseAuth.getInstance().currentUser

        /*if (authUser!!.photoUrl != null) {
            Glide.with(this)
                .load(authUser.photoUrl)
                .into(ivNavProfile)
        }*/
        tvNavProfile.text = authUser!!.displayName
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
        val goal = PropertyManager.getGoal()

        if(!TextUtils.isEmpty(goal)){
            menuItemGoal.title = "${getString(R.string.menu_item_goal)} - ${goal}kg"
        }else{
            menuItemGoal.title = getString(R.string.menu_item_goal)
        }
    }

    private fun initNavItemAlarm(){
        val alarmTime = PropertyManager.getAlarm()

        if(!TextUtils.isEmpty(alarmTime)){

            val hour = alarmTime!!.split(",")[0]
            val min = alarmTime.split(",")[1]

            menuItemAlarm.title = "${getString(R.string.menu_item_alram)} - $hour 시 $min 분"
        }else{
            menuItemAlarm.title = getString(R.string.menu_item_alram)
        }
    }

    private fun iniNavItemVersion(){
        menuItemVersion.title = "${getString(R.string.menu_item_version)} - ${BuildConfig.VERSION_NAME}"

        val badgeVersion = menuItemVersion.actionView as TextView
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

        val goalWeight = PropertyManager.getGoal()
        val maxWeight = RealmManager.getMaxWeight()
        val minWeight = RealmManager.getMinWeight()

        var axisMinWeight = if(!TextUtils.isEmpty(goalWeight)){
            min(goalWeight!!.toDouble(), minWeight.toDouble()).toInt()
        }else{
            minWeight.toDouble().toInt()
        }

        axisMinWeight = (axisMinWeight / 5) * 5

        var axisMaxWeight = ceil(maxWeight.toDouble()).toInt()
        if(axisMaxWeight % 5 != 0){
            axisMaxWeight = ((axisMaxWeight / 5) + 1) * 5
        }

        binding.lcChart.run {
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


        /*val dataVals = ArrayList<BarEntry>()
        val colors = IntArray(weightList.size)

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

        val barDataSet = BarDataSet(dataVals, "전체 현황")
        barDataSet.setColors(colors, this)

        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(barDataSet)

        val data = BarData(dataSets)
        binding.bcChart.data = data
        binding.bcChart.invalidate()*/

        var entryList = ArrayList<Entry>()
        val lineData = LineData()

        for(i: Int in 0 until weightList.size){
            entryList.add(Entry(i.toFloat(), weightList[i]!!.weight.toFloat()))
        }

        val lineDataSet = LineDataSet(entryList, "전체 현황")
        lineDataSet.run {
            lineWidth = 3.0f
            circleRadius = 6.0f
            setDrawValues(false)
            setDrawCircleHole(true)
            setDrawCircles(true)
            setDrawHorizontalHighlightIndicator(false)
            setDrawHighlightIndicators(false)
            color = Color.rgb(255, 155, 155)
            setCircleColor(Color.rgb(255, 155, 155))
        }

        lineData.addDataSet(lineDataSet)

        binding.lcChart.data = lineData
        binding.lcChart.invalidate()

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
        val goal = PropertyManager.getGoal()

        if(!TextUtils.isEmpty(goal)){
            menuItemGoal.title = "${getString(R.string.menu_item_goal)} - $goal"
            remainWeight = todayWeightData.weight - PropertyManager.getGoal()!!.toDouble();
            binding.tvRemain.text = "Remain : ${String.format("%.1f", remainWeight)}kg"
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
        val alarmTime = "$hour,$min"
        PropertyManager.setAlarm(alarmTime)

        val receiverIntent = Intent(applicationContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, receiverIntent, 0)

        alarmManager?.cancel(pendingIntent)

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, min)
            set(Calendar.SECOND, 0)
        }

        if (calendar.time < Date()) { //설정한 시간에 따라, 알람이 설정이 안될 수 있으므로 달 첫번째 부터의 시간을 설정
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
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

            val dateTime = weightList[value.toInt()]!!.pk
            val date = dateTime!!.substring(5, dateTime.length)

            return date
        }
    }
}
