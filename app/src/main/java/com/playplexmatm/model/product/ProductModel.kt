package com.playplexmatm.model.product

data class ProductModel(
    val name: String,
    val slug: String,
    val barcode_symbology: String,
    val item_code: String,
    val xid: String,
    val image_url: String,
    val image: String,
    val description:String,
    val x_category_id: String,
    val x_brand_id: String,
    val x_unit_id: String,
    val x_warehouse_id: String,
    val category: Category,
    val brand: Brand,
    val unit: Units,
    val details: Details

)

data class Category(
    val name: String,
    val xid: String,
)

data class Units(
    val name: String,
    val short_name: String,
    val xid: String,
)

data class Details(
    val opening_stock: Int,
    val opening_stock_date: String,
    val wholesale_price: String,
    val wholesale_quantity: String,
    val mrp: Int,
    val purchase_tax_type:String,
    val purchase_price: String,
    val sales_price: Int,
    val current_stock: Int,
    val quantity: Int,
    val tax: Tax,
    val x_tax_id:String,
    val warehouse:WareHouse
)

data class Tax(
    val name: String,
    val rate: Int,
    val xid: String,
)

data class Brand(
    val name: String,
    val xid: String,
)
data class WareHouse(
    val name: String,
    val xid: String,
)