package com.playplelx.model.categoryofproducts

data class CategoryProductModel(
    val products: ArrayList<Products>
)

data class Products(
    val xid: String,
    val name: String,
    val items: ArrayList<Items>
):java.io.Serializable

data class Items(
    val item_id: String,
    val xid: String,
    val name: String,
    val image_url: String,
    val discount_rate: Double,
    val total_discount: Double,
    val total_tax: Double,
    val unit_price: Double,
    val single_unit_price: Double,
    var subtotal: Double,
    var quantity: Double,
    val x_unit_id: String,
    val stock_quantity: Double,
    var isSelected: Boolean = false
) : java.io.Serializable