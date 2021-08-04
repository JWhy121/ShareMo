package com.cookandroid.sharemo

//채팅방 모델
data class ChatData(var senderUid:String? = null,
                    var receiverUid:String? = null,
                    var message:String? = null,
                    var senderNickname:String? = null,
                    var receiverNickname:String? = null)