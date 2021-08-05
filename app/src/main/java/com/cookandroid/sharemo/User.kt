package com.cookandroid.sharemo

//유저 정보를 담는 모델
data class User(val user_uid : String? = null,
                val user_email: String? = null,
                val user_name : String? = null,
                var user_phone : String? = null,
                var user_dong : String? = null,
                var user_profileImage : String? = null,
                var user_nickname :String? = null)
