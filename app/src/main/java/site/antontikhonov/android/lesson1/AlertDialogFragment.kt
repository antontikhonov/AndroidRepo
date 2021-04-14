package site.antontikhonov.android.lesson1

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

const val EXTRA_TITLE_DIALOG = "TITLE_DIALOG"

class AlertDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val resMessage = requireNotNull(arguments?.getInt(EXTRA_TITLE_DIALOG))
        return AlertDialog.Builder(activity)
            .setTitle(R.string.no_permissions_dialog_title)
            .setMessage(resMessage)
            .setPositiveButton(R.string.no_permissions_dialog_button) { _, _ -> (activity as? MainActivity)?.restartCheckPermission() }
            .create()
    }

    override fun onDestroyView() {
        dismissAllowingStateLoss()
        super.onDestroyView()
    }

    companion object {
        fun newInstance(resMessage: Int): AlertDialogFragment {
            val fragment = AlertDialogFragment()
            val args = Bundle()
            args.putInt(EXTRA_TITLE_DIALOG, resMessage)
            fragment.arguments = args
            return fragment
        }
    }

    interface AlertDialogDisplayer {
        fun displayAlertDialog(resMessage: Int)
    }
}