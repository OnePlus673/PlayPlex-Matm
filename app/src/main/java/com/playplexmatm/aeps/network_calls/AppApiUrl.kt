package com.playplexmatm.aeps.network_calls

import com.playplexmatm.util.AppConstants


object AppApiUrl : AppConstants {
    //Production Environment
     var BASE_URL = "http://aeps.payplex.org.in/index.php"
      //var BASE_URL = "http://edigitalvillage.net/index.php"

    var OFFERS_URL = BASE_URL
    var IMAGE_URL = "http://aeps.payplex.org.in"

    //API URLS

    //GENERAL APP USER URLS

    //url =/applogin/
    val LOGIN_BY_PASSWORD: String = "$BASE_URL/applogin/userLogin"
    val REGISTER_USER: String = "$BASE_URL/applogin/register"
    val SEND_REGISTER_OTP: String = "$BASE_URL/applogin/sendRegisterOtp"
    val GETFORGOTOTP: String = BASE_URL + "/applogin/getpassotp"
    val FORGOT_PASS: String = BASE_URL + "/applogin/forgetPass"
    val VERIFY_MOBILE: String = BASE_URL + "/applogin/verifyMobile"

    // url =/appapi/
    val GET_AEPS_BALANCE: String = "$BASE_URL/appapi/aepsBalance"
    val GET_DASHBOARD: String = "$BASE_URL/appapi/dashboard"
    val GET_LOGIN_DETAILS: String = "$BASE_URL/appapi/getMicroAtmLoginDetails"
    val UPDATE_LOGIN_DETAILS: String = "$BASE_URL/appapi/updateMicroAtmLoginPassword"
    val GET_BALANCE: String = "$BASE_URL/appapi/walletBalance"
    val GET_PROFILE: String = "$BASE_URL/appapi/profile"
    val GET_OPERATORS: String = "$BASE_URL/appapi/getOperators"
    val CHECK_IF_SAME_RECHARGE: String = "$BASE_URL/appapi/checkIfSameRecharge"
    val VERIFY_PIN: String = "$BASE_URL/appapi/verifyPin"
    val RECHARGE_HISTORY: String = "$BASE_URL/appapi/rechargeHistoryFromTo"

    //*********************Do Not Copy*********************************//

    val CHANGE_PIN: String = BASE_URL + "/appapi/changepin"
    val FUND_CREDIT: String = BASE_URL + "/appapi/viewcreditwallet"
    val FUND_DEBIT: String = BASE_URL + "/appapi/viewdebitwallet"
    val CHECKSAME_FUNDTRANSFER: String = BASE_URL + "/appapi/checkifsamefundtransfer"
    val LEDGER_REPORT: String = BASE_URL + "/appapi/ledgerfromto"
    val COMMISION_REPORT_URL: String = BASE_URL + "/appapi/getcommslab"
    val BROWSE_PLANS = OFFERS_URL + "appapi/roffer"
    val BROWSE_PLANS_DTH = OFFERS_URL + "/appapi/Dthinfo"
    val DISSPUTE_HISTORY: String = BASE_URL + "/appapi/disputehistory"
    val NEWUSER_URL: String = BASE_URL + "/appapi/create_retailer_api"
    val NEW_DISTRIBUTOR_URL: String = BASE_URL + "/appapi/create_distributor_api"
    val USER_LIST: String = BASE_URL + "/appapi/user_list"
    val GET_USER_ID: String = BASE_URL + "/appapi/getcusid"
    val FUND_TRANSFER: String = BASE_URL + "/appapi/direct_credit"
    val RAISE_DISPUTE: String = BASE_URL + "/appapi/submitdispute"
    val FUND_REQUEST_URL: String = BASE_URL + "/appapi/fundreq"
    val GET_SUPPORT: String = BASE_URL + "/appapi/support"
    val USER_DAYBOOK: String = BASE_URL + "/appapi/userdaybook"
    val UPDATE_WALLET: String = BASE_URL + "/appapi/add_wallet_balance"
    val GET_UPIDETAILS: String = BASE_URL + "/appapi/getupidetails"
    val LOGOUT_USER: String = BASE_URL + "/appapi/userlogout"
    val GETPINOTP: String = BASE_URL + "/appapi/getpinotp"
    val FORGETPIN: String = BASE_URL + "/appapi/forgetpin"
    val FUND_MYREQUEST: String = BASE_URL + "/appapi/viewmyfundreq"
    val USER_SEARCH: String = BASE_URL + "/appapi/user_list_byname_or_mobile"
    val CHANGE_PASWORD: String = BASE_URL + "/appapi/changepassword"
    val RECHARGE_HISTORY_BY_MOBILE: String = BASE_URL + "/appapi/rechargehistorybymobile"
    val RECHARGE_HISTORY_BY_DATE: String = BASE_URL + "/appapi/rechargehistorybydate"
    val GET_OPERATORS_API: String = BASE_URL + "/appapi/getoperator"
    val GET_DYNAMIC_QR_CODE: String =
        BASE_URL + "/appapi/getDynamicQrCode"

    //DMT APIS
    val DMT_LOGIN: String =
        BASE_URL + "/dmt/verifySender"
    val DMT_SENDOTP: String =
        BASE_URL + "/dmt/verifySenderOtp"
    val DMT_REGISTER: String =
        BASE_URL + "/dmt/registerSender"
    val DMT_ADD_BENFICIARY: String =
        BASE_URL + "/dmt/addBeneficiary"
    val DMT_VIEW_BENIFICIARY: String =
        BASE_URL + "/dmt/getBeneficiary"
    val DELETE_RECIPIENT: String =
        BASE_URL + "/dmt/deleteBeneficiary"
    val GET_CHARGE: String =
        BASE_URL + "/aeps/getCharge"
    val DMT_TRANSACTION: String =
        BASE_URL + "/dmt/moneyTransfer"
    val DMT_HISTORY: String =
        BASE_URL + "/dmt/getDmtHistory"
    val CHECK_STATUS: String =
        BASE_URL + "/dmt/checkStatus"

    val DMT_BANK_LIST: String =
        BASE_URL + "/dmt/getBankNames"

    val VERIFY_BANK: String =
        BASE_URL + "/dmt/accountValidation"
    val DMT_COMMISIONSLAB_URL: String =
        BASE_URL + "/dmt/dmt_commission_slab"


    //MicroAtm
    val GET_MERCHANT_CATEGORY: String =
        BASE_URL + "/appapi/getMerchantCategory"

    val VERIFY_KYC_MATM: String = "$BASE_URL/appapi/onboardMerchant"

    val UPDATE_KYC_MATM: String = "$BASE_URL/appapi/updateMerchantOnboardingDetails"


    val GET_PINCODE: String = "$BASE_URL/appapi/getPincodes"
    val GET_IFSC: String = "$BASE_URL/appapi/getIfsc"
    val GET_ALL_IFSC: String = "$BASE_URL/appapi/getAllIfsc"

    val MICRO_ATM_ONBOARDING_STATUS: String =
        BASE_URL + "/appapi/getMerchantOnboardingStatus"

    val FETCH_ONBOARDING_DETAILS: String =
        BASE_URL + "/appapi/fetchOnboardingDetails"


    val MICROATM_HISTORY: String =
        BASE_URL + "/aeps/microatm_history"


    val MICROATM_COMMISIONSLAB_URL: String =
        BASE_URL + "/aeps/microatm_commission_slab"


    //POS

    val POS_HISTORY: String =
        BASE_URL + "/aeps/pos_history"


    val POS_COMMISIONSLAB_URL: String =
        BASE_URL + "/aeps/pos_commission_slab"


    //AEPS
    val GET_AEPS_CHARGE: String =
        BASE_URL + "/aeps/getCharge"
    val AEPS_HISTORY: String =
        BASE_URL + "/aeps/aeps_history"
    val AEPS_COMMISIONSLAB_URL: String =
        BASE_URL + "/aeps/aeps_commission_slab"

    val AEPS_BANK_LIST: String =
        BASE_URL + "/aeps/getbank"

    val DUMMY_PID: String =
        BASE_URL + "/aeps/getPidData"

    val AEPS_TRANSACTION: String =
        BASE_URL + "/aeps/aepsapi"

    val AEPS_PAYOUT: String =
        BASE_URL + "/aeps/submitPayout"

    val GET_PAYOUT_DETAILS: String =
        BASE_URL + "/aeps/getPayoutDetails"

    val AEPSPAYOUT_HISTORY: String =
        BASE_URL + "/aeps/payoutHistory"

    val AEPSCOMMISSION_HISTORY: String =
        BASE_URL + "/aeps/aeps_history"

    val AEPS_LEDGER_HISTORY: String =
            BASE_URL + "/aeps/aeps_ledger"

    val STATE_LIST: String = "$BASE_URL/aeps/getstate"

    val VERIFY_KYC: String = "$BASE_URL/aeps/onboarding"

    val EKYC: String =
        BASE_URL + "/aeps/ekycsubmit"

    val VALIDATE_EKYC_OTP: String =
        BASE_URL + "/aeps/validateekycotp"

    val RESEND_EKYC_OTP: String =
        BASE_URL + "/aeps/resendekycotp"

    val UPDATE_AEPS_BANK: String =
        BASE_URL + "/aeps/updateAepsBank"

    //MICRO ATM
    val MICRO_ATM_TRANSACTION: String =
        BASE_URL + "/aeps/submitMicroAtmResponse"

    val MICRO_ATM_LOGIN: String =
        BASE_URL + "/aeps/microAtmDetails"





    //******************************************************************//

    // url =rechargeapi/recharge
    val RECHARGE: String = "$BASE_URL/rechargeapi/recharge"

    val CIRCLE_API: String = BASE_URL + "/appapi/getCircle"

    val BILLPAY: String = "$BASE_URL/rechargeapi/billpay"

    val GETBILLDETAILS = OFFERS_URL + "/appapi/ElectricityInfo"

}