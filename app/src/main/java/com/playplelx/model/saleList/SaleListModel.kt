package com.playplelx.model.saleList

data class SaleListModel(
    val unique_id: String,
    val invoice_number: String,
    val order_type: String,
    val total: Double,
    val xid: String,
    val payment_status: String,
    val user: User,
    val staff_member: StaffMember

)

data class StaffMember(
    val name: String,
    val user_type: String,
    val xid: String
)

data class User(
    val user_type: String,
    val name: String,
    val phone: String,
)