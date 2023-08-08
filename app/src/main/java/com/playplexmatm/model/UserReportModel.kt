package com.playplexmatm.model

class UserReportModel(
    val user_type: String,
    val name: String,
    val email: String,
    val phone: String,
    val details: Details
)

class Details(
    val purchase_order_count: String,
    val purchase_return_count: String,
    val sales_order_count: String,
    val sales_return_count: String,
    val total_amount: String,
    val paid_amount: String,
    val due_amount: String
)