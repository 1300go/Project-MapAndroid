package com.example.project11

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide // Glide 라이브러리 필요
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat
import java.util.Calendar
import java.util.Date

class Reserve1 : AppCompatActivity() {

    // UI 컴포넌트 변수 선언
    private lateinit var headerImageView: ImageView
    private lateinit var calendarView: CalendarView
    private lateinit var quantityTextView: TextView
    private lateinit var totalPriceTextView: TextView
    private lateinit var incrementButton: Button
    private lateinit var decrementButton: Button
    private lateinit var reserveButton: Button

    // 데이터 변수
    private var ticketCount = 1
    private val ticketPrice = 20000
    private var selectedDate: Date = Date() // 기본값: 오늘

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reserve1)

        // 1. 뷰 초기화
        initViews()

        // 2. 파이어베이스 데이터 가져오기 (이미지, 날짜 제한)
        fetchFestivalData()

        // 3. 버튼 리스너 설정 (수량 조절, 예매)
        setupListeners()
    }

    private fun initViews() {
        headerImageView = findViewById(R.id.headerImageView)
        calendarView = findViewById(R.id.calendarView)
        quantityTextView = findViewById(R.id.quantityTextView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        incrementButton = findViewById(R.id.incrementButton)
        decrementButton = findViewById(R.id.decrementButton)
        reserveButton = findViewById(R.id.reserveButton)

        // 초기 날짜 설정 (오늘)
        calendarView.date = System.currentTimeMillis()
    }

    private fun fetchFestivalData() {
        val db = FirebaseFirestore.getInstance()
        // Firestore: festivals -> andong 문서 참조
        val docRef = db.collection("festivals").document("andong")

        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                // [요구사항 7] 이미지 로드 (imageUrl 필드)
                val imageUrl = document.getString("imageUrl")
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(imageUrl)
                        .centerCrop()
                        .into(headerImageView)
                }

                // [요구사항 1] 날짜 제한 (open_date ~ close_date)
                val openTimestamp = document.getTimestamp("open_date")
                val closeTimestamp = document.getTimestamp("close_date")

                if (openTimestamp != null && closeTimestamp != null) {
                    // Timestamp를 밀리초(Long)로 변환하여 CalendarView에 적용
                    val minDateMillis = openTimestamp.seconds * 1000
                    val maxDateMillis = closeTimestamp.seconds * 1000

                    calendarView.minDate = minDateMillis
                    calendarView.maxDate = maxDateMillis

                    // 선택 날짜 초기화: 오픈일로 설정 (범위 밖일 경우 대비)
                    if (System.currentTimeMillis() < minDateMillis) {
                        calendarView.date = minDateMillis
                        selectedDate = openTimestamp.toDate()
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "데이터를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupListeners() {
        val priceFormat = DecimalFormat("#,###") // 3자리마다 콤마

        // [요구사항 2, 4] + 버튼 클릭 (수량 증가, 가격 업데이트)
        incrementButton.setOnClickListener {
            ticketCount++
            quantityTextView.text = ticketCount.toString()
            updateTotalPrice(priceFormat)
        }

        // [요구사항 2, 4] - 버튼 클릭 (수량 감소, 1 미만 불가)
        decrementButton.setOnClickListener {
            if (ticketCount > 1) {
                ticketCount--
                quantityTextView.text = ticketCount.toString()
                updateTotalPrice(priceFormat)
            }
        }

        // 캘린더 날짜 변경 리스너
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // 선택된 날짜를 Date 객체로 변환하여 저장
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.time
        }

        // [요구사항 5, 6] 예매 버튼 클릭
        reserveButton.setOnClickListener {
            saveReservationToFirebase()
        }
    }

    // [요구사항 4] 가격 업데이트 함수
    private fun updateTotalPrice(formatter: DecimalFormat) {
        val total = ticketCount * ticketPrice
        totalPriceTextView.text = "${formatter.format(total)}원"
    }

    private fun saveReservationToFirebase() {
        val db = FirebaseFirestore.getInstance()

        // [요구사항 5] 저장할 데이터 생성
        val reservationData = hashMapOf(
            "rsv_count" to ticketCount, // 선택한 수량
            "rsv_date" to Timestamp(selectedDate), // 선택한 날짜
            "user_id" to "user1" // 고정된 사용자 ID
        )

        // festivals -> andong -> reservations 컬렉션에 추가
        db.collection("festivals").document("andong")
            .collection("reservations")
            .add(reservationData)
            .addOnSuccessListener {
                // [요구사항 6] 성공 시 처리
                Toast.makeText(this, "예매가 완료되었습니다.", Toast.LENGTH_SHORT).show()

                // Reserve2 화면으로 이동
                val intent = Intent(this, Reserve2::class.java)
                startActivity(intent)
                finish() // 현재 화면 종료 (선택 사항)
            }
            .addOnFailureListener { e ->
                // [요구사항 6] 실패 시 처리
                Toast.makeText(this, "예매가 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
    }
}