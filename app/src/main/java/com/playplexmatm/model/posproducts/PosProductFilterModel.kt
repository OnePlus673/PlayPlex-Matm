package com.playplexmatm.model.posproducts

import java.io.Serializable

data class PosProductFilterModel(
    val name: String = "",
    val image_url: String = "",
    val item_id: String = "",
    val xid: String,
    val discount_rate: Int = 0,
    val total_discount: Int = 0,
    val x_tax_id: String,
    val tax_type: String,
    val tax_rate: Int = 0,
    val total_tax: Double = 0.0,
    val x_unit_id: String,
    var unit_price: Double = 0.0,
    var single_unit_price: Double = 0.0,
    val subtotal: Double = 0.0,
    val current_stock: Double=0.0,
    var quantity: Double=0.0,
    var isSelected: Boolean = false
) : Serializable