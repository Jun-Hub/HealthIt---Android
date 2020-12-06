package io.jun.healthit.billing

interface BillingCallback {
    // # 원하는 함수가있다면 추가, 필요없다면 제거하기
    fun onPurchased(productId: String?) // # 구매가 정상적으로 완료되었을때 해당 제품 아이디를 넘겨줍니다.
    fun onUpdatePrice(prices: Pair<Double?, Double?>?) // # 화면에 가격을 표시하고 싶으므로 가격 정보를 넘겨줍니다.
    fun onBillingError()
}