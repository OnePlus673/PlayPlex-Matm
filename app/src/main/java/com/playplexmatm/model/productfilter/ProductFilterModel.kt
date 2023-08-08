package com.playplexmatm.model.productfilter

import java.io.Serializable

data class ProductFilterModel(
    val name: String,
    val image_url: String,
    val item_id: String,
    val xid: String,
    val discount_rate: Int,
    val individual_discount: Double,
    val total_discount: Double,
    val x_tax_id: String,
    var tax_type: String,
    var tax_rate: Int,
    val total_tax: Double,
    val x_unit_id: String,
    var unit_price: Double,
    var single_unit_price: Double,
    val subtotal: Double,
    val current_stock: Int,
    var quantity: Int,
) : Serializable