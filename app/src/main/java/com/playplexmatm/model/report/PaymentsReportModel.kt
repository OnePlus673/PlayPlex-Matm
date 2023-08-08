package com.playplexmatm.model.report

class PaymentsReportModel(
    val date: String,
    val payment_type: String,
    val amount: String,
    val payment_number: String,
    val xid: String,
    val x_user_id: String,
    val x_payment_mode_id: String,
    val user: User,
    val payment_mode: PaymentMode
)

class PaymentMode(
    val name: String,
    val mode_type: String,
    val xid: String
)