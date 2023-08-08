package com.sg.swapnapay.model

data class CommisionSlabModel(
    val scheme_id: String,
    val slab: String,
    val type: String,
    val retailer_comm: String,
    val distributor_comm: String,
    val master_comm: String
)