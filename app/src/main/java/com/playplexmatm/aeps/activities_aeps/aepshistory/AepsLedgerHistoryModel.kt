package com.payplex.aeps.activities_aeps.aepshistory

import java.io.Serializable

data class AepsLedgerHistoryModel(
    val aeps_txn_id: String,
    val aeps_txn_agentid: String,
    val aeps_txn_recrefid: String,
    val aeps_txn_opbal: String,
    val aeps_txn_crdt: String,
    val aeps_txn_dbdt: String,
    val aeps_txn_clbal: String,
    val aeps_txn_type: String,
    val aeps_txn_date: String
) : Serializable