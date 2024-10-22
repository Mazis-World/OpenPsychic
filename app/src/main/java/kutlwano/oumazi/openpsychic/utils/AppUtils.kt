package kutlwano.oumazi.openpsychic.utils

import java.text.SimpleDateFormat
import java.util.*

object AppUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getCurrentDate(): String {
        val date = Calendar.getInstance().time
        return dateFormat.format(date)
    }
}