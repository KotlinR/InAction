package com.example.abox.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.ButtonBarLayout
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.fragment.app.DialogFragment
import com.example.abox.R

class EnterTextDialog : DialogFragment() {

    companion object {
        private const val KEY_TEXT = "message"
        private const val KEY_YES = "yesBtn"
        private const val KEY_NO = "noBtn"
        private const val KEY_CANCEL = "cancel"
        private const val KEY_TITLE = "title"
        private const val KEY_TYPE = "type"
        private const val KEY_INDEX = "index"

        fun newInstance(
            text: String,
            positiveBtn: String,
            negativeBtn: String?,
            neutralBtn: String?,
            title: String,
            type: String,
            index: Int?,
        ): EnterTextDialog {
            val dialog = EnterTextDialog()
            dialog.arguments = bundleOf(
                KEY_TEXT to text,
                KEY_YES to positiveBtn,
                KEY_NO to negativeBtn,
                KEY_CANCEL to neutralBtn,
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
        return inflater.inflate(R.layout.enter_text_dialog, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireDialog().setTitle(arguments?.getString(KEY_TITLE))

        val editText = view.findViewById<TextView>(R.id.etEnterText)
        editText.text = arguments?.getString(KEY_TEXT)

        view.findViewById<Button>(R.id.btnPositiveEnterText).apply {
            text = arguments?.getString(KEY_YES)
            setOnClickListener {
                interactionListener?.onDialogSetTextClick(
                    dialog = this@EnterTextDialog,
                    newText = editText.text.toString(),
                    type = arguments?.getString(KEY_TYPE).toString(),
                    index = arguments?.getInt(KEY_INDEX),
                )
            }
        }

        view.findViewById<Button>(R.id.btnNegativeEnterText).apply {
            text = arguments?.getString(KEY_NO)
            setOnClickListener {
                dismiss()
            }
        }

        view.findViewById<Button>(R.id.btnNeutralEnterText).apply {
            text = arguments?.getString(KEY_CANCEL)
            setOnClickListener {
                editText.text = ""
            }
        }
    }

    interface InteractionListener {
        fun onDialogSetTextClick(dialog: DialogFragment, newText: String, type: String, index: Int?)
    }
}