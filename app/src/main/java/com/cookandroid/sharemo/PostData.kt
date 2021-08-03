package com.cookandroid.sharemo

import com.google.firebase.database.ServerValue
import java.sql.Timestamp
import java.util.*
import kotlin.collections.HashMap

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