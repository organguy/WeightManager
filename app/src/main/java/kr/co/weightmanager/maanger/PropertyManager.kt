package kr.co.weightmanager.maanger

import android.content.Context
import android.content.SharedPreferences
import kr.co.weightmanager.MyApplication

object PropertyManager {
    private const val PREF_NAME = "weight_maanger.xml"
    private var mPrefs: SharedPreferences = MyApplication.getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var mEditor: SharedPreferences.Editor = mPrefs.edit()


    /**
     * 설정 ---> 목표 체중
     */
    private const val GOAL = "GOAL"
    private var goal: String? = null

    fun getGoal(): String? {
        goal = mPrefs.getString(GOAL, "")
        return goal
    }

    fun setGoal(_goal: String?) {
        mEditor.putString(GOAL, _goal)
        mEditor.commit()
    }
}
