package com.playplexmatm.model.report

class ProductsReportModel(
    val name: String,
    val image: String,
    val item_code: String,
    val xid: String,
    val image_url: String,
    val x_unit_id: String,
    val unit: Unit,
    val details: Details
)

class Unit(
    val short_name: String,
    val xid: String
)
