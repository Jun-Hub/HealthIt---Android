package io.jun.healthit.billing

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.BillingProcessor.IBillingHandler
import com.anjlab.android.iab.v3.TransactionDetails
import io.jun.healthit.R

class BillingManager(
    private val activity: Activity,
    private val billingCallback: BillingCallback) : IBillingHandler {

    private val TAG = "BillingManager"

    var billingProcessor = BillingProcessor(
        activity,
        activity.getString(R.string.google_play_license_key),
        this)

    init { billingProcessor.initialize() }


    /**
     * 구독 또는 구매 완료시
     * @param productId 제품 아이디
     * @param details 거래 정보
     */
    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        Log.d(TAG, "onProductPurchased: $productId")
        billingProcessor.loadOwnedPurchasesFromGoogle() // 구매정보 업데이트
        billingCallback.onPurchased(productId)
    }

    override fun onPurchaseHistoryRestored() {
        // # 구매 복원 호출시 이 함수가 실행됩니다.
        billingProcessor.loadOwnedPurchasesFromGoogle()
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        // # 결제 오류시 따로 토스트 메세지를 표시하고 싶으시면 여기에 하시면됩니다.
        billingCallback.onBillingError()
        Log.d(TAG, "onBillingError")
    }

    /**
     * BillingProcessor 초기화 완료시
     */
    override fun onBillingInitialized() {
        val subDetails =
            billingProcessor.getSubscriptionListingDetails(activity.getString(R.string.sku_subs)) // 1개월 구독 정보
        
        // # SkuDetails.priceValue: ex) 1,000원일경우 => 1000.00
        billingCallback.onUpdatePrice(subDetails.priceValue)
        
        billingProcessor.loadOwnedPurchasesFromGoogle() // 구매정보 업데이트
    }

    /**
     * 인앱 상품 구매하기
     */
    /*fun purchase() {
        if (billingProcessor.isInitialized) {
            if (billingProcessor.isSubscribed(activity.getString(R.string.sku_subs))) {
                Toast.makeText(activity,
                    "이미 광고 제거 상품을 구독중입니다. 이중 결제 방지를 위해 구독이 끝나면 PRO 버전을 구매 해 주십시오.",
                    Toast.LENGTH_LONG).show()
            } else {
                billingProcessor.purchase(activity, activity.getString(R.string.sku_inapp))
            }
        }
    }*/

    /**
     * 구독하기
     */
    fun subscribe() {
        if (billingProcessor.isInitialized &&
            !billingProcessor.isSubscribed(activity.getString(R.string.sku_subs))
        ) {
            billingProcessor.subscribe(activity, activity.getString(R.string.sku_subs))
        }
    }

    private fun onResume() {
        // # SharedPreference에 구매 여부를 저장 해 두고, 그에 따라 광고를 바로 숨기거나 보여주는 코드입니다.
        billingProcessor.loadOwnedPurchasesFromGoogle()
        // # PRO 버전 구매를 했거나 구독을 했다면!
        //storage.setPurchasedProVersion(billingProcessor.isPurchased(Setting.SKU) || billingProcessor.isSubscribed(Setting.SUBSCRIBE_SKU))
        // # 안에는 대충 이런 코드입니다. purchased ? adView.setVisibility(View.GONE) : View.VISIBLE
        //if (activity.mAdLoader != null) activity.mAdLoader.update()
    }

    fun onDestroy() {
        // # 꼭! 릴리즈 해주세요.
        billingProcessor.release()
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return billingProcessor.handleActivityResult(requestCode, resultCode, data)
    }

}