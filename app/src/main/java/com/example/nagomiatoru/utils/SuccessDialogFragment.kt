package com.example.nagomiatoru.utils

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.nagomiatoru.R

/**
 * Diálogo de éxito reutilizable que muestra un mensaje de confirmación
 * y redirige al usuario después de un tiempo determinado.
 */
class SuccessDialogFragment : DialogFragment() {

    private var title: String = "Success!"
    private var message: String = "Your action was completed successfully."
    private var delayMillis: Long = 2000
    private var onDismissAction: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuperar argumentos si existen
        arguments?.let {
            title = it.getString(ARG_TITLE, title)
            message = it.getString(ARG_MESSAGE, message)
            delayMillis = it.getLong(ARG_DELAY, delayMillis)
        }

        // Hacer que el diálogo no se pueda cancelar tocando fuera
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.success_dialog, container, false)
        view?.setBackgroundResource(android.R.color.transparent)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar contenido
        view.findViewById<TextView>(R.id.tv_success_title).text = title
        view.findViewById<TextView>(R.id.tv_success_message).text = message

        // Cerrar automáticamente el diálogo después del tiempo especificado
        Handler(Looper.getMainLooper()).postDelayed({
            dismiss()
            onDismissAction?.invoke()
        }, delayMillis)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(null)
        }
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_MESSAGE = "message"
        private const val ARG_DELAY = "delay"

        /**
         * Crea y muestra un diálogo de éxito.
         *
         * @param fragmentManager El gestor de fragmentos donde mostrar el diálogo
         * @param title El título del diálogo
         * @param message El mensaje descriptivo
         * @param delayMillis El tiempo de espera antes de cerrar automáticamente
         * @param onDismiss Acción a ejecutar cuando se cierre el diálogo
         */
        fun show(
            fragmentManager: FragmentManager,
            title: String = "Success!",
            message: String = "Your action was completed successfully.",
            delayMillis: Long = 2000,
            onDismiss: () -> Unit
        ) {
            val fragment = SuccessDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_MESSAGE, message)
                    putLong(ARG_DELAY, delayMillis)
                }
                this.onDismissAction = onDismiss
            }

            fragment.show(fragmentManager, "SuccessDialog")
        }
    }
}