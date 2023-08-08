package com.playplexmatm.activity.pos

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.activity.MainActivity
import com.playplexmatm.activity.itemlist.AddEditProductActivity
import com.playplexmatm.activity.partylist.AddEditCustomerActivity
import com.playplexmatm.adapter.CustomerListAdapter
import com.playplexmatm.adapter.PosProductAdapter
import com.playplexmatm.adapter.ProductPosAdapter
import com.playplexmatm.model.category.CategoryModel
import com.playplexmatm.model.categoryofproducts.Items
import com.playplexmatm.model.categoryofproducts.Products
import com.playplexmatm.model.customers.CustomerModel
import com.playplexmatm.model.posproducts.PosProductFilterModel
import com.playplexmatm.model.posproducts.ProductData
import com.playplexmatm.model.productfilter.ProductFilterModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.*
import kotlinx.android.synthetic.main.activity_pos_setting.*
import kotlinx.android.synthetic.main.layout_list_bottomsheet_customer.view.*
import kotlinx.android.synthetic.main.layout_list_category.view.*
import kotlinx.android.synthetic.main.layout_listview_bottomsheet.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PosSettingActivity : AppCompatActivity(), View.OnClickListener, PosProductAdapter.onClick,
    CustomerListAdapter.ListAdapterListener, ProductPosAdapter.onClick {

    lateinit var mContext: PosSettingActivity
    lateinit var ivBack: ImageView
    lateinit var apiInterface: ApiInterface
    lateinit var pbLoadData: ProgressBar
    lateinit var tvDetails: TextView
    lateinit var acUserName: AutoCompleteTextView
    private var categoryFilterProdcuctList: ArrayList<Products> = arrayListOf()

    private var userList: ArrayList<CustomerModel> = arrayListOf()
    private var userStringList: ArrayList<String> = arrayListOf()
    private var userId: String = ""
    lateinit var acProduct: AutoCompleteTextView
    private var categoryPassFilterProdcuctList: java.util.ArrayList<Products> = arrayListOf()
    var selectedCategoryId = ""
    lateinit var customerListAdapter: CustomerListAdapter

    private var productList: ArrayList<ProductData> = arrayListOf()
    private var productStringList: ArrayList<String> = arrayListOf()
    private var filterProductList: ArrayList<ProductFilterModel> = arrayListOf()
    lateinit var rvProducts: RecyclerView
    lateinit var rvCategory: RecyclerView
    private var categoryList: ArrayList<CategoryModel> = arrayListOf()

    private var categoryStringList: ArrayList<String> = arrayListOf()
    private var customerStringList: ArrayList<String> = arrayListOf()
    private var posProductModelList: ArrayList<ProductData> = arrayListOf()
    private var newPosProductModelList: ArrayList<ProductData> = arrayListOf()
    //    lateinit var posProductAdapter: PosProductAdapter
    private var posProdctMainArrayList: ArrayList<PosProductFilterModel> = arrayListOf()
    private var posProdctFilterArrayList: ArrayList<ProductData> = arrayListOf()
    private var total = 0.0
    lateinit var tvTotalValue: EditText

    private val ITEMCODE_SCAN = 12
    lateinit var dataBaseHelper: DatabaseHelper

    //    private var categoryProdcuctList: ArrayList<Products> = arrayListOf()
//    private var categoryFilterProdcuctList: ArrayList<Products> = arrayListOf()
//    private var categoryPassFilterProdcuctList: ArrayList<Products> = arrayListOf()
    private var ItemList: ArrayList<Items> = arrayListOf()
    private var NewItemList: ArrayList<Items> = arrayListOf()
    private var FinalItemList: ArrayList<Items> = arrayListOf()
    private var FinalProductList: ArrayList<ProductData> = arrayListOf()

    lateinit var databaseHelper: DatabaseHelper

    var bottomSheetDialogCategory: BottomSheetDialog? = null

    lateinit var category: String


//    private var posCategoryAdapter: PosCategoryAdapter =
//        PosCategoryAdapter(this, categoryFilterProdcuctList)

    private var posProductAdapter: PosProductAdapter =
        PosProductAdapter(this, ItemList,this)

//    private var productPosAdapter: ProductPosAdapter =
//        ProductPosAdapter(this, productList,this)

    lateinit var from: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pos_setting)


        val bundle = intent.extras

        if (bundle!=null) {
            etSearch.setText(bundle.getString("barcode").toString())
        }

        if(intent.extras != null) {
            from = intent.getStringExtra("from")!!
        }




        mContext = this
        initUI()
        mNetworkCallCategoryAPIFilter()
        addListner()

        if(from.equals("direct")) {
            rl_category.visibility = View.GONE
            rvProducts.visibility = View.GONE
            tvTotalValue.isEnabled = true
            tvTotalValue.isFocusable = true
        } else {
            rl_category.visibility = View.VISIBLE
            rvProducts.visibility = View.VISIBLE
            tvTotalValue.isEnabled = false
            tvTotalValue.isFocusable = false
        }
    }

    private fun initUI() {
        databaseHelper = DatabaseHelper(mContext)
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        ivBack = findViewById(R.id.ivBack)
        pbLoadData = findViewById(R.id.pbLoadData)
        tvDetails = findViewById(R.id.tvDetails)
        acUserName = findViewById(R.id.acUserName)
        acProduct = findViewById(R.id.acProduct)
        rvProducts = findViewById(R.id.rvProducts)
        rvCategory = findViewById(R.id.rvCategory)
        tvTotalValue = findViewById(R.id.tvTotalValue)


        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallGetUserAPI()
            mNetworkCallCategoryAPI()
//            mNetworkCallGetProductAPI()
        } else {
            Toast.makeText(
                mContext,
                mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addListner() {
        ivBack.setOnClickListener(this)
        tvDetails.setOnClickListener(this)


        tvUser.setOnClickListener {
            val intent = Intent(this,AddEditCustomerActivity::class.java)
            startActivity(intent)
        }

        ivBarCode.setOnClickListener {
            val intent = Intent(this,BarcodeScannerActivity::class.java)
            startActivityForResult(intent, ITEMCODE_SCAN)
        }

        acUserName.setOnTouchListener { v, event ->
            acUserName.showDropDown()
            false
        }

        acProduct.setOnTouchListener { v, event ->
            acProduct.showDropDown()
            false
        }

        acCategory.setOnClickListener {
            showCategoryBottomSheet()
        }

        ivAddBtn.setOnClickListener {
            startActivity(Intent(this,AddEditProductActivity::class.java))
        }


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.tvDetails -> {
                if(from.equals("direct")) {
                    if (userId.isEmpty()) {
                        Toast.makeText(mContext, "Please select customer", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }

                    var item = Items(
                        "","","","",00.00, 00.00,
                        00.00, 00.00, 00.00, 00.00,
                        00.00, "", 00.00, false
                    )

                    FinalItemList.add(item)

                    startActivity(
                        Intent(mContext, NewPosSaleActivity::class.java).putExtra(
                            "posProductModel", FinalItemList
                        ).putExtra("totalvalue", tvTotalValue.text.toString().toDouble()).putExtra("userId", userId).putExtra("from","direct")
                    )
                } else {
                    if (userId.isEmpty()) {
                        Toast.makeText(mContext, "Please select customer", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }

                    Log.e("Item size", posProductModelList.size.toString())


                    FinalProductList.clear()
                    Log.e("FinalProductList", posProductModelList.size.toString())

                    for (i in 0 until posProductModelList.size) {
                        Log.e("Quantity", posProductModelList.get(i).quantity.toString())

                        if (posProductModelList.get(i).quantity > 0) {
                            FinalProductList.add(posProductModelList.get(i))
                        }
                    }

                    FinalItemList.clear()

                    Log.e("FinalProductList.size", FinalProductList.size.toString())
                    for (i in 0 until FinalProductList.size) {

                        Log.e("Final Product Name", FinalProductList.get(i).name.toString())
                        Log.e("Final Product quantity", FinalProductList.get(i).quantity.toString())



                        if (databaseHelper.CheckOrderExists(
                                FinalProductList.get(i).xid,
                                FinalProductList.get(i).x_unit_id
                            ).toInt() > 0
                        ) {
                            var item = Items(
                                FinalProductList[i].item_id,
                                FinalProductList[i].xid,
                                FinalProductList[i].name,
                                FinalProductList[i].image_url,
                                FinalProductList[i].discount_rate.toDouble(),
                                FinalProductList[i].total_discount.toDouble(),
                                FinalProductList[i].total_tax,
                                FinalProductList[i].unit_price,
                                FinalProductList[i].single_unit_price,
                                FinalProductList[i].subtotal,
                                FinalProductList[i].quantity,
                                FinalProductList[i].x_unit_id,
                                FinalProductList[i].stock_quantity,
                                FinalProductList[i].isSelected
                            )

                            FinalItemList.add(item)

                        }
                    }
                    Log.e("FinalItemList.size", FinalItemList.size.toString())
                    startActivity(
                        Intent(mContext, NewPosSaleActivity::class.java).putExtra(
                            "posProductModel", FinalItemList
                        ).putExtra("totalvalue", total).putExtra("userId", userId).putExtra("from","product")
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ITEMCODE_SCAN) {
            if (resultCode === RESULT_OK){
                val extra: Bundle = data!!.getBundleExtra("extra")!!
                etSearch.setText(extra.getString("barcode").toString())
            }
        }
    }

    private fun mNetworkCallGetUserAPI() {
        userList.clear()
        userStringList.clear()
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getCustomerDropDown()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())

                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONArray("data")
                            userList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object : TypeToken<ArrayList<CustomerModel?>?>() {}.type
                                )
                            )
                            acUserName.setOnClickListener {
                                showCustomerBottomSheet()
                            }
//                            setAdapter(userList)
                        } else {
                            Toast.makeText(
                                mContext, jsonObject.optString("message"), Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            mContext,
                            mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject = JSONObject(response.errorBody()!!.string())
                        Util(mContext).logOutAlertDialog(mContext, JsonObject.optString("message"))
                    } catch (e: Exception) {

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun mNetworkCallCategoryAPI() {
        ItemList.clear()
//        categoryFilterProdcuctList.clear()
//        categoryStringList.clear()
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getAllCategories()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        Log.e("Response first", jsonObject.toString())

                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONObject("data")
                            val products = data.optJSONArray("products")

                            Log.e("My Product List",products.toString())

                            for (i in 0 until products.length()) {
                                val notifyObj = products.getJSONObject(i)
                                val itemsLisArray = notifyObj.getJSONArray("items")
                                for(j in 0 until itemsLisArray.length()) {
                                    val noti = itemsLisArray.getJSONObject(j)
                                    val userListModel = Gson()
                                        .fromJson(
                                            noti.toString(),
                                            Items::class.java
                                        )
                                    ItemList.add(userListModel)
//                                    ItemList.addAll(
//                                        Gson().fromJson(
//                                            itemsLisArray.toString(),
//                                            object : TypeToken<java.util.ArrayList<Products?>?>() {}.type
//                                        )
//                                    )
//                                    for (i in 0 until ItemList.size) {
//                                if (ItemList.get(i).item_id.size > 0) {
//                                        ItemList.add(ItemList.get(i))
//                                }
//                                    }
                                }

                            }



                            etSearch.addTextChangedListener(object : TextWatcher {
                                override fun beforeTextChanged(
                                    s: CharSequence?,
                                    start: Int,
                                    count: Int,
                                    after: Int
                                ) {
                                }

                                override fun onTextChanged(
                                    s: CharSequence,
                                    start: Int,
                                    before: Int,
                                    count: Int
                                ) {
                                    if (s.toString().length != 0) {
                                        filter(s.toString())
                                    } else {
                                        showAll()
                                    }
                                }

                                override fun afterTextChanged(s: Editable?) {}
                            })

                            databaseHelper.delAll(mContext)
                            setCategoryAdapter(ItemList)

                            if(!etSearch.text.toString().isNullOrEmpty())
                            {
                                filter(etSearch.text.toString())
                            }
                        } else {
                            Toast.makeText(
                                mContext, jsonObject.optString("message"), Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            mContext,
                            mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }


        })
    }

    private fun mNetworkCallCategoryAPIFilter(category: String) {
        ItemList.clear()
//        categoryFilterProdcuctList.clear()
//        categoryStringList.clear()
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getAllCategories()
        Log.e("category","inside category api")
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {

                    Log.e("category","inside response")
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        Log.e("Response Category", jsonObject.toString())

                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONObject("data")
                            val products = data.optJSONArray("products")

                            Log.e("products category",products.toString())

                            for (i in 0 until products.length()) {
                                val notifyObj = products.getJSONObject(i)
                                val itemsLisArray = notifyObj.getJSONArray("items")
                                for(j in 0 until itemsLisArray.length()) {
                                    val noti = itemsLisArray.getJSONObject(j)
                                    val userListModel = Gson()
                                        .fromJson(
                                            noti.toString(),
                                            Items::class.java
                                        )
                                    if(notifyObj.getString("name").equals(category)) {
                                        ItemList.add(userListModel)
                                    }
                                }

                            }

                            etSearch.addTextChangedListener(object : TextWatcher {
                                override fun beforeTextChanged(
                                    s: CharSequence?,
                                    start: Int,
                                    count: Int,
                                    after: Int
                                ) {
                                }

                                override fun onTextChanged(
                                    s: CharSequence,
                                    start: Int,
                                    before: Int,
                                    count: Int
                                ) {
                                    if (s.toString().length != 0) {
                                        filter(s.toString())
                                    } else {
                                        showAll()
                                    }
                                }

                                override fun afterTextChanged(s: Editable?) {}
                            })

                            databaseHelper.delAll(mContext)
                            setCategoryAdapter(ItemList)
//                            setProductAdapter(ItemList)
                        } else {
                            Toast.makeText(
                                mContext, jsonObject.optString("message"), Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            mContext,
                            mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else
                {
                    Log.e("category failed",response.toString())

                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }


        })
    }
    /* private fun mNetworkCallCategoryAPI(category: String) {
         categoryProdcuctList.clear()
         categoryFilterProdcuctList.clear()
 //        categoryStringList.clear()
         pbLoadData.visibility = View.VISIBLE
         val call = apiInterface.getAllCategories()
         call.enqueue(object : Callback<JsonObject> {
             override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                 if (response.isSuccessful) {
                     pbLoadData.visibility = View.GONE
                     if (response.code() == 200) {
                         val jsonObject = JSONObject(response.body().toString())
                         Log.e("Response", jsonObject.toString())

                         if (jsonObject.optBoolean("status")) {
                             val data = jsonObject.optJSONObject("data")
                             val products = data.optJSONArray("products")


                             for(i in 0 until products.length()) {
                                 val notifyObjJson = products.getJSONObject(i)
                                 Log.e("prodct name",notifyObjJson.getString("name")+"::"+category)
                                 if(notifyObjJson.getString("name").equals(category)) {
                                     val userListModel = Gson()
                                         .fromJson(
                                             notifyObjJson.toString(),
                                             Products::class.java
                                         )
                                     categoryProdcuctList.add(userListModel)
 //                                    categoryProdcuctList.addAll(
 //                                        Gson().fromJson(
 //                                            products.toString(),
 //                                            object :
 //                                                TypeToken<java.util.ArrayList<Products?>?>() {}.type
 //                                        )
 //                                    )
                                     for (i in 0 until categoryProdcuctList.size) {
                                         if (categoryProdcuctList.get(i).items.size > 0) {
                                             categoryFilterProdcuctList.add(categoryProdcuctList.get(i))
                                         }
                                     }
                                 }
                             }
                             databaseHelper.delAll(mContext)
                             setCategoryAdapter(ItemList)
                         } else {
                             Toast.makeText(
                                 mContext, jsonObject.optString("message"), Toast.LENGTH_SHORT
                             ).show()
                         }
                     } else {
                         Toast.makeText(
                             mContext,
                             mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                             Toast.LENGTH_SHORT
                         ).show()
                     }
                 }
             }

             override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                 pbLoadData.visibility = View.GONE
                 Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
             }

         })
     }
 */

    private fun mNetworkCallCategoryAPIFilter() {
        categoryList.clear()
//        categoryStringList.clear()
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getCategoryDropDown()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        Log.e("Category",jsonObject.toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONArray("data")
                            categoryList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<CategoryModel?>?>() {}.type
                                )
                            )
                            setCategoryAdapterFilter(categoryList)
                        } else {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            mContext,
                            mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject = JSONObject(response.errorBody()!!.string())
                        Util(mContext).logOutAlertDialog(mContext, JsonObject.optString("message"))
                    } catch (e: java.lang.Exception) {

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun setCategoryAdapter(ItemList: ArrayList<Items>) {
        rvCategory.layoutManager = GridLayoutManager(mContext,2)
        rvCategory.setHasFixedSize(true)
//        posCategoryAdapter = PosCategoryAdapter(mContext, categoryList)
        posProductAdapter = PosProductAdapter(mContext, ItemList,this)
        rvCategory.adapter = posProductAdapter
        rvProducts.adapter = posProductAdapter
        posProductAdapter.notifyDataSetChanged()
    }


    private fun setCategoryAdapterFilter(categoryList: ArrayList<CategoryModel>) {
        categoryStringList.add("All")
        for (i in 0 until categoryList.size) {
            categoryStringList.add(categoryList[i].name)
        }
//        val adapter: ArrayAdapter<String> =
//            ArrayAdapter<String>(this, R.layout.dropdown, categoryStringList)

//        acCategory.threshold = 0 //will start working from first character

//        acCategory.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

//        acCategory.setOnItemClickListener { parent, view, position, id ->
//            categoryId = categoryList[position].xid
//        }
    }


    private fun mNetworkCallGetProductAPI() {
        productList.clear()
        productStringList.clear()
        posProductModelList.clear()
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getPosProducts()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONObject("data")
                            Log.e("products data",data.toString())

                            val product = data.optJSONArray("products")
                            Log.e("products my pos",product.toString())
                            posProductModelList.addAll(
                                Gson().fromJson(
                                    product.toString(),
                                    object : TypeToken<java.util.ArrayList<ProductData?>?>() {}.type
                                )
                            )

                            Log.e("podProductModelList",posProductModelList.size.toString())
                            setProductAdapter(posProductModelList)
                        } else {
                            Toast.makeText(
                                mContext, jsonObject.optString("message"), Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            mContext,
                            mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun mNetworkCallGetProductXidAPI(category_id: String) {
        productList.clear()
        productStringList.clear()
        posProductModelList.clear()
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getPosProductsXid(category_id)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONObject("data")
                            Log.e("products data",data.toString())

                            val product = data.optJSONArray("products")
                            Log.e("products my pos",product.toString())
                            posProductModelList.addAll(
                                Gson().fromJson(
                                    product.toString(),
                                    object : TypeToken<java.util.ArrayList<ProductData?>?>() {}.type
                                )
                            )

                            setProductAdapter(posProductModelList)
                        } else {
                            Toast.makeText(
                                mContext, jsonObject.optString("message"), Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            mContext,
                            mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun setProductAdapter(productList: ArrayList<ProductData>) {
        rvProducts.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        rvProducts.setHasFixedSize(true)
//        productPosAdapter = ProductPosAdapter(mContext, productList, this)
        rvProducts.adapter = ProductPosAdapter(mContext, productList, this)
//        productPosAdapter.notifyDataSetChanged()

    }

    /*   override fun onItemClick(productData: ProductData, tvQuantity: TextView, tvPrice: TextView) {
           if (productData.quantity < productData.stock_quantity) {
               productData.quantity = productData.quantity + 1
               tvQuantity.text = productData.quantity.toString()
               val subtotal = (productData.quantity * productData.unit_price)
               productData.single_unit_price = subtotal
               // === productData.unit_price = subtotal
               tvPrice.text = subtotal.toString()
               productData.isSelected = true
               posProdctMainArrayList.add(
                   PosProductFilterModel(
                       productData.name,
                       productData.image_url,
                       "",
                       productData.xid,
                       productData.discount_rate,
                       productData.total_discount,
                       productData.x_tax_id,
                       productData.tax_type,
                       productData.tax_rate,
                       productData.total_tax,
                       "",
                       productData.unit_price,
                       productData.single_unit_price,
                       productData.subtotal,
                       productData.stock_quantity,
                       productData.quantity,
                       productData.isSelected
                   )
               )

               total += productData.unit_price
               tvTotalValue.setText(total.toString())


               posProductAdapter.notifyDataSetChanged()
           } else {
               Toast.makeText(mContext, "item stock is finished", Toast.LENGTH_SHORT).show()
               return

           }

       }*/

/*
    override fun onResume() {
        super.onResume()
        if (intent.extras != null) {
            posProductModelList =
                (intent.getSerializableExtra("posProductModel") as ArrayList<ProductData>?)!!
            total = intent.getDoubleExtra("totalvalue", 0.0)
            tvTotalValue.text = total.toString()
            Log.e("posProductModelList", "=" + posProductModelList)
            //  setProductAdapter(posProductModelList)
        } else {
            if (InternetConnection.checkConnection(mContext)) {

                //  mNetworkCallGetProductAPI()
            } else {
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.str_check_internet_connections),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }
*/


    override fun onResume() {
        super.onResume()

        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallGetUserAPI()
            mNetworkCallCategoryAPI()
            mNetworkCallGetProductAPI()
        } else {
            Toast.makeText(
                mContext,
                mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        val displaytotal: Double = databaseHelper.getTotalCartAmt(PrefManager(mContext))
        val sumTotal = DatabaseHelper.decimalformatData.format(displaytotal)
        total = sumTotal.toDouble()
        tvTotalValue.setText(total.toString())

//        if (posCategoryAdapter != null) {
//            posCategoryAdapter.notifyDataSetChanged()
//        }
        if (posProductAdapter != null) {
            posProductAdapter.notifyDataSetChanged()
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(mContext, MainActivity::class.java))
        finish()
    }

    override fun onItemClick(productData: Items, tvQuantity: TextView, tvPrice: TextView) {

        Log.e("Item 2","executed")

        productData.isSelected = true

        if (databaseHelper.CheckOrderExists(productData.xid, productData.x_unit_id)
                .toDouble() < productData.stock_quantity.toDouble()
        ) {
            productData.quantity++
            tvQuantity.text = databaseHelper.AddUpdateOrder(
                productData.xid,
                productData.x_unit_id,
                true,
                this,
                false,
                productData.single_unit_price.toDouble(),
                productData.name
            )
            productData.subtotal = productData.quantity * productData.single_unit_price
            val displaytotal: Double = databaseHelper.getTotalCartAmt(PrefManager(mContext))
            val sumTotal = DatabaseHelper.decimalformatData.format(displaytotal)
            total = sumTotal.toDouble()
            tvTotalValue.setText(total.toString())

//            posCategoryAdapter.notifyDataSetChanged()

            for( i in 0 until ItemList.size)
            {
                if(productData.xid.equals(ItemList.get(i).xid))
                {
                    var flagPresent : Boolean = false

                    for(j in 0 until NewItemList.size)
                    {
                        if(productData.xid.equals(NewItemList.get(j).xid))
                            flagPresent = true
                    }
                    if(!flagPresent)
                        NewItemList.add(ItemList.get(i))

                }

            }

        } else Toast.makeText(
            this, "Stock limit alert", Toast.LENGTH_SHORT
        ).show()
    }

    fun filter(text: String) {
        val temp: MutableList<Items> = ArrayList()
        for (d in ItemList) {
            if (d.name.contains(text, ignoreCase = true) || d.item_id.contains(text, ignoreCase = true) ) {
                temp.add(d)
            }
        }
        //update recyclerview
        posProductAdapter.updateList(temp)
    }

    fun showAll() {
        val temp: MutableList<Items> = ArrayList()
        for (d in ItemList) {
            temp.add(d)
        }
        //update recyclerview
        posProductAdapter.updateList(temp)
    }

    private fun showCategoryBottomSheet()
    {
        val view: View = layoutInflater.inflate(R.layout.layout_listview_bottomsheet, null)

        val  listAdapter = ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categoryStringList)
        view.lvCategory.adapter = listAdapter



        bottomSheetDialogCategory = BottomSheetDialog(this)
        bottomSheetDialogCategory!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        view.lvCategory.setOnItemClickListener { parent, view, position, id ->
            val element = listAdapter.getItem(position) // The item that was clicked

//            Toast.makeText(this,element,Toast.LENGTH_SHORT).show()

            acCategory.setText(element)
            category = element.toString()

//            if(acCategory.text.toString().equals("All")) {
//                mNetworkCallCategoryAPI()

            selectedCategoryId = getCategoryId(category)
            filterProducts(category)


//                Log.e("Category Test","Calling all category")
//            } else {
//
//                filterProducts("category")

//                mNetworkCallCategoryAPIFilter(category)
            Log.e("Category Test","Calling  category filter")

//            }


            Log.e("Cat",category)

            bottomSheetDialogCategory!!.dismiss()

        }
        bottomSheetDialogCategory!!.show()

    }


    private fun getCategoryId(category: String) : String
    {
        for ( i in 0 until categoryList.size)
        {
            if(categoryList[i].name.equals(category))
            {
                return categoryList[i].xid
            }
        }

        return "-1"
    }


    private  fun filterProducts(category: String)
    {

        newPosProductModelList.clear()


        Log.e("PosProductList Size",posProductModelList.size.toString())

        if(category.equals("All"))
        {
            mNetworkCallGetProductAPI()
        }
        else
        {
            Log.e("Selected Category Id",selectedCategoryId)


            mNetworkCallGetProductXidAPI(selectedCategoryId)

            //            for(i in 0 until posProductModelList.size)
//            {
//
//                Log.e("Product Category Id",posProductModelList.get(i).xid.toString())
//
//                if(posProductModelList[i].xid.equals(selectedCategoryId )){
//                    newPosProductModelList.add(posProductModelList[i])
//                }
//            }
//
        }


        val newAdapter = ProductPosAdapter(mContext,newPosProductModelList, this)
        rvProducts.adapter = newAdapter
        newAdapter.notifyDataSetChanged()
//        rvProducts.adapter = productPosAdapter
//        productPosAdapter.notifyDataSetChanged()
    }


    private fun showCustomerBottomSheet()
    {
        val view: View = layoutInflater.inflate(R.layout.layout_list_bottomsheet_customer, null)

        view.rvCustomer.apply {

            layoutManager = LinearLayoutManager(this@PosSettingActivity)
            customerListAdapter = CustomerListAdapter(
                context, userList, this@PosSettingActivity
            )
            view.rvCustomer.addItemDecoration(SimpleDividerItemDecoration(this@PosSettingActivity))
            view.rvCustomer.adapter = customerListAdapter
        }
        view.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable?) {
                filterCustomer(s.toString())
            }
        })


        bottomSheetDialogCategory = BottomSheetDialog(this,R.style.SheetDialog)
        bottomSheetDialogCategory!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)


        bottomSheetDialogCategory!!.show()

    }



    fun filterCustomer(text: String) {
        val temp: MutableList<CustomerModel> = ArrayList()
        for (d in userList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.name.contains(text, ignoreCase = true) || d.phone.contains(text, ignoreCase = true)) {
                temp.add(d)
            }
        }
        //update recyclerview
        customerListAdapter.updateList(temp)
    }

    override fun onClickAtOKButton(customerModel: CustomerModel) {
        if(customerModel != null) {
            acUserName.setText(customerModel.name)
            userId = customerModel.xid
            bottomSheetDialogCategory!!.dismiss()
        }
    }

    override fun onItemClick(productData: ProductData, tvQuantity: TextView, tvPrice: TextView) {

        Log.e("Item 1","executed")


        productData.isSelected = true

        if (databaseHelper.CheckOrderExists(productData.xid, productData.x_unit_id)
                .toDouble() < productData.stock_quantity.toDouble()
        ) {
            productData.quantity++
            tvQuantity.text = databaseHelper.AddUpdateOrder(
                productData.xid,
                productData.x_unit_id,
                true,
                this,
                false,
                productData.single_unit_price.toDouble(),
                productData.name
            )
            productData.subtotal = productData.quantity * productData.single_unit_price
            val displaytotal: Double = databaseHelper.getTotalCartAmt(PrefManager(mContext))
            val sumTotal = DatabaseHelper.decimalformatData.format(displaytotal)
            total = sumTotal.toDouble()
            tvTotalValue.setText(total.toString())

//            posCategoryAdapter.notifyDataSetChanged()

            Log.e("Item List Size",ItemList.size.toString())

            for( i in 0 until ItemList.size)
            {
                if(productData.xid.equals(ItemList.get(i).xid))
                {
                    var flagPresent : Boolean = false

                    for(j in 0 until NewItemList.size)
                    {
                        if(productData.xid.equals(NewItemList.get(j).xid))
                            flagPresent = true
                    }
                    if(!flagPresent)
                    {
                        NewItemList.add(ItemList.get(i))
                        Log.e("New Item List","Item Added")

                    }

                }

            }

        } else Toast.makeText(
            this, "Stock limit alert", Toast.LENGTH_SHORT
        ).show()
    }


}