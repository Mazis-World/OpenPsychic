package io.getmadd.openpsychic.model

import java.io.Serializable

class PaymentMethod (
    var id: String? = null,
    var provider: String? = null,
    var address: String? = null,
    ) : Serializable {
    constructor() : this(
        id = null,
        provider = null,
        address = null,
    )
}