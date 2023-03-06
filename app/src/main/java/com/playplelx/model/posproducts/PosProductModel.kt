package com.playplelx.model.posproducts

import com.playplelx.model.product.Units
import java.io.Serializable

data class PosProductModel(
    val status: Boolean,
    val message: String,
    val data: Data
)

data class Data(
    val products: ArrayList<ProductData>
)

data class ProductData(
    val item_id: String,
    val xid: String,
    val name: String,
    val image: String,
    val image_url: String,
    val discount_rate: Int = 0,
    val total_discount: Int = 0,
    val x_tax_id: String,
    val tax_type: String,
    val tax_rate: Int,
    val total_tax: Double=0.0,
    val x_unit_id: String,
    var unit_price: Double = 0.0,
    var single_unit_price: Double = 0.0,
    val subtotal: Double=0.0,
    var quantity: Double = 0.0,
    val stock_quantity: Double = 0.0,
    val unit_short_name: String,
    var isSelected: Boolean = false
):Serializable

data class Unit(
    val company_id: String,
    val name: String,
    val short_name: String,
    val base_unit: String,
    val operator: String,
    val operator_value: String,
    val xid: String,
)