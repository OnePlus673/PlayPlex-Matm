package com.playplexmatm.network

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @FormUrlEncoded
    @POST("rest/auth/login")
    fun doLogin(
        @Field("email") email: String,
        @Field("password") paasword: String
    ): Call<JsonObject>

    @POST("rest/auth/logout")
    fun doLogout(): Call<JsonObject>

    @GET("rest/categories?fields=id,xid,name,slug,parent_id,x_parent_id,image,image_url")
    fun getCategory(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<JsonObject>

    @GET("rest/categories?fields=id,xid,name,slug,parent_id,x_parent_id,image,image_url&limit=1000")
    fun getCategoryDropDown(
    ): Call<JsonObject>

    @GET("rest/brands?fields=id,xid,name,slug,image,image_url")
    fun getBrand(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<JsonObject>

    @GET("rest/brands?fields=id,xid,name,slug,image,image_url&limit=1000")
    fun getBrandDropDown(): Call<JsonObject>

    @Multipart
    @POST("rest/upload-file")
    fun uploadFile(
        @Part uploadphoto: MultipartBody.Part,
        @Part("folder") folder: RequestBody,
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("rest/categories")
    fun addCategory(
        @Field("name") name: String,
        @Field("slug") slug: String,
        @Field("parent_id") parent_id: String,
        @Field("image") image: String,
    ): Call<JsonObject>

    @FormUrlEncoded
    @PUT("rest/categories/{id}")
    fun updateCategory(
        @Path("id") id: String,
        @Field("name") name: String,
        @Field("slug") slug: String,
        @Field("parent_id") parent_id: String,
        @Field("image") image: String,
    ): Call<JsonObject>

    @DELETE("rest/categories/{id}")
    fun deleteCategory(@Path("id") id: String): Call<JsonObject>

    @FormUrlEncoded
    @POST("rest/brands")
    fun addBrand(
        @Field("name") name: String,
        @Field("slug") slug: String,
        @Field("image") image: String,
        @Field("image_url") image_url: String,
    ): Call<JsonObject>

    @FormUrlEncoded
    @PUT("rest/brands/{id}")
    fun updateBrand(
        @Path("id") id: String,
        @Field("name") name: String,
        @Field("slug") slug: String,
        @Field("image") image: String,
        @Field("image_url") image_url: String,
    ): Call<JsonObject>

    @DELETE("rest/brands/{id}")
    fun deleteBrand(@Path("id") id: String): Call<JsonObject>

    @FormUrlEncoded
    @POST("rest/taxes")
    fun addTexes(
        @Field("name") name: String,
        @Field("rate") rate: String
    ): Call<JsonObject>

    @GET("rest/taxes?fields=id,xid,name,rate")
    fun getTaxes(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<JsonObject>

    @GET("rest/taxes?fields=id,xid,name,rate&limit=1000")
    fun getTaxesDropDown(): Call<JsonObject>

    @FormUrlEncoded
    @PUT("rest/taxes/{id}")
    fun updateTaxes(
        @Path("id") id: String, @Field("name") name: String,
        @Field("rate") rate: String
    ): Call<JsonObject>

    @DELETE("rest/taxes/{id}")
    fun deleteTaxes(@Path("id") id: String): Call<JsonObject>

    @FormUrlEncoded
    @POST("rest/payment-modes")
    fun addPaymentModes(
        @Field("name") name: String,
        @Field("mode_type") mode_type: String
    ): Call<JsonObject>

    @GET("rest/payment-modes?fields=id,xid,name,mode_type")
    fun getPaymentModes(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<JsonObject>

    @GET("rest/payment-modes?fields=id,xid,name,mode_type&limit=1000")
    fun getPaymentModeDropDown(

    ): Call<JsonObject>

    @FormUrlEncoded
    @PUT("rest/payment-modes/{id}")
    fun updatePaymentModes(
        @Path("id") id: String, @Field("name") name: String,
        @Field("mode_type") rate: String
    ): Call<JsonObject>

    @DELETE("rest/payment-modes/{id}")
    fun deletepaymentModes(
        @Path("id") id: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("rest/units")
    fun addUnits(
        @Field("name") name: String,
        @Field("short_name") short_name: String,
        @Field("operator") operator: String,
        @Field("operator_value") operator_value: String
    ): Call<JsonObject>

    @GET("rest/units?fields=id,xid,name,short_name,operator,operator_value,is_deletable")
    fun getUnits(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<JsonObject>


    @GET("rest/units?fields=id,xid,name,short_name,operator,operator_value,is_deletable&limit=1000")
    fun getUnitDropDown(): Call<JsonObject>

    @FormUrlEncoded
    @PUT("rest/units/{id}")
    fun updateUnits(
        @Path("id") id: String, @Field("name") name: String,
        @Field("short_name") short_name: String,
        @Field("operator") operator: String,
        @Field("operator_value") operator_value: String
    ): Call<JsonObject>

    @DELETE("rest/units/{id}")
    fun deleteUnits(
        @Path("id") id: String
    ): Call<JsonObject>

    @GET("rest/warehouses?fields=id,xid,company_id,x_company_id,logo,logo_url,dark_logo,dark_logo_url,name,slug,email,phone,address,show_email_on_invoice,show_phone_on_invoice,show_mrp_on_invoice,show_discount_tax_on_invoice,terms_condition,bank_details,signature,signature_url,online_store_enabled,default_pos_order_status,customers_visibility,suppliers_visibility,products_visibility")
    fun getWareHouse(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<JsonObject>


    @GET("rest/warehouses?fields=id,xid,company_id,x_company_id,logo,logo_url,dark_logo,dark_logo_url,name,slug,email,phone,address,show_email_on_invoice,show_phone_on_invoice,show_mrp_on_invoice,show_discount_tax_on_invoice,terms_condition,bank_details,signature,signature_url,online_store_enabled,default_pos_order_status,customers_visibility,suppliers_visibility,products_visibility&limit=1000")
    fun getWareHouseDropDown(): Call<JsonObject>

    @DELETE("rest/warehouses/{id}")
    fun deleteWareHouse(@Path("id") id: String): Call<JsonObject>

    @FormUrlEncoded
    @POST("rest/warehouses")
    fun addWareHouse(
        @Field("name") name: String,
        @Field("slug") slug: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("logo") logo: String,
        @Field("logo_url") logo_url: String,
        @Field("address") address: String,
        @Field("default_pos_order_status") default_pos_order_status: String,
        @Field("customers_visibility") customers_visibility: String,
        @Field("suppliers_visibility") suppliers_visibility: String,
        @Field("products_visibility") products_visibility: String,
    ): Call<JsonObject>


    @FormUrlEncoded
    @PUT("rest/warehouses/{id}")
    fun updateWareHouse(
        @Path("id") id: String,
        @Field("name") name: String,
        @Field("slug") slug: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("logo") logo: String,
        @Field("logo_url") logo_url: String,
        @Field("address") address: String,
        @Field("default_pos_order_status") default_pos_order_status: String,
        @Field("customers_visibility") customers_visibility: String,
        @Field("suppliers_visibility") suppliers_visibility: String,
        @Field("products_visibility") products_visibility: String,
    ): Call<JsonObject>

    @GET("rest/customers?fields=id,xid,user_type,name,email,profile_image,profile_image_url,is_walkin_customer,phone,address,shipping_address,status,created_at,details{opening_balance,opening_balance_type,credit_period,credit_limit,due_amount,warehouse_id,x_warehouse_id},details:warehouse{id,xid,name},role_id,role{id,xid,name,display_name},warehouse_id,x_warehouse_id,warehouse{xid,name}")
    fun getCustomers(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<JsonObject>


    @GET("rest/customers?fields=id,xid,user_type,name,email,profile_image,profile_image_url,is_walkin_customer,phone,address,shipping_address,status,created_at,details{opening_balance,opening_balance_type,credit_period,credit_limit,due_amount,warehouse_id,x_warehouse_id},details:warehouse{id,xid,name},role_id,role{id,xid,name,display_name},warehouse_id,x_warehouse_id,warehouse{xid,name}&limit=1000")
    fun getCustomerDropDown(): Call<JsonObject>

    @FormUrlEncoded
    @POST("rest/customers")
    fun addCustomers(
        @Field("user_type") user_type: String,
        @Field("warehouse_id") warehouse_id: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("profile_image") profile_image: String,
        @Field("profile_image_url") profile_image_url: String,
        @Field("phone") phone: String,
        @Field("address") address: String,
        @Field("status") status: String,
        @Field("shipping_address") shipping_address: String,
        @Field("opening_balance") opening_balance: String,
        @Field("opening_balance_type") opening_balance_type: String,
        @Field("credit_period") credit_period: String,
        @Field("credit_limit") credit_limit: String,
        @Field("tax_number") tax_number: String,
    ): Call<JsonObject>

    @DELETE("rest/customers/{id}")
    fun deleteCustomers(@Path("id") id: String): Call<JsonObject>

    @FormUrlEncoded
    @PUT("rest/customers/{id}")
    fun updateCustomers(
        @Path("id") id: String,
        @Field("user_type") user_type: String,
        @Field("warehouse_id") warehouse_id: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("profile_image") profile_image: String,
        @Field("profile_image_url") profile_image_url: String,
        @Field("phone") phone: String,
        @Field("address") address: String,
        @Field("status") status: String,
        @Field("shipping_address") shipping_address: String,
        @Field("opening_balance") opening_balance: String,
        @Field("opening_balance_type") opening_balance_type: String,
        @Field("credit_period") credit_period: String,
        @Field("credit_limit") credit_limit: String,
        @Field("tax_number") tax_number: String,
    ): Call<JsonObject>


    @GET("rest/suppliers?fields=id,xid,user_type,name,email,profile_image,profile_image_url,is_walkin_customer,phone,address,shipping_address,status,created_at,details{opening_balance,opening_balance_type,credit_period,credit_limit,due_amount,warehouse_id,x_warehouse_id},details:warehouse{id,xid,name},role_id,role{id,xid,name,display_name},warehouse_id,x_warehouse_id,warehouse{xid,name}")
    fun getSuppliers(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<JsonObject>


    @GET("rest/suppliers?fields=id,xid,user_type,name,email,profile_image,profile_image_url,is_walkin_customer,phone,address,shipping_address,status,created_at,details{opening_balance,opening_balance_type,credit_period,credit_limit,due_amount,warehouse_id,x_warehouse_id},details:warehouse{id,xid,name},role_id,role{id,xid,name,display_name},warehouse_id,x_warehouse_id,warehouse{xid,name}&limit=1000")
    fun getSuppliersDropDown(): Call<JsonObject>

    @FormUrlEncoded
    @POST("rest/suppliers")
    fun addSuppliers(
        @Field("user_type") user_type: String,
        @Field("warehouse_id") warehouse_id: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("profile_image") profile_image: String,
        @Field("profile_image_url") profile_image_url: String,
        @Field("phone") phone: String,
        @Field("address") address: String,
        @Field("status") status: String,
        @Field("shipping_address") shipping_address: String,
        @Field("opening_balance") opening_balance: String,
        @Field("opening_balance_type") opening_balance_type: String,
        @Field("credit_period") credit_period: String,
        @Field("credit_limit") credit_limit: String,
        @Field("tax_number") tax_number: String,
    ): Call<JsonObject>

    @DELETE("rest/suppliers/{id}")
    fun deleteSuppliers(@Path("id") id: String): Call<JsonObject>

    @FormUrlEncoded
    @PUT("rest/suppliers/{id}")
    fun updateSuppliers(
        @Path("id") id: String,
        @Field("user_type") user_type: String,
        @Field("warehouse_id") warehouse_id: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("profile_image") profile_image: String,
        @Field("profile_image_url") profile_image_url: String,
        @Field("phone") phone: String,
        @Field("address") address: String,
        @Field("status") status: String,
        @Field("shipping_address") shipping_address: String,
        @Field("opening_balance") opening_balance: String,
        @Field("opening_balance_type") opening_balance_type: String,
        @Field("credit_period") credit_period: String,
        @Field("credit_limit") credit_limit: String,
        @Field("tax_number") tax_number: String,
    ): Call<JsonObject>

    @GET("rest/payment-in?fields=id,xid,date,amount,notes,payment_mode_id,payment_number,payment_type,payment_mode_id,x_payment_mode_id,paymentMode{id,xid,name},user_id,x_user_id,user{id,xid,name,profile_image,profile_image_url,user_type}")
    fun getPaymentIn(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("dates") date: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("rest/payment-in")
    fun addPaymentIn(
        @Field("user_id") user_id: String,
        @Field("payment_type") payment_type: String,
        @Field("payment_mode_id") payment_mode_id: String,
        @Field("notes") notes: String,
        @Field("date") date: String,
        @Field("amount") amount: String,
    ): Call<JsonObject>


    @FormUrlEncoded
    @PUT("rest/payment-in/{id}")
    fun updatePaymentIn(
        @Path("id") id: String,
        @Field("user_id") user_id: String,
        @Field("payment_type") payment_type: String,
        @Field("payment_mode_id") payment_mode_id: String,
        @Field("notes") notes: String,
        @Field("date") date: String,
        @Field("amount") amount: String,
    ): Call<JsonObject>

    @DELETE("rest/payment-in/{id}")
    fun deletePaymentIn(
        @Path("id") id: String
    ): Call<JsonObject>

    @GET("rest/payment-out?fields=id,xid,date,amount,notes,payment_mode_id,payment_number,payment_type,payment_mode_id,x_payment_mode_id,paymentMode{id,xid,name},user_id,x_user_id,user{id,xid,name,profile_image,profile_image_url,user_type}")
    fun getPaymentOut(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("dates") date: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("rest/payment-out")
    fun addPaymentOut(
        @Field("user_id") user_id: String,
        @Field("payment_type") payment_type: String,
        @Field("payment_mode_id") payment_mode_id: String,
        @Field("notes") notes: String,
        @Field("date") date: String,
        @Field("amount") amount: String,
    ): Call<JsonObject>


    @FormUrlEncoded
    @PUT("rest/payment-out/{id}")
    fun updatePaymentOut(
        @Path("id") id: String,
        @Field("user_id") user_id: String,
        @Field("payment_type") payment_type: String,
        @Field("payment_mode_id") payment_mode_id: String,
        @Field("notes") notes: String,
        @Field("date") date: String,
        @Field("amount") amount: String,
    ): Call<JsonObject>

    @DELETE("rest/payment-out/{id}")
    fun deletePaymentOUt(
        @Path("id") id: String
    ): Call<JsonObject>


    @GET("rest/products?fields=id,xid,name,slug,barcode_symbology,item_code,image,image_url,category_id,x_category_id,category{id,xid,name},brand_id,x_brand_id,brand{id,xid,name},unit_id,x_unit_id,unit{id,xid,name,short_name},description,details{stock_quantitiy_alert,opening_stock,opening_stock_date,wholesale_price,wholesale_quantity,mrp,purchase_price,sales_price,tax_id,x_tax_id,purchase_tax_type,sales_tax_type,current_stock,warehouse_id,x_warehouse_id,status},details:tax{id,xid,name,rate},details:warehouse{id,xid,name},customFields{id,xid,field_name,field_value},warehouse_id,x_warehouse_id,warehouse{id,xid}")
    fun getProducts(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("rest/pos/products")
    fun getPosProductsXid(
        @Field("category_id") category_id: String,

        ): Call<JsonObject>

    @GET("rest/products?fields=id,xid,name,slug,barcode_symbology,item_code,image,image_url,category_id,x_category_id,category{id,xid,name},brand_id,x_brand_id,brand{id,xid,name},unit_id,x_unit_id,unit{id,xid,name,short_name},description,details{stock_quantitiy_alert,opening_stock,opening_stock_date,wholesale_price,wholesale_quantity,mrp,purchase_price,sales_price,tax_id,x_tax_id,purchase_tax_type,sales_tax_type,current_stock,warehouse_id,x_warehouse_id,status},details:tax{id,xid,name,rate},details:warehouse{id,xid,name},customFields{id,xid,field_name,field_value},warehouse_id,x_warehouse_id,warehouse{id,xid}&limit=1000")
    fun getProductDropDown(
    ): Call<JsonObject>


    @FormUrlEncoded
    @POST("rest/products")
    fun addProducts(
        @Field("warehouse_id") warehouse_id: String,
        @Field("name") name: String,
        @Field("image") image: String,
        @Field("image_url") image_url: String,
        @Field("slug") slug: String,
        @Field("barcode_symbology") barcode_symbology: String,
        @Field("item_code") item_code: String,
        @Field("stock_quantitiy_alert") stock_quantitiy_alert: String,
        @Field("category_id") category_id: String,
        @Field("brand_id") brand_id: String,
        @Field("mrp") mrp: String,
        @Field("purchase_price") purchase_price: String,
        @Field("sales_price") sales_price: String,
        @Field("tax_id") tax_id: String,
        @Field("unit_id") unit_id: String,
        @Field("purchase_tax_type") purchase_tax_type: String,
        @Field("description") description: String,
        @Field("sales_tax_type") sales_tax_type: String,
        @Field("opening_stock") opening_stock: String,
        @Field("opening_stock_date") opening_stock_date: String,
        @Field("wholesale_price") wholesale_price: String,
        @Field("wholesale_quantity") wholesale_quantity: String
    ): Call<JsonObject>


    @FormUrlEncoded
    @PUT("rest/products/{id}")
    fun updateProducts(
        @Path("id") id: String,
        @Field("warehouse_id") warehouse_id: String,
        @Field("name") name: String,
        @Field("image") image: String,
        @Field("image_url") image_url: String,
        @Field("slug") slug: String,
        @Field("barcode_symbology") barcode_symbology: String,
        @Field("item_code") item_code: String,
        @Field("stock_quantitiy_alert") stock_quantitiy_alert: String,
        @Field("category_id") category_id: String,
        @Field("brand_id") brand_id: String,
        @Field("mrp") mrp: String,
        @Field("purchase_price") purchase_price: String,
        @Field("sales_price") sales_price: String,
        @Field("tax_id") tax_id: String,
        @Field("unit_id") unit_id: String,
        @Field("purchase_tax_type") purchase_tax_type: String,
        @Field("description") description: String,
        @Field("sales_tax_type") sales_tax_type: String,
        @Field("opening_stock") opening_stock: String,
        @Field("opening_stock_date") opening_stock_date: String,
        @Field("wholesale_price") wholesale_price: String,
        @Field("wholesale_quantity") wholesale_quantity: String
    ): Call<JsonObject>


    @DELETE("rest/products/{id}")
    fun deleteProducts(@Path("id") id: String): Call<JsonObject>

    @GET(
        "rest/sales?fields=id,xid,unique_id,warehouse_id,x_warehouse_id,warehouse{id,xid,name},from_warehouse_id,x_from_warehouse_id,fromWarehouse{id,xid,name},invoice_number,order_type,order_date,tax_amount,discount,shipping,subtotal,paid_amount,due_amount,order_status,payment_status,total,tax_rate,staff_user_id,x_staff_user_id,staffMember{id,xid,name,profile_image,profile_image_url,user_type},user_id,x_user_id,user{id,xid,user_type,name,profile_image,profile_image_url,phone},orderPayments{id,xid,amount,payment_id,x_payment_id},orderPayments:payment{id,xid,amount,payment_mode_id,x_payment_mode_id,date,notes},orderPayments:payment:paymentMode{id,xid,name},items{id,xid,product_id,x_product_id,single_unit_price,unit_price,quantity,tax_rate,total_tax,tax_type,total_discount,subtotal},items:product{id,xid,name,image,image_url,unit_id,x_unit_id},items:product:unit{id,xid,name,short_name},items:product:details{id,xid,warehouse_id,x_warehouse_id,product_id,x_product_id,current_stock},cancelled,terms_condition,shippingAddress{id,xid,order_id,name,email,phone,address,shipping_address,city,state,country,zipcode}"
    )
    fun getSales(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("dates") dates: String
    ): Call<JsonObject>

    @GET(
        "rest/sales/{id}?fields=id,xid,unique_id,warehouse_id,x_warehouse_id,warehouse{id,xid,name},from_warehouse_id,x_from_warehouse_id,fromWarehouse{id,xid,name},invoice_number,order_type,order_date,tax_amount,discount,shipping,subtotal,paid_amount,due_amount,order_status,payment_status,total,tax_rate,staff_user_id,x_staff_user_id,staffMember{id,xid,name,profile_image,profile_image_url,user_type},user_id,x_user_id,user{id,xid,user_type,name,profile_image,profile_image_url,phone},orderPayments{id,xid,amount,payment_id,x_payment_id},orderPayments:payment{id,xid,amount,payment_mode_id,x_payment_mode_id,date,notes},orderPayments:payment:paymentMode{id,xid,name},items{id,xid,product_id,x_product_id,single_unit_price,unit_price,quantity,tax_rate,total_tax,tax_type,total_discount,subtotal},items:product{id,xid,name,image,image_url,unit_id,x_unit_id},items:product:unit{id,xid,name,short_name},items:product:details{id,xid,warehouse_id,x_warehouse_id,product_id,x_product_id,current_stock},cancelled,terms_condition,shippingAddress{id,xid,order_id,name,email,phone,address,shipping_address,city,state,country,zipcode}&xid"
    )
    fun getSalesId(
        @Path("id") id: String
    ): Call<JsonObject>

    @DELETE("rest/sales/{id}")
    fun deleteSales(
        @Path("id") id: String
    ): Call<JsonObject>


    @FormUrlEncoded
    @POST("rest/sales")
    fun addSales(
        @Field("order_type") order_type: String,
        @Field("invoice_number") invoice_number: String,
        @Field("order_date") order_date: String,
        @Field("warehouse_id") warehouse_id: String,
        @Field("user_id") user_id: String,
        @Field("terms_condition") terms_condition: String,
        @Field("notes") notes: String,
        @Field("order_status") order_status: String,
        @Field("tax_id") tax_id: String?,
        @Field("tax_rate") tax_rate: String,
        @Field("tax_amount") tax_amount: String,
        @Field("discount") discount: String,
        @Field("shipping") shipping: String,
        @Field("subtotal") subtotal: String,
        @Field("total") total: String,
        @Field("total_items") total_items: String,
        @Field("product_items") product_items: JSONArray,
        @Field("is_product_invoice") is_product_invoice: Int

        ): Call<JsonObject>

    @FormUrlEncoded
    @PUT("rest/sales")
    fun updateSales(
        @Field("order_type") order_type: String,
        @Field("invoice_number") invoice_number: String,
        @Field("order_date") order_date: String,
        @Field("warehouse_id") warehouse_id: String,
        @Field("user_id") user_id: String,
        @Field("terms_condition") terms_condition: String,
        @Field("notes") notes: String,
        @Field("order_status") order_status: String,
        @Field("tax_id") tax_id: String?,
        @Field("tax_rate") tax_rate: String,
        @Field("tax_amount") tax_amount: String,
        @Field("discount") discount: String,
        @Field("shipping") shipping: String,
        @Field("subtotal") subtotal: String,
        @Field("total") total: String,
        @Field("total_items") total_items: String,
        @Field("product_items") product_items: JSONArray,

        ): Call<JsonObject>


    @GET(
        "rest/purchases?fields=id,xid,unique_id,warehouse_id,x_warehouse_id,warehouse{id,xid,name},from_warehouse_id,x_from_warehouse_id,fromWarehouse{id,xid,name},invoice_number,order_type,order_date,tax_amount,discount,shipping,subtotal,paid_amount,due_amount,order_status,payment_status,total,tax_rate,staff_user_id,x_staff_user_id,staffMember{id,xid,name,profile_image,profile_image_url,user_type},user_id,x_user_id,user{id,xid,user_type,name,profile_image,profile_image_url,phone},orderPayments{id,xid,amount,payment_id,x_payment_id},orderPayments:payment{id,xid,amount,payment_mode_id,x_payment_mode_id,date,notes},orderPayments:payment:paymentMode{id,xid,name},items{id,xid,product_id,x_product_id,single_unit_price,unit_price,quantity,tax_rate,total_tax,tax_type,total_discount,subtotal},items:product{id,xid,name,image,image_url,unit_id,x_unit_id},items:product:unit{id,xid,name,short_name},items:product:details{id,xid,warehouse_id,x_warehouse_id,product_id,x_product_id,current_stock},cancelled,terms_condition,shippingAddress{id,xid,order_id,name,email,phone,address,shipping_address,city,state,country,zipcode}"
    )
    fun getPurchase(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("dates") date: String
    ): Call<JsonObject>

    @GET(
        "rest/purchases/{id}?fields=id,xid,unique_id,warehouse_id,x_warehouse_id,warehouse{id,xid,name},from_warehouse_id,x_from_warehouse_id,fromWarehouse{id,xid,name},invoice_number,order_type,order_date,tax_amount,discount,shipping,subtotal,paid_amount,due_amount,order_status,payment_status,total,tax_rate,staff_user_id,x_staff_user_id,staffMember{id,xid,name,profile_image,profile_image_url,user_type},user_id,x_user_id,user{id,xid,user_type,name,profile_image,profile_image_url,phone},orderPayments{id,xid,amount,payment_id,x_payment_id},orderPayments:payment{id,xid,amount,payment_mode_id,x_payment_mode_id,date,notes},orderPayments:payment:paymentMode{id,xid,name},items{id,xid,product_id,x_product_id,single_unit_price,unit_price,quantity,tax_rate,total_tax,tax_type,total_discount,subtotal},items:product{id,xid,name,image,image_url,unit_id,x_unit_id},items:product:unit{id,xid,name,short_name},items:product:details{id,xid,warehouse_id,x_warehouse_id,product_id,x_product_id,current_stock},cancelled,terms_condition,shippingAddress{id,xid,order_id,name,email,phone,address,shipping_address,city,state,country,zipcode}&xid"
    )
    fun getPurchaseId(
        @Path("id") id: String
    ): Call<JsonObject>


    @GET(
        "/api/rest/customers?fields=id,xid,user_type,name,email,profile_image,profile_image_url,phone,details{purchase_order_count,purchase_return_count,sales_order_count,sales_return_count,total_amount,paid_amount,due_amount}&order=id desc&offset=0&limit=10"
    )
    fun getUserReportCustomer(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<JsonObject>

    @GET(
        "/api/rest/suppliers?fields=id,xid,user_type,name,email,profile_image,profile_image_url,phone,details{purchase_order_count,purchase_return_count,sales_order_count,sales_return_count,total_amount,paid_amount,due_amount}&order=id desc&offset=0&limit=10"
    )
    fun getUserReportSupplier(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<JsonObject>

    @DELETE("rest/purchases/{id}")
    fun deletePurchase(@Path("id") id: String): Call<JsonObject>


    @FormUrlEncoded
    @POST("rest/purchases")
    fun addPurchases(
        @Field("order_type") order_type: String,
        @Field("invoice_number") invoice_number: String,
        @Field("order_date") order_date: String,
        @Field("warehouse_id") warehouse_id: String,
        @Field("user_id") user_id: String,
        @Field("terms_condition") terms_condition: String,
        @Field("notes") notes: String,
        @Field("order_status") order_status: String,
        @Field("tax_id") tax_id: String? = null,
        @Field("tax_rate") tax_rate: String,
        @Field("tax_amount") tax_amount: String,
        @Field("discount") discount: String,
        @Field("shipping") shipping: String,
        @Field("subtotal") subtotal: String,
        @Field("total") total: String,
        @Field("total_items") total_items: String,
        @Field("product_items") product_items: JSONArray,
        @Field("is_product_invoice") is_product_invoice: Int,

        ): Call<JsonObject>

    @FormUrlEncoded
    @PUT("rest/purchases/{id}")
    fun updatePurchases(
        @Path("id") id: String,
        @Field("order_type") order_type: String,
        @Field("invoice_number") invoice_number: String,
        @Field("order_date") order_date: String,
        @Field("warehouse_id") warehouse_id: String,
        @Field("user_id") user_id: String,
        @Field("terms_condition") terms_condition: String,
        @Field("notes") notes: String,
        @Field("order_status") order_status: String,
        @Field("tax_id") tax_id: String? = null,
        @Field("tax_rate") tax_rate: String,
        @Field("tax_amount") tax_amount: String,
        @Field("discount") discount: String,
        @Field("shipping") shipping: String,
        @Field("subtotal") subtotal: String,
        @Field("total") total: String,
        @Field("total_items") total_items: String,
        @Field("product_items") product_items: JSONArray,

        ): Call<JsonObject>

//    @FormUrlEncoded
//    @POST("rest/purchases")
//    fun addPurchasesDemo(
//        @
//        ): Call<JsonObject>

    @POST("rest/pos/products")
    fun getPosProducts(): Call<JsonObject>

    @FormUrlEncoded
    @POST("rest/pos/save")
    fun posSave(
        @Field("all_payments") all_payments: JSONArray,
        @Field("details") details: JSONObject,
        @Field("product_items") product_items: JSONArray
    ): Call<JsonObject>

    @POST("rest/dashboard")
    fun getDashBoard(): Call<JsonObject>

    @GET(
        "rest/online-orders?fields=id,xid,unique_id,warehouse_id,x_warehouse_id,warehouse{id,xid,name},from_warehouse_id,x_from_warehouse_id,fromWarehouse{id,xid,name},invoice_number,order_type,order_date,tax_amount,discount,shipping,subtotal,paid_amount,due_amount,order_status,payment_status,total,tax_rate,staff_user_id,x_staff_user_id,staffMember{id,xid,name,profile_image,profile_image_url,user_type},user_id,x_user_id,user{id,xid,user_type,name,profile_image,profile_image_url,phone},orderPayments{id,xid,amount,payment_id,x_payment_id},orderPayments:payment{id,xid,amount,payment_mode_id,x_payment_mode_id,date,notes},orderPayments:payment:paymentMode{id,xid,name},items{id,xid,product_id,x_product_id,single_unit_price,unit_price,quantity,tax_rate,total_tax,tax_type,total_discount,subtotal},items:product{id,xid,name,image,image_url,unit_id,x_unit_id},items:product:unit{id,xid,name,short_name},items:product:details{id,xid,warehouse_id,x_warehouse_id,product_id,x_product_id,current_stock},cancelled,terms_condition,shippingAddress{id,xid,order_id,name,email,phone,address,shipping_address,city,state,country,zipcode}"
    )
    fun getOnlineOrders(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<JsonObject>

    @POST("rest/online-orders/confirm/{id}")
    fun confirmOrders(@Path("id") id: String): Call<JsonObject>

    @POST("rest/online-orders/delivered/{id}")
    fun deliveredOrders(@Path("id") id: String): Call<JsonObject>

    @POST("rest/online-orders/cancel/{id}")
    fun canceledOrders(@Path("id") id: String): Call<JsonObject>

    @POST("rest/pos/categories")
    fun getAllCategories(): Call<JsonObject>

    @GET(
        "rest/payments?fields=id,xid,date,payment_type,amount,payment_number,user_id,x_user_id,user{id,xid,name,profile_image,profile_image_url,user_type},payment_mode_id,x_payment_mode_id,paymentMode{id,xid,name,mode_type}"
    )
    fun getPaymentReport(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("dates") date: String
    ): Call<JsonObject>

    @GET(
        "rest/products?fields=id,xid,name,image,image_url,item_code,unit_id,x_unit_id,unit{id,xid,short_name},details{product_id,x_product_id,current_stock,stock_quantitiy_alert}&fetch_stock_alert=true&order=id desc&offset=0&limit=10"
    )
    fun getStockAlert(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<JsonObject>


    @GET(
        "rest/sales?fields=id,xid,order_date,invoice_number,total,payment_status,user_id,x_user_id,user{id,xid,name,profile_image,profile_image_url,user_type},staff_user_id,x_staff_user_id,staffMember{id,xid,name,profile_image,profile_image_url,user_type}&order=order_date desc&offset=0&limit=10"
    )
    fun getSalesSummary(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("dates") date: String
    ): Call<JsonObject>

    @GET(
        "rest/products?fields=id,xid,name,,item_code,image,image_url,category_id,x_category_id,category{id,xid,name},brand_id,x_brand_id,brand{id,xid,name},unit_id,x_unit_id,unit{id,xid,name,short_name},details{stock_quantitiy_alert,opening_stock,opening_stock_date,wholesale_price,wholesale_quantity,mrp,purchase_price,sales_price,tax_id,x_tax_id,purchase_tax_type,sales_tax_type,current_stock,warehouse_id,x_warehouse_id,status},details:tax{id,xid,name,rate}&order=name asc&offset=0&limit=10"
    )
    fun getStockSummary(
        @Query("order") order: String,
        @Query("desc") desc: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<JsonObject>

    @GET(
        "rest/reports/profit-loss"
    )
    fun getProfitLoss(
        @Query("active_report_type") order: String
        ): Call<JsonObject>


    @FormUrlEncoded
    @POST("public/api/v1/order-payments")
    fun orderPayments(
        @Field("date") date: String,
        @Field("payment_mode_id") payment_mode_id: String,
        @Field("amount") amount: String,
        @Field("notes") notes: String,
        @Field("order_id") order_id: String
    ): Call<JsonObject>

    @GET("rest/order-html/{xid}/en")
    fun orderHtml(
        @Path("xid") xid: String
    ): Call<JsonObject>

    @GET("rest/payment-html/{xid}/en")
    fun paymentPdf(
        @Path("xid") xid: String
    ): Call<JsonObject>

}