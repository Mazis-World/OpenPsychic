package kutlwano.oumazi.openpsychic.model

import com.google.firebase.Timestamp
import java.io.Serializable

class RequestStatusUpdate(
    var status: String,
    var timestamp: Timestamp
) : Serializable {
    constructor() : this(
        timestamp= Timestamp.now(),
        status=""
    )
}