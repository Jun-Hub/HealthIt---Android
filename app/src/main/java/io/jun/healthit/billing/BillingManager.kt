package io.jun.healthit.billing

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.BillingProcessor.IBillingHandler
import com.anjlab.android.iab.v3.TransactionDetails
import io.jun.healthit.R

class BillingManager(
    private val activity: Activity,
    private val billingCallback: BillingCallback?) : IBillingHandler {

    var billingProcessor = BillingProcessor(
        activity,
        activity.getString(R.string.google_play_license_key),
        this)

    init { billingProcessor.initialize() }

    /*
    - 변수 및 커스텀 클래스 참조
    Setting.GP_LICENSE_KEY: 구글 플레이 라이센스 키 (비밀!)
    Setting.SKU: 광고제거용 PRO 버전 (관리되는 제품)
    Setting.SUBSCRIBE_SKU: 1개월 광고제거 (구독 상품)
    AdLoader(context.mAdLoader): 구글 애드몹 광고를 보여주기 위해 제가 만든 클래스
    AppStorage: SharedPreference를 쓰기 쉽게 제가 만든 클래스 -> 간단하게 현재 구독 및 결제 상태를 저장하기 위해 사용
     */


    /**
     * 구독 또는 구매 완료시
     * @param productId 제품 아이디
     * @param details 거래 정보
     */
    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        billingProcessor.loadOwnedPurchasesFromGoogle() // 구매정보 업데이트
        billingCallback?.onPurchased(productId)

        onResume()
    }

    override fun onPurchaseHistoryRestored() {
        // # 구매 복원 호출시 이 함수가 실행됩니다.
        showToast("onPurchaseHistoryRestored")
        onResume()
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        // # 결제 오류시 따로 토스트 메세지를 표시하고 싶으시면 여기에 하시면됩니다.
        showToast("onBillingError")
    }

    /**
     * BillingProcessor 초기화 완료시
     */
    override fun onBillingInitialized() {
        val details =
            billingProcessor.getPurchaseListingDetails(activity.getString(R.string.sku_inapp)) // PRO 버전 정보
        val subDetails =
            billingProcessor.getSubscriptionListingDetails(activity.getString(R.string.sku_subs)) // 1개월 구독 정보
        
        // # SkuDetails.priceValue: ex) 1,000원일경우 => 1000.00
        val pricePair = Pair(details.priceValue, subDetails.priceValue)
        billingCallback?.onUpdatePrice(pricePair)
        
        billingProcessor.loadOwnedPurchasesFromGoogle() // 구매정보 업데이트

        onResume()
    }

    /**
     * 인앱 상품 구매하기
     */
    fun purchase() {
        if (billingProcessor.isInitialized) {
            if (billingProcessor.isSubscribed(activity.getString(R.string.sku_subs))) {
                Toast.makeText(activity,
                    "이미 광고 제거 상품을 구독중입니다. 이중 결제 방지를 위해 구독이 끝나면 PRO 버전을 구매 해 주십시오.",
                    Toast.LENGTH_LONG).show()
            } else {
                billingProcessor.purchase(activity, activity.getString(R.string.sku_inapp))
            }
        }
    }

    /**
     * 구독하기
     */
    fun subscribe() {
        if (billingProcessor.isInitialized) {
            // # 저는 PRO 버전도 같이 팔고있기 때문에 중복 구입 방지를 위해 구매여부 체크를 해두었습니다.
            if (!billingProcessor.isPurchased(activity.getString(R.string.sku_inapp)) &&
                !billingProcessor.isSubscribed(activity.getString(R.string.sku_subs))) {

                billingProcessor.subscribe(activity, activity.getString(R.string.sku_subs))
            }
        }
    }

    fun onResume() {
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

    private fun showToast(str: String) {
        Toast.makeText(activity, str, Toast.LENGTH_SHORT).show()
    }

}