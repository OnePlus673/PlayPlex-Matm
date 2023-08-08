package com.playplexmatm.model.pos

data class PaymentModel(
    val payment_mode_id:String,
    val payment_mode_name:String,
    val amount:Double,
    val notes:String,
)