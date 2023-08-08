package com.playplexmatm.model.report

class StockSummaryModel(
    val name: String,
    val item_code: String,
    val image_url: String,
    val category: Category,
    val brand: Brand,
    val unit: Unit,
    val details: Details
)

class Category(
    val name: String
)

class Brand(
    val name: String,
    val short_name: String
)

class Details(
    val current_stock: String,
    val stock_quantitiy_alert: String,
    val opening_stock_date: String,
    val wholesale_price: String,
    val wholesale_quantity: String,
    val mrp: String,
    val purchase_price: String,
    val sales_price: String,
    val purchase_tax_type: String,
    val sales_tax_type: String,
    val status: String
)
