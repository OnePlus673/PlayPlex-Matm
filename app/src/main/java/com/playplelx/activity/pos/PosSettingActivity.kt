package com.playplelx.activity.pos

import android.content.ClipData.Item
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplelx.R
import com.playplelx.activity.MainActivity
import com.playplelx.adapter.PosCategoryAdapter
import com.playplelx.adapter.PosProductAdapter
import com.playplelx.model.category.CategoryModel
import com.playplelx.model.categoryofproducts.Items
import com.playplelx.model.categoryofproducts.Products
import com.playplelx.model.customers.CustomerModel
import com.playplelx.model.posproducts.PosProductFilterModel
import com.playplelx.model.posproducts.ProductData
import com.playplelx.model.productfilter.ProductFilterModel
import com.playplelx.network.ApiInterface
import com.playplelx.network.Apiclient
import com.playplelx.util.DatabaseHelper
import com.playplelx.util.InternetConnection
import com.playplelx.util.PrefManager
import com.playplelx.util.Util
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PosSettingActivity : AppCompatActivity(), View.OnClickListener, PosProductAdapter.onClick {

    lateinit var mContext: PosSettingActivity
    lateinit var ivBack: ImageView
    lateinit var apiInterface: ApiInterface
    lateinit var pbLoadData: ProgressBar
    lateinit var tvDetails: TextView
    lateinit var acUserName: AutoCompleteTextView

    private var userList: ArrayList<CustomerModel> = arrayListOf()
    private var userStringList: ArrayList<String> = arrayListOf()
    private var userId: String = ""
    lateinit var acProduct: AutoCompleteTextView

    private var productList: ArrayList<ProductData> = arrayListOf()
    private var productStringList: ArrayList<String> = arrayListOf()
    private var filterProductList: ArrayList<ProductFilterModel> = arrayListOf()
    lateinit var rvProducts: RecyclerView
    lateinit var rvCategory: RecyclerView
    private var categoryList: ArrayList<CategoryModel> = arrayListOf()
    private var categoryStringList: ArrayList<String> = arrayListOf()
    private var posProductModelList: ArrayList<ProductData> = arrayListOf()
    lateinit var posProductAdapter: PosProductAdapter
    lateinit var posCategoryAdapter: PosCategoryAdapter
    private var posProdctMainArrayList: ArrayList<PosProductFilterModel> = arrayListOf()
    private var posProdctFilterArrayList: ArrayList<ProductData> = arrayListOf()
    private var total = 0.0
    lateinit var tvTotalValue: TextView

    private var categoryProdcuctList: ArrayList<Products> = arrayListOf()
    private var categoryFilterProdcuctList: ArrayList<Products> = arrayListOf()
    private var categoryPassFilterProdcuctList: ArrayList<Products> = arrayListOf()
    private var ItemList: ArrayList<Items> = arrayListOf()
    lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pos_setting)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        supportActionBar!!.hide()
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


        acUserName.setOnTouchListener { v, event ->
            acUserName.showDropDown()
            false
        }

        acProduct.setOnTouchListener { v, event ->
            acProduct.showDropDown()
            false
        }


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.tvDetails -> {
                if (userId.isEmpty()) {
                    Toast.makeText(mContext, "Please select user", Toast.LENGTH_SHORT).show()
                    return
                }
                /*  posProdctFilterArrayList.clear()
                  for (i in 0 until posProductModelList.size) {
                      if (posProductModelList[i].isSelected) {
                          posProdctFilterArrayList.add(
                              ProductData(
                                  "",
                                  posProductModelList[i].xid,
                                  posProductModelList[i].name,
                                  posProductModelList[i].image,
                                  posProductModelList[i].image_url,
                                  posProductModelList[i].discount_rate,
                                  posProductModelList[i].total_discount,
                                  posProductModelList[i].x_tax_id,
                                  posProductModelList[i].tax_type,
                                  posProductModelList[i].tax_rate,
                                  posProductModelList[i].total_tax,
                                  "",
                                  posProductModelList[i].unit_price,
                                  posProductModelList[i].single_unit_price,
                                  posProductModelList[i].subtotal,
                                  posProductModelList[i].quantity,
                                  posProductModelList[i].stock_quantity,
                                  "",
                                  posProductModelList[i].isSelected
                              )
                          )
                      }
                  }*/

                categoryPassFilterProdcuctList.clear()
                ItemList.clear()
                for (i in 0 until categoryFilterProdcuctList.size) {
                    for (j in 0 until categoryFilterProdcuctList.get(i).items.size) {
                        if (categoryFilterProdcuctList.get(i).items.get(j).isSelected) {
                            ItemList.add(
                                Items(
                                    "",
                                    categoryFilterProdcuctList.get(i).items.get(j).xid,
                                    categoryFilterProdcuctList.get(i).items.get(j).name,
                                    categoryFilterProdcuctList.get(i).items.get(j).image_url,
                                    0.0,
                                    0.0,
                                    0.0,
                                    categoryFilterProdcuctList.get(i).items.get(j).unit_price,
                                    categoryFilterProdcuctList.get(i).items.get(j).single_unit_price,
                                    categoryFilterProdcuctList.get(i).items.get(j).subtotal,
                                    categoryFilterProdcuctList.get(i).items.get(j).quantity,
                                    categoryFilterProdcuctList.get(i).items.get(j).x_unit_id,
                                    categoryFilterProdcuctList.get(i).items.get(j).stock_quantity,
                                    categoryFilterProdcuctList.get(i).items.get(j).isSelected,
                                )
                            )
                            categoryPassFilterProdcuctList.add(
                                Products(
                                    categoryFilterProdcuctList.get(i).xid,
                                    categoryFilterProdcuctList.get(i).name,
                                    ItemList
                                )
                            )
                        }
                    }
                }


                startActivity(
                    Intent(mContext, NewPosSaleActivity::class.java).putExtra(
                        "posProductModel", ItemList
                    ).putExtra("totalvalue", total).putExtra("userId", userId)
                )

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

                            setAdapter(userList)
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

    private fun setAdapter(userList: ArrayList<CustomerModel>) {
        for (i in 0 until userList.size) {
            userStringList.add(userList[i].name)
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, userStringList)

        acUserName.threshold = 0 //will start working from first character

        acUserName.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

        acUserName.setOnItemClickListener { parent, view, position, id ->
            userId = userList[position].xid
        }
    }


    private fun mNetworkCallCategoryAPI() {
        categoryProdcuctList.clear()
        categoryFilterProdcuctList.clear()
        categoryStringList.clear()
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getAllCategories()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONObject("data")
                            val products = data.optJSONArray("products")


                            categoryProdcuctList.addAll(
                                Gson().fromJson(
                                    products.toString(),
                                    object : TypeToken<java.util.ArrayList<Products?>?>() {}.type
                                )
                            )
                            for (i in 0 until categoryProdcuctList.size) {
                                if (categoryProdcuctList.get(i).items.size > 0) {
                                    categoryFilterProdcuctList.add(categoryProdcuctList.get(i))
                                }
                            }

                            databaseHelper.delAll(mContext)
                            setCategoryAdapter(categoryFilterProdcuctList)
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

    private fun setCategoryAdapter(categoryList: ArrayList<Products>) {
        rvCategory.layoutManager = LinearLayoutManager(mContext)
        rvCategory.setHasFixedSize(true)
        posCategoryAdapter = PosCategoryAdapter(mContext, categoryList)
        rvCategory.adapter = posCategoryAdapter
        posCategoryAdapter.notifyDataSetChanged()
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
                            val product = data.optJSONArray("products")
                            posProductModelList.addAll(
                                Gson().fromJson(
                                    product.toString(),
                                    object : TypeToken<java.util.ArrayList<ProductData?>?>() {}.type
                                )
                            )

                            //  setProductAdapter(posProductModelList)
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

/*
    private fun setProductAdapter(productList: ArrayList<ProductData>) {
        rvProducts.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        rvProducts.setHasFixedSize(true)
        posProductAdapter = PosProductAdapter(mContext, productList, this)
        rvProducts.adapter = posProductAdapter
        posProductAdapter.notifyDataSetChanged()


    }
*/

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

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(mContext, MainActivity::class.java))
        finish()
    }

    override fun onItemClick(productData: Items, tvQuantity: TextView, tvPrice: TextView) {

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
            tvTotalValue.text = total.toString()


            categoryFilterProdcuctList




            posCategoryAdapter.notifyDataSetChanged()

        } else Toast.makeText(
            this, "Stock limit alert", Toast.LENGTH_SHORT
        ).show()
    }
}