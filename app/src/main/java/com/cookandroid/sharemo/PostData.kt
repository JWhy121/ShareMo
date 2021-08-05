package com.cookandroid.sharemo

//포스트 내용을 담는 모델
data class PostData(
        var uid : String? = null,
        var content : String? = null,
        var nickname : String? = null,
        var price : String? = null,
        var dong : String? = null,
        var website : String? = null,
        var item : String? = null,
        var timstamp : String? = null,
        var imgUrl : String? = null
)