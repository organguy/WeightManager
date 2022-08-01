package kr.co.weightmanager.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.DialogFragment
import kr.co.weightmanager.databinding.DialogAlarmBinding
import kr.co.weightmanager.interfaces.SetAlarmListener
import kr.co.weightmanager.maanger.PropertyManager

class AlarmDialog : DialogFragment() {

    var mListener: SetAlarmListener? = null

    lateinit var binding : DialogAlarmBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if(dialog != null){
            val w: Window? = dialog!!.window
            w!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            w.requestFeature(Window.FEATURE_NO_TITLE)
            w.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.setCancelable(false)
        }

        binding = DialogAlarmBinding.inflate(inflater, container, false)

        binding.btOk.setOnClickListener {
            setAlarm()
        }

        binding.btCancel.setOnClickListener {
            dismiss()
        }

        if(!TextUtils.isEmpty(PropertyManager.getAlarm())){

            var alarmTime = PropertyManager.getAlarm()
            var hour = alarmTime!!.split(",")[0]
            var min = alarmTime!!.split(",")[1]

            binding.tpAlarm.hour = hour.toInt()
            binding.tpAlarm.minute = min.toInt()
        }

        return binding.root
    }

    fun setOnAlarmListener(listener: SetAlarmListener){
        mListener = listener
    }

    fun setAlarm(){
        if(mListener != null){
            var hour = binding.tpAlarm.hour
            var min = binding.tpAlarm.minute
            mListener!!.onResult(hour, min)
            dismiss()
        }
    }
}