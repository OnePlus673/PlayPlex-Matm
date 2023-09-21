package com.playplexmatm.model.bills

data class BusinessProfile(
    val businessName: String = "",
    val phone: String = "",
    val businessAddress: String = "",
    val city: String = "",
    val state: String = "",
    val panCardNo: String = "",
    val gstinNo: String = "",
) {
    constructor() : this("", "", "", "", "", "", "")
}