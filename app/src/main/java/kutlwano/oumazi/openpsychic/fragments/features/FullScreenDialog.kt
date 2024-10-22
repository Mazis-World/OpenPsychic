package kutlwano.oumazi.openpsychic.fragments.features

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import kutlwano.oumazi.openpsychic.R

class FullScreenDialog(context: Context) : Dialog(context, R.style.FullScreenDialog) {
    init {
        // Set your custom dialog layout here
        setContentView(R.layout.layout_make_post_dream)

        // Set dialog size and other properties if needed
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }
}
