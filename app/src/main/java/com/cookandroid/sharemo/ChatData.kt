package com.cookandroid.sharemo

//채팅을 보내는 사람, 받는 사람, 채팅 메시지를 저장
data class ChatData(var senderUid:String? = "", var receiverUid:String? = "", var message:String? = "")
