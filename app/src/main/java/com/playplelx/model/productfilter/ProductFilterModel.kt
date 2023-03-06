package com.playplelx.model.productfilter

data class ProductFilterModel(
    val name: String = "",
    val image_url: String = "",
    val item_id: String = "",
    val xid: String,
    val discount_rate: Int = 0,
    val total_discount: Int = 0,
    val x_tax_id: String,
    val tax_type: String,
    val tax_rate: Int = 0,
    val total_tax: Int = 0,
    val x_unit_id: String,
    var unit_price: Int = 0,
    var single_unit_price: Int=0,
    val subtotal: Int = 0,
    val current_stock: Int,
    var quantity: Int,
)