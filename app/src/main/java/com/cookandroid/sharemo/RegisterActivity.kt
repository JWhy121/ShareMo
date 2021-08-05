package com.cookandroid.sharemo

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.regex.Pattern

/*회원 가입 화면*/
class RegisterActivity : AppCompatActivity() {

    private var mFirebaseAuth : FirebaseAuth? = null //파이어베이스 인증
    private lateinit var mDatabaseRef : DatabaseReference //실시간 데이터베이스

    //위젯 연결할 변수 선언
    lateinit var edt_email : EditText
    lateinit var edt_pwd : EditText
    lateinit var edt_name : EditText
    lateinit var edt_phone : EditText
    lateinit var edt_nickname : EditText

    lateinit var btn_register : Button
    lateinit var btn_back : Button
    lateinit var btn_confirmId : Button
    lateinit var btn_confirmNickname : Button
    lateinit var btn_reset : Button

    private var spinnerCity: Spinner? = null
    private  var spinnerSigungu:Spinner? = null
    private  var spinnerDong:Spinner? = null
    private var arrayAdapter: ArrayAdapter<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mFirebaseAuth = FirebaseAuth.getInstance()

        //위젯 연결
        edt_email = findViewById(R.id.edt_Email)
        edt_pwd = findViewById(R.id.edt_Pwd)
        edt_name = findViewById(R.id.edt_Name)
        edt_phone = findViewById(R.id.edt_Phone)
        edt_nickname = findViewById(R.id.edt_Nickname)

        btn_register = findViewById(R.id.btn_Register)
        btn_back = findViewById(R.id.btn_Back)
        btn_confirmId = findViewById(R.id.btn_ConfirmID)
        btn_confirmNickname = findViewById(R.id.btn_ConfirmNickname)
        btn_reset = findViewById(R.id.btn_Reset)

        spinnerCity = findViewById<Spinner>(R.id.spn_City)

        spinnerSigungu = findViewById<Spinner>(R.id.spn_Prov)
        spinnerDong = findViewById<Spinner>(R.id.spn_Dong)

        //동네 스피너 어댑터 설정
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.spinner_region) as Array<String>)
        arrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCity!!.setAdapter(arrayAdapter)

        var pattern : Pattern = android.util.Patterns.EMAIL_ADDRESS

        initAddressSpinner()

        //처음에는 이메일 입력창 제외하고 모두 입력 불가능하게 설정
        edt_pwd.isEnabled = false
        edt_name.isEnabled = false
        edt_phone.isEnabled = false
        edt_nickname.isEnabled = false
        btn_register.isEnabled = false

        //이메일 중복 확인 버튼
        btn_confirmId.setOnClickListener {
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo").child("UserAccount")
            mDatabaseRef.orderByChild("user_email").equalTo("${edt_email.text.toString()}")
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    var value = snapshot.getValue()
                    if(!pattern.matcher(edt_email.text.toString()).matches()){
                        Toast.makeText(this@RegisterActivity,"이메일 형식으로 입력하세요",Toast.LENGTH_SHORT).show()
                    }else if(value != null){
                        Toast.makeText(this@RegisterActivity,"이미 가입되어 있는 아이디입니다",Toast.LENGTH_SHORT).show()
                    }else{
                        //가입 가능한 아이디이면 이메일 입력 창 비활성화 후 나머지 창 활성화
                        Toast.makeText(this@RegisterActivity,"사용 가능한 아이디입니다",Toast.LENGTH_SHORT).show()
                        edt_email.isEnabled = false
                        edt_pwd.isEnabled = true
                        edt_name.isEnabled = true
                        edt_phone.isEnabled = true
                        edt_nickname.isEnabled = true
                    }
                }
            })
        }

        //닉네임 중복 확인 버튼
        btn_confirmNickname.setOnClickListener {
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo").child("UserAccount")
            mDatabaseRef.orderByChild("user_nickname").equalTo("${edt_nickname.text.toString()}")
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {

                        }
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var value = snapshot.getValue()
                            if(edt_nickname.text.toString().equals("")){
                                Toast.makeText(this@RegisterActivity,"닉네임을 입력하세요",Toast.LENGTH_SHORT).show()
                            }else if(value != null){
                                Toast.makeText(this@RegisterActivity,"이미 등록되어 있는 닉네임입니다",Toast.LENGTH_SHORT).show()
                            }else{
                                //가입 가능한 닉네임이면 닉네임 입력 창 비활성화 및 가입하기 버튼 활성화
                                Toast.makeText(this@RegisterActivity,"사용 가능한 닉네임입니다",Toast.LENGTH_SHORT).show()
                                edt_nickname.isEnabled = false
                                btn_register.isEnabled = true
                            }
                        }
                    })
        }

        //재설정 버튼 클릭 시 다시 처음 세팅으로 초기화
        btn_reset.setOnClickListener {

            edt_email.setText("")
            edt_pwd.setText("")
            edt_name.setText("")
            edt_phone.setText("")
            edt_nickname.setText("")

            edt_email.isEnabled = true
            edt_pwd.isEnabled = false
            edt_name.isEnabled = false
            edt_phone.isEnabled = false
            edt_nickname.isEnabled = false
            btn_register.isEnabled = false
        }

        btn_back.setOnClickListener {
            onBackPressed()
        }

        //회원가입 처리 시작
        btn_register.setOnClickListener {

            var user_email : String = edt_email.text.toString()
            var user_nickname : String = edt_nickname.text.toString()
            var user_pwd : String = edt_pwd.text.toString()
            var user_name : String = edt_name.text.toString()
            var user_phone : String = edt_phone.text.toString()

            //모든 항목을 작성해야 회원가입 가능하게 함
            if(user_name.equals("") || user_pwd.equals("") || user_phone.equals("") || user_nickname.equals("")){
                Toast.makeText(this, "가입 정보를 모두 입력하세요", Toast.LENGTH_SHORT).show()
            }else{
                //파이어베이스에 유저를 등록하고, 리얼타임 데이터베이스에 사용자 정보들을 setValue로 넣어줌
                mFirebaseAuth!!.createUserWithEmailAndPassword(user_email, user_pwd)?.addOnCompleteListener(this){
                    if(it.isSuccessful){
                        val mFirebaseUser : FirebaseUser? = mFirebaseAuth?.currentUser
                        val user_uid :String = mFirebaseUser!!.uid
                        mDatabaseRef = FirebaseDatabase.getInstance().getReference("ShareMo").child("UserAccount").child(user_uid)

                        val hashMap : HashMap<String, String> = HashMap()
                        hashMap.put("user_uid", user_uid)
                        hashMap.put("user_name", user_name)
                        hashMap.put("user_phone", user_phone)
                        hashMap.put("user_nickname", user_nickname)
                        hashMap.put("user_email", user_email)
                        hashMap.put("user_profileImage","")
                        hashMap.put("user_dong", spinnerDong!!.selectedItem.toString())


                        mDatabaseRef.setValue(hashMap).addOnCompleteListener {
                            if(it.isSuccessful){
                                edt_email.setText("")
                                edt_pwd.setText("")
                                edt_phone.setText("")
                                edt_nickname.setText("")
                                edt_name.setText("")
                            }
                        }

                        Toast.makeText(this, "$user_name 님, 가입을 축하합니다", Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        Toast.makeText(this, "비밀번호가 6자 이상이 아닙니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    //스피너 설정
    private fun initAddressSpinner() {
        spinnerCity!!.onItemSelectedListener =  object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 시군구, 동의 스피너를 초기화한다.
                when (position) {
                    0 -> spinnerSigungu!!.adapter = null
                    1 -> setSigunguSpinnerAdapterItem(R.array.spinner_region_seoul)
                    2 -> setSigunguSpinnerAdapterItem(R.array.spinner_region_busan)
                    3 -> setSigunguSpinnerAdapterItem(R.array.spinner_region_daegu)
                    4 -> setSigunguSpinnerAdapterItem(R.array.spinner_region_incheon)
                    5 -> setSigunguSpinnerAdapterItem(R.array.spinner_region_gwangju)
                    6 -> setSigunguSpinnerAdapterItem(R.array.spinner_region_daejeon)
                    7 -> setSigunguSpinnerAdapterItem(R.array.spinner_region_ulsan)
                    8 -> setSigunguSpinnerAdapterItem(R.array.spinner_region_sejong)
                    9 -> setSigunguSpinnerAdapterItem(R.array.spinner_region_gyeonggi)
                    10 -> setSigunguSpinnerAdapterItem(R.array.spinner_region_gangwon)
                    11 -> setSigunguSpinnerAdapterItem(R.array.spinner_region_chung_buk)
                    12 -> setSigunguSpinnerAdapterItem(R.array.spinner_region_chung_nam)
                    13 -> setSigunguSpinnerAdapterItem(R.array.spinner_region_jeon_buk)
                    14 -> setSigunguSpinnerAdapterItem(R.array.spinner_region_jeon_nam)
                    15 -> setSigunguSpinnerAdapterItem(R.array.spinner_region_gyeong_buk)
                    16 -> setSigunguSpinnerAdapterItem(R.array.spinner_region_gyeong_nam)
                    17 -> setSigunguSpinnerAdapterItem(R.array.spinner_region_jeju)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerSigungu!!.onItemSelectedListener =  object : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 서울특별시 선택시
                if (spinnerCity!!.selectedItemPosition == 1 && spinnerSigungu!!.selectedItemPosition > -1) {
                    when (position) {
                        0 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_gangnam)
                        1 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_gangdong)
                        2 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_gangbuk)
                        3 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_gangseo)
                        4 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_gwanak)
                        5 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_gwangjin)
                        6 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_guro)
                        7 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_geumcheon)
                        8 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_nowon)
                        9 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_dobong)
                        10 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_dongdaemun)
                        11 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_dongjag)
                        12 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_mapo)
                        13 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_seodaemun)
                        14 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_seocho)
                        15 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_seongdong)
                        16 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_seongbuk)
                        17 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_songpa)
                        18 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_yangcheon)
                        19 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_yeongdeungpo)
                        20 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_yongsan)
                        21 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_eunpyeong)
                        22 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_jongno)
                        23 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_jung)
                        24 -> setDongSpinnerAdapterItem(R.array.spinner_region_seoul_jungnanggu)
                    }
                } else {
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setSigunguSpinnerAdapterItem(array_resource: Int) {
        if (arrayAdapter != null) {
            spinnerSigungu!!.adapter = null
            arrayAdapter = null
        }
        if (spinnerCity!!.selectedItemPosition > 1) {
            spinnerDong!!.adapter = null
        }
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(array_resource) as Array<String>)
        arrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSigungu!!.adapter = arrayAdapter
    }

    private fun setDongSpinnerAdapterItem(array_resource: Int) {
        if (arrayAdapter != null) {
            spinnerDong!!.adapter = null
            arrayAdapter = null
        }
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(array_resource) as Array<String>)
        arrayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDong!!.adapter = arrayAdapter
    }
}