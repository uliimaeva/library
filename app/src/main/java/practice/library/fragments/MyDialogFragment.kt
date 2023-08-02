package practice.library.fragments

import android.app.Dialog
import android.os.Bundle
import android.os.Process
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.DialogFragment
import kotlin.system.exitProcess

class MyDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Вы хотите выйти?")
                .setCancelable(true)
                .setPositiveButton("Да") { _, _ ->
                    finishAffinity(requireActivity())
                    exitProcess(0)
                }
                .setNegativeButton(
                    "Нет"
                ) { _, _ ->
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
