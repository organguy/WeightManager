package kr.co.weightmanager.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kr.co.weightmanager.R
import kr.co.weightmanager.databinding.DialogWeightBinding
import kr.co.weightmanager.interfaces.InsertGoalListener

class WeightDialog : DialogFragment() {

    var mListener: InsertGoalListener? = null

    lateinit var binding : DialogWeightBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if(dialog != null){
            val w: Window? = dialog!!.window
            w!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            w.requestFeature(Window.FEATURE_NO_TITLE)
            w.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.setCancelable(false)
        }

        binding = DialogWeightBinding.inflate(inflater, container, false)

        binding.btOk.setOnClickListener {
            insertWeight()
        }

        binding.btCancel.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    fun setOnInsertWeightListener(listener: InsertGoalListener){
        mListener = listener
    }

    private fun insertWeight(){

        if(!TextUtils.isEmpty(binding.etWeight.text)){
            if(mListener != null){
                val weight = binding.etWeight.text.toString()
                mListener!!.onResult(weight)
                dismiss()
            }
        }else{
            Toast.makeText(context, R.string.msg_write_weight, Toast.LENGTH_SHORT).show()
        }
    }
}