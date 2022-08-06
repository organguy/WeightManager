package kr.co.weightmanager.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.DialogFragment
import kr.co.weightmanager.databinding.DialogAlarmBinding
import kr.co.weightmanager.databinding.DialogLogoutBinding
import kr.co.weightmanager.interfaces.OnLogoutListener
import kr.co.weightmanager.interfaces.SetAlarmListener
import kr.co.weightmanager.maanger.PropertyManager
import kr.co.weightmanager.maanger.PropertyManager.setAlarm

class LogoutDialog : DialogFragment() {

    var mListener: OnLogoutListener? = null

    lateinit var binding : DialogLogoutBinding

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

        binding = DialogLogoutBinding.inflate(inflater, container, false)

        binding.btOk.setOnClickListener {
            if(mListener != null){
                mListener!!.onResult()
            }
        }

        binding.btCancel.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    fun setOnLogoutListener(listener: OnLogoutListener){
        mListener = listener
    }
}