package com.example.abox.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.abox.R

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

        createButton(
            id = R.id.posButton,
            key = KEY_YES,
            onClick = { interactionListener?.onDialogPositiveClick(this) },
        )

        createButton(
            id = R.id.negButton,
            key = KEY_NO,
            onClick = { interactionListener?.onDialogNegativeClick(this) },
        )

        createButton(
            id = R.id.neutralButton,
            key = KEY_CANCEL,
            onClick = { interactionListener?.onDialogNeutralClick(this) },
        )
    }

    private fun createButton(id: Int, key: String, onClick: () -> Unit) {
        view?.findViewById<Button>(id)?.apply {
            arguments?.getString(key)?.let {
                text = it
                setOnClickListener {
                    onClick()
                }
            } ?: run { visibility = View.GONE }
        }
    }

    interface InteractionListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
        fun onDialogNeutralClick(dialog: DialogFragment)
    }
}