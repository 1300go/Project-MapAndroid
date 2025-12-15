package com.example.project11

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.text.DecimalFormat

class Reserve1 : AppCompatActivity() {

    // ViewModel 연결 (activity-ktx 라이브러리 필요)
    private val viewModel: ReserveViewModel by viewModels()

    // UI 컴포넌트
    private lateinit var headerImageView: ImageView
    private lateinit var calendarView: CalendarView
    private lateinit var quantityTextView: TextView
    private lateinit var totalPriceTextView: TextView
    private lateinit var incrementButton: Button
    private lateinit var decrementButton: Button
    private lateinit var reserveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reserve1)

        initViews()
        setupObservers() // ViewModel 데이터 관찰 설정
        setupListeners() // 버튼 클릭 이벤트 설정

        // 데이터 로드 요청
        viewModel.loadFestivalData()
    }

    private fun initViews() {
        headerImageView = findViewById(R.id.headerImageView)
        calendarView = findViewById(R.id.calendarView)
        quantityTextView = findViewById(R.id.quantityTextView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        incrementButton = findViewById(R.id.incrementButton)
        decrementButton = findViewById(R.id.decrementButton)
        reserveButton = findViewById(R.id.reserveButton)

        // 초기 날짜 UI 설정 (데이터 로드 전)
        calendarView.date = System.currentTimeMillis()
    }

    private fun setupListeners() {
        // ViewModel의 함수 호출
        incrementButton.setOnClickListener { viewModel.increaseTicket() }
        decrementButton.setOnClickListener { viewModel.decreaseTicket() }

        reserveButton.setOnClickListener { viewModel.reserveTicket() }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            viewModel.setDate(year, month, dayOfMonth)
        }
    }

    private fun setupObservers() {
        val priceFormat = DecimalFormat("#,###")

        // 1. 티켓 수량 변경 관찰
        viewModel.ticketCount.observe(this) { count ->
            quantityTextView.text = count.toString()
        }

        // 2. 총 가격 변경 관찰
        viewModel.totalPrice.observe(this) { price ->
            totalPriceTextView.text = "${priceFormat.format(price)}원"
        }

        // 3. 이미지 URL 로드 관찰
        viewModel.imageUrl.observe(this) { url ->
            Glide.with(this)
                .load(url)
                .centerCrop()
                .into(headerImageView)
        }

        // 4. 캘린더 날짜 범위 설정 관찰
        viewModel.calendarConstraints.observe(this) { (min, max, defaultDate) ->
            calendarView.minDate = min
            calendarView.maxDate = max
            // 초기 설정 날짜가 있다면 적용
            if (defaultDate != null) {
                calendarView.date = defaultDate
            }
        }

        // 5. 토스트 메시지 관찰
        viewModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        // 6. 예매 성공 시 화면 이동 관찰
        viewModel.reservationSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                val intent = Intent(this, Reserve2::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}