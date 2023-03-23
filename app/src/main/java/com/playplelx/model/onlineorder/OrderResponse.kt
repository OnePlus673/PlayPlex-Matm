package com.playplelx.model.onlineorder

import android.telecom.Call.Details

data class OrderResponse(
    val unique_id: String,
    val invoice_number: String,
    val order_type: String,
    val order_date: String,
    val subtotal: Double,
    val due_amount: Double,
    val order_status: String,
    val cancelled: Int,
    val xid: String,
    val shipping_address: ShippingAddress,
    val user: User,
    val items: ArrayList<Items>,
)

data class User(
    val user_type: String,
    val name: String,
    val phone: String,
    val profile_image_url: String,
)

data class ShippingAddress(
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val shipping_address: String,
    val city: String,
    val state: String,
    val country: String,
    val zipcode: String,
)

data class Items(
    val single_unit_price: Double,
    val unit_price: Double,
    val quantity: Double,
    val tax_rate: Double,
    val total_tax: Double,
    val subtotal: Double,
    val xid: String,
    val product: Product
)

data class Product(
    val name: String,
    val image_url: String,
    val xid: String,
    val details: ProductDetails
)

data class ProductDetails(
    val current_stock: Double,
    val xid: String
)