package com.playplexmatm.model.bills
data class SaleBillRecord(
    val saleBillNumber: String = "",
    val date: String = "",
    val customer: Customer = Customer(),
    val saleBillAmount: String = "",
    val receivedAmount: String = "",
    val balanceDue: String = "",
    val paymentType: String = "",
    val notes: String = ""
){
    constructor() : this("", "",customer=Customer(),"","","","","")
}