package com.playplexmatm.model.bills

data class Customer(
    val name: String = "",
    val phone: String = ""
) {
    constructor() : this("", "")
}