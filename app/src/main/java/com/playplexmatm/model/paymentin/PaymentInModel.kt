package com.playplexmatm.model.paymentin

data class PaymentInModel(
    val date: String,
    val amount: String,
    val payment_number: String,
    val notes: String,
    val payment_type: String,
    val xid: String,
    val payment_mode: PaymentMode,
    val user: User
)

data class PaymentMode(
    val name: String,
    val xid: String
)

data class User(
    val name: String,
    val profile_image: String,
    val user_type: String,
    val profile_image_url: String,
    val xid: String
)