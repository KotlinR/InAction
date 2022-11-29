package com.example.abox.dialogs

import android.annotation.SuppressLint
import android.opengl.Visibility
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
        private const val KEY_TYPE = "type"
        private const val KEY_INDEX = "index"

        fun newInstance(
            message: String,
            yesBtn: String,
            noBtn: String?,
            cancelBtn: String,
            title: String,
            type: String,
            index: Int?,
        ): ConfirmationDialog {
            val dialog = ConfirmationDialog()
            dialog.arguments = bundleOf(
                KEY_MESSAGE to message,
                KEY_YES to yesBtn,
                KEY_NO to noBtn,
                KEY_CANCEL to cancelBtn,
                KEY_TITLE to title,
                KEY_TYPE to type,
                KEY_INDEX to index,
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


        view.findViewById<Button>(R.id.posButton).apply {
            arguments?.getString(KEY_YES)?.let {
                text = it
                setOnClickListener {
                    interactionListener?.onDialogPositiveClick(
                        dialog = this@ConfirmationDialog,
                        type = arguments?.getString(KEY_TYPE).toString(),
                        index = arguments?.getInt(KEY_INDEX),
                    )
                }
            } ?: run { visibility = View.GONE }
        }
        view.findViewById<Button>(R.id.negButton).apply {
            text = arguments?.getString(KEY_NO)
            setOnClickListener {
                interactionListener?.onDialogNegativeClick(
                    dialog = this@ConfirmationDialog,
                    type = arguments?.getString(KEY_TYPE).toString(),
                )
            }
        }
        view.findViewById<Button>(R.id.neutralButton).apply {
            text = arguments?.getString(KEY_CANCEL)
            setOnClickListener {
                interactionListener?.onDialogNeutralClick(this@ConfirmationDialog)
            }
        }
    }

    interface InteractionListener {
        fun onDialogPositiveClick(dialog: DialogFragment, type: String, index: Int?)
        fun onDialogNegativeClick(dialog: DialogFragment, type: String)
        fun onDialogNeutralClick(dialog: DialogFragment)
    }
}