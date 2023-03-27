package com.action.round.ui.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.action.round.R
import com.action.round.dialog.setUpButton

class EnterTextDialog : DialogFragment() {

    companion object {
        private const val KEY_TEXT = "message"
        private const val KEY_POS = "yesBtn"
        private const val KEY_NEG = "noBtn"
        private const val KEY_NEUTR = "cancel"
        private const val KEY_TITLE = "title"

        fun newInstance(
            text: String,
            positiveBtn: String?,
            negativeBtn: String?,
            neutralBtn: String?,
            title: String,
        ): EnterTextDialog {
            val dialog = EnterTextDialog()
            dialog.arguments = bundleOf(
                KEY_TEXT to text,
                KEY_POS to positiveBtn,
                KEY_NEG to negativeBtn,
                KEY_NEUTR to neutralBtn,
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
        return inflater.inflate(R.layout.dialog_enter_text, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireDialog().setTitle(arguments?.getString(KEY_TITLE))

        val editText = view.findViewById<TextView>(R.id.etEnterText)
        editText.text = arguments?.getString(KEY_TEXT)

        arguments?.let { arguments ->
            view.apply {
                setUpButton(
                    id = R.id.btnPositiveEnterText,
                    key = KEY_POS,
                    arguments = arguments,
                    onClick = {
                        interactionListener?.onDialogSetTextClick(
                            dialog = this@EnterTextDialog,
                            newText = editText.text.toString(),
                        )
                    }
                )
                setUpButton(
                    id = R.id.btnNegativeEnterText,
                    key = KEY_NEG,
                    arguments = arguments,
                    onClick = { editText.text = "" }
                )
                setUpButton(
                    id = R.id.btnNeutralEnterText,
                    key = KEY_NEUTR,
                    arguments = arguments,
                    onClick = { dismiss() }
                )
            }
        }
    }

    interface InteractionListener {
        fun onDialogSetTextClick(dialog: DialogFragment, newText: String)
    }
}