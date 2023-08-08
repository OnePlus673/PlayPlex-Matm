package com.playplexmatm.model.customers

data class CustomerModel(
    val user_type:String,
    val name:String,
    val email:String,
    val profile_image:String,
    val profile_image_url:String,
    val phone:String,
    val address:String,
    val shipping_address:String,
    val xid:String,
    val details:Details,
    val x_warehouse_id: String,
    val status: String
)
data class Details(
    val opening_balance:String,
    val opening_balance_type:String,
    val credit_period:String,
    val credit_limit:String,
    val due_amount:String,
    val warehouse:WareHouse
)
data class WareHouse(
    val name:String,
    val xid:String,
    val logo_url:String,
)