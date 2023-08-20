package com.playplexmatm.model.bills

data class Customer(
    val name: String = "",
    val phone: String = "",
    val gstin: String = "",
    val buildingNumber: String = "",
    val area: String = "",
    val pinCode: String = "",
    val city: String = "",
    val state: String = ""
) {
    constructor() : this("", "","","","","","","")
}