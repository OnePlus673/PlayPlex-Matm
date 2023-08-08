package com.playplexmatm.model.purchasemodel

import com.playplexmatm.model.saleList.Details
import com.playplexmatm.model.saleList.Items
import com.playplexmatm.model.saleList.OrderPayments
import com.playplexmatm.model.saleList.Payment
import com.playplexmatm.model.saleList.PaymentMode
import com.playplexmatm.model.saleList.Product
import com.playplexmatm.model.saleList.StaffMember
import com.playplexmatm.model.saleList.Unit
import com.playplexmatm.model.saleList.User
import java.io.Serializable

data class PurchaseModel(
    val order_date: String,
    val order_status: String,
    val unique_id: String,
    val invoice_number: String,
    val order_type: String,
    val subtotal: String,
    val paid_amount: String,
    val due_amount: String,
    val discount: String,
    val shipping: String,
    val tax_amount: String,
    val total: String,
    val xid: String,
    val terms_condition: String,
    val payment_status: String,
    val user: User,
    val staff_member: StaffMember,
    val items: List<Items>,
    val order_payments: List<OrderPayments>

) : Serializable

data class StaffMember(
    val name: String,
    val user_type: String,
    val xid: String
): Serializable

data class User(
    val user_type: String,
    val name: String,
    val phone: String,
): Serializable


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
    val x_product_id: String,
    val tax_type: String,
    val tax_rate: String,
    val stock_quantity: Double,
    var isSelected: Boolean = false,
    val product: Product
) : Serializable

data class Product(
    val name: String,
    val image_url: String,
    val details: Details,
    val unit: Unit
): Serializable

data class OrderPayments(
    val amount: String,
    val date: String,
    val notes: String,
    val payment: Payment
): Serializable

data class Payment(
    val amount: String,
    val date: String,
    val payment_mode: PaymentMode
): Serializable

data class PaymentMode(
    val name: String
): Serializable

data class Details(
    val current_stock: String,
    val xid: String,
    val x_warehouse_id: String,
    val x_product_id: String,
    val x_tax_id: String
): Serializable

data class Unit(
    val name: String,
    val short_name : String,
    val xid: String
): Serializable