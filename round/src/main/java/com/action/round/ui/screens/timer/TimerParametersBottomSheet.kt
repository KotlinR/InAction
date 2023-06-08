package com.action.round.ui.screens.timer

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.action.round.R
import com.action.round.data.models.TimerParameters
import com.action.round.utills.hideKeyboard
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TimerParametersBottomSheet(
    private val timerParameters: TimerParameters,
    private val totalRounds: Int?,
    private val onResetTimerParameters: (TimerParameters) -> Unit,
    private val onRefreshTotalRounds: (Int) -> Unit,
) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "TimerParametersBottomSheet"
    }

    private var tvRoundMin: EditText? = null
    private var tvRoundSec: EditText? = null
    private var tvRelaxMin: EditText? = null
    private var tvRelaxSec: EditText? = null
    private var tvTotalRoundsField: EditText? = null

    private val resetTimersParameters by lazy {
        TimerParameters(
            countdown = timerParameters.countdown,
            round = timerParameters.round,
            relax = timerParameters.relax,
            preStart = timerParameters.preStart,
            preStop = timerParameters.preStop,
            totalRounds = timerParameters.totalRounds
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBS = inflater.inflate(R.layout.bottom_sheet_timer, container, false)

        val originalWhite = viewBS.findViewById<TextView>(R.id.tvCountdown).textColors
        val btnApply = viewBS.findViewById<Button>(R.id.btnApply)
        //Countdown
        val tvCountdownOff = viewBS.findViewById<TextView>(R.id.tvCountdown_0)
        val tvCountdown15 = viewBS.findViewById<TextView>(R.id.tvCountdown_15)
        val tvCountdown30 = viewBS.findViewById<TextView>(R.id.tvCountdown_30)
        val tvCountdown60 = viewBS.findViewById<TextView>(R.id.tvCountdown_60)
        //Round
        val timeRound = setTime(timerParameters.round)
        tvRoundMin = viewBS.createdEditTextView(R.id.tvRoundMin, timeRound.first)
        tvRoundSec = viewBS.createdEditTextView(R.id.tvRoundSec, timeRound.second)
        //Relax
        val timeRelax = setTime(timerParameters.relax)
        tvRelaxMin = viewBS.createdEditTextView(R.id.tvRelaxMin, timeRelax.first)
        tvRelaxSec = viewBS.createdEditTextView(R.id.tvRelaxSec, timeRelax.second)
        //PreStart
        val tvPreStartOff = viewBS.findViewById<TextView>(R.id.tvPreStart_0)
        val tvPreStart10 = viewBS.findViewById<TextView>(R.id.tvPreStart_10)
        val tvPreStart20 = viewBS.findViewById<TextView>(R.id.tvPreStart_20)
        val tvPreStart30 = viewBS.findViewById<TextView>(R.id.tvPreStart_30)
        //PreStop
        val tvPreStopOff = viewBS.findViewById<TextView>(R.id.tvPreStop_0)
        val tvPreStop10 = viewBS.findViewById<TextView>(R.id.tvPreStop_10)
        val tvPreStop20 = viewBS.findViewById<TextView>(R.id.tvPreStop_20)
        val tvPreStop30 = viewBS.findViewById<TextView>(R.id.tvPreStop_30)
        //TotalRounds
        val getTotalRounds = totalRounds?.toString() ?: timerParameters.totalRounds.toString()
        tvTotalRoundsField = viewBS.createdEditTextView(R.id.tvTotalRoundsField, getTotalRounds)
        if (totalRounds != null) tvTotalRoundsField?.isEnabled = false
        //COUNTDOWN
        when (timerParameters.countdown) {
            0 -> tvCountdownOff.setTextColor(Color.GREEN)
            15 -> tvCountdown15.setTextColor(Color.GREEN)
            30 -> tvCountdown30.setTextColor(Color.GREEN)
            60 -> tvCountdown60.setTextColor(Color.GREEN)
        }
        tvCountdownOff?.setOnClickListener {
            resetTimersParameters.countdown = 0
            tvCountdownOff.setTextColor(Color.GREEN)
            tvCountdown15.setTextColor(originalWhite)
            tvCountdown30.setTextColor(originalWhite)
            tvCountdown60.setTextColor(originalWhite)
        }
        tvCountdown15?.setOnClickListener {
            resetTimersParameters.countdown = 15
            tvCountdownOff.setTextColor(originalWhite)
            tvCountdown15.setTextColor(Color.GREEN)
            tvCountdown30.setTextColor(originalWhite)
            tvCountdown60.setTextColor(originalWhite)
        }
        tvCountdown30?.setOnClickListener {
            resetTimersParameters.countdown = 30
            tvCountdownOff.setTextColor(originalWhite)
            tvCountdown15.setTextColor(originalWhite)
            tvCountdown30.setTextColor(Color.GREEN)
            tvCountdown60.setTextColor(originalWhite)
        }
        tvCountdown60?.setOnClickListener {
            resetTimersParameters.countdown = 60
            tvCountdownOff.setTextColor(originalWhite)
            tvCountdown15.setTextColor(originalWhite)
            tvCountdown30.setTextColor(originalWhite)
            tvCountdown60.setTextColor(Color.GREEN)
        }
        //Pre START
        when (timerParameters.preStart) {
            0 -> tvPreStartOff.setTextColor(Color.GREEN)
            10 -> tvPreStart10.setTextColor(Color.GREEN)
            20 -> tvPreStart20.setTextColor(Color.GREEN)
            30 -> tvPreStart30.setTextColor(Color.GREEN)
        }
        tvPreStartOff.setOnClickListener {
            resetTimersParameters.preStart = 0
            tvPreStartOff.setTextColor(Color.GREEN)
            tvPreStart10.setTextColor(originalWhite)
            tvPreStart20.setTextColor(originalWhite)
            tvPreStart30.setTextColor(originalWhite)
        }
        tvPreStart10.setOnClickListener {
            resetTimersParameters.preStart = 10
            tvPreStartOff.setTextColor(originalWhite)
            tvPreStart10.setTextColor(Color.GREEN)
            tvPreStart20.setTextColor(originalWhite)
            tvPreStart30.setTextColor(originalWhite)
        }
        tvPreStart20.setOnClickListener {
            resetTimersParameters.preStart = 20
            tvPreStartOff.setTextColor(originalWhite)
            tvPreStart10.setTextColor(originalWhite)
            tvPreStart20.setTextColor(Color.GREEN)
            tvPreStart30.setTextColor(originalWhite)
        }
        tvPreStart30.setOnClickListener {
            resetTimersParameters.preStart = 30
            tvPreStartOff.setTextColor(originalWhite)
            tvPreStart10.setTextColor(originalWhite)
            tvPreStart20.setTextColor(originalWhite)
            tvPreStart30.setTextColor(Color.GREEN)
        }
        //Pre STOP
        when (timerParameters.preStop) {
            0 -> tvPreStopOff.setTextColor(Color.GREEN)
            10 -> tvPreStop10.setTextColor(Color.GREEN)
            20 -> tvPreStop20.setTextColor(Color.GREEN)
            30 -> tvPreStop30.setTextColor(Color.GREEN)
        }
        tvPreStopOff.setOnClickListener {
            resetTimersParameters.preStop = 0
            tvPreStopOff.setTextColor(Color.GREEN)
            tvPreStop10.setTextColor(originalWhite)
            tvPreStop20.setTextColor(originalWhite)
            tvPreStop30.setTextColor(originalWhite)
        }
        tvPreStop10.setOnClickListener {
            resetTimersParameters.preStop = 10
            tvPreStopOff.setTextColor(originalWhite)
            tvPreStop10.setTextColor(Color.GREEN)
            tvPreStop20.setTextColor(originalWhite)
            tvPreStop30.setTextColor(originalWhite)
        }
        tvPreStop20.setOnClickListener {
            resetTimersParameters.preStop = 20
            tvPreStopOff.setTextColor(originalWhite)
            tvPreStop10.setTextColor(originalWhite)
            tvPreStop20.setTextColor(Color.GREEN)
            tvPreStop30.setTextColor(originalWhite)
        }
        tvPreStop30.setOnClickListener {
            resetTimersParameters.preStop = 30
            tvPreStopOff.setTextColor(originalWhite)
            tvPreStop10.setTextColor(originalWhite)
            tvPreStop20.setTextColor(originalWhite)
            tvPreStop30.setTextColor(Color.GREEN)
        }
        //Click button Apply
        btnApply.setOnClickListener {
            onResetTimerParameters(resetTimersParameters)
            onRefreshTotalRounds(resetTimersParameters.totalRounds)
            dismiss()
        }
        //Click this bottom sheet view
        viewBS.setOnClickListener { view ->
            view.hideKeyboard()
        }
        return viewBS
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun setTime(totalTime: Int): Pair<String, String> {
        val counterMin: Int = totalTime / 60
        val counterSec: Int = totalTime - (counterMin * 60)

        val min = if (counterMin < 10) "0$counterMin" else "$counterMin"
        val sec = if (counterSec < 10) "0$counterSec" else "$counterSec"
        return Pair(min, sec)
    }

    private fun convertToSeconds(tvMin: EditText?, tvSec: EditText?): Int {
        val min: String = if (tvMin?.text!!.isEmpty()) "0" else tvMin.text.toString()
        val sec: String =
            if (tvSec?.text!!.isEmpty() || tvSec.text.toString().toInt() >= 60) "0" else tvSec.text.toString()

        return min.toInt() * 60 + sec.toInt()
    }

    private fun convertToRounds(tvTotalRounds: EditText?): Int {
        return if (tvTotalRounds?.text!!.isEmpty()) timerParameters.totalRounds else tvTotalRounds.text.toString()
            .toInt()
    }

    private fun View.createdEditTextView(idView: Int, text: String): EditText {
        return this.findViewById<EditText?>(idView).apply {
            setText(text)
            setSelection(this.text.length)
            doAfterTextChanged {
                if (this == tvRoundMin || this == tvRoundSec) {
                    val timeRound = convertToSeconds(tvRoundMin, tvRoundSec)
                    resetTimersParameters.round = if (timeRound < 30) 30 else timeRound
                }
                if (this == tvRelaxMin || this == tvRelaxSec) {
                    val timeRelax = convertToSeconds(tvRelaxMin, tvRelaxSec)
                    resetTimersParameters.relax = if (timeRelax < 15) 15 else timeRelax
                }
                if (this == tvTotalRoundsField && totalRounds == null) {
                    resetTimersParameters.totalRounds = convertToRounds(tvTotalRoundsField)
                }

            }
        }
    }

    private fun refresh() {
        tvRoundMin?.setText(setTime(timerParameters.round).first)
        tvRoundSec?.setText(setTime(timerParameters.round).second)
        tvRelaxMin?.setText(setTime(timerParameters.relax).first)
        tvRelaxSec?.setText(setTime(timerParameters.relax).second)
        if (totalRounds == null) tvTotalRoundsField?.setText(timerParameters.totalRounds.toString())
    }
}