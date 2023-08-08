package com.playplexmatm.model.report

class SalesSummaryModel(
    val order_date: String,
    val invoice_number: String,
    val total: String,
    val payment_status: String,
    val xid: String,
    val x_user_id: String,
    val x_staff_user_id: String,
    val user: User,
    val staff_member: StaffMember
)

class User(
    val name: String,
    val profile_image: String,
    val user_type: String,
    val xid: String,
    val profile_image_url: String
)

class StaffMember(
    val name: String,
    val profile_image: String,
    val user_type: String,
    val xid: String,
    val profile_image_url: String
)