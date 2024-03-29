package com.sport.abox.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.sport.abox.R

class ConfirmationDialog : DialogFragment() {

    companion object {
        private const val KEY_MESSAGE = "message"
        private const val KEY_YES = "yesBtn"
        private const val KEY_NO = "noBtn"
        private const val KEY_CANCEL = "cancel"
        private const val KEY_TITLE = "title"

        fun newInstance(
            message: String,
            positiveBtn: String?,
            negativeBtn: String?,
            neutralBtn: String?,
            title: String,
        ): ConfirmationDialog {
            val dialog = ConfirmationDialog()
            dialog.arguments = bundleOf(
                KEY_MESSAGE to message,
                KEY_YES to positiveBtn,
                KEY_NO to negativeBtn,
                KEY_CANCEL to neutralBtn,
                KEY_TITLE to title,
            )
            return dialog
        }
    }

    private val interactionListener: InteractionListener?
        get() = activity as? InteractionListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.confirmation_dialog, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireDialog().setTitle(arguments?.getString(KEY_TITLE))
        view.findViewById<TextView>(R.id.tvConfirmation).text = arguments?.getString(KEY_MESSAGE)

        arguments?.let { arguments ->
            view.apply {
                setUpButton(
                    id = R.id.posButton,
                    key = KEY_YES,
                    arguments = arguments,
                    onClick = { interactionListener?.onDialogPositiveClick(this@ConfirmationDialog) },
                )
                setUpButton(
                    id = R.id.negButton,
                    key = KEY_NO,
                    arguments = arguments,
                    onClick = { interactionListener?.onDialogNegativeClick(this@ConfirmationDialog) },
                )
                setUpButton(
                    id = R.id.neutralButton,
                    key = KEY_CANCEL,
                    arguments = arguments,
                    onClick = { interactionListener?.onDialogNeutralClick(this@ConfirmationDialog) },
                )
            }
        }
    }

    interface InteractionListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
        fun onDialogNeutralClick(dialog: DialogFragment)
    }
}