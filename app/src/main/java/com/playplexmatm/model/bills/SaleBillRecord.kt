package com.playplexmatm.model.bills
data class SaleBillRecord(
    val saleBillNumber: String = "",
    val date: String = "",
    val customer: Customer = Customer(),
    val amount: String = "",
    val paymentType: String = "",
    val notes: String = ""
)