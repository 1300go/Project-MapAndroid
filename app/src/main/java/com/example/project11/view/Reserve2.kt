package com.example.project11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class Reserve2 : AppCompatActivity() {

    // ViewModel 연결
    private val viewModel: Reserve2ViewModel by viewModels()

    // UI 컴포넌트
    private lateinit var container: LinearLayout

    // 팝업 관련 변수
    private lateinit var popupDimBackground: View
    private lateinit var cancelPopup: LinearLayout
    private lateinit var popupTitle: TextView
    private lateinit var popupDate: TextView
    private lateinit var popupCount: TextView
    private lateinit var confirmCancelButton: Button
    private lateinit var closePopupButton: Button

    // 삭제할 대상 임시 저장 (ID와 View)
    private var targetDocumentId: String? = null
    private var targetView: View? = null

    // 날짜 포맷 (View용)
    private val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reserve2)

        initViews()
        setupObservers() // ViewModel 관찰 설정

        // 데이터 요청
        viewModel.loadReservations()
    }

    private fun initViews() {
        container = findViewById(R.id.reservationContainer)

        // 팝업 UI 연결
        popupDimBackground = findViewById(R.id.popupDimBackground)
        cancelPopup = findViewById(R.id.cancelPopup)
        popupTitle = findViewById(R.id.popupTitle)
        popupDate = findViewById(R.id.popupDate)
        popupCount = findViewById(R.id.popupCount)
        confirmCancelButton = findViewById(R.id.confirmCancelButton)
        closePopupButton = findViewById(R.id.closePopupButton)

        // 닫기 버튼
        closePopupButton.setOnClickListener { hidePopup() }

        // 확인(삭제) 버튼 -> ViewModel에 요청
        confirmCancelButton.setOnClickListener {
            targetDocumentId?.let { id ->
                viewModel.deleteReservation(id)
            }
        }
    }

    private fun setupObservers() {
        // 1. 예약 목록 데이터 관찰 -> 화면 그리기
        viewModel.reservationList.observe(this) { items ->
            // 기존 뷰 초기화
            container.removeAllViews()

            for (item in items) {
                createReservationView(item)
            }
        }

        // 2. 삭제 성공 관찰
        viewModel.deleteSuccess.observe(this) { deletedId ->
            Toast.makeText(this, "예매가 취소되었습니다.", Toast.LENGTH_SHORT).show()

            // 화면에서 해당 뷰 제거
            if (targetView != null) {
                container.removeView(targetView)
            }
            hidePopup()

            // (선택사항) 데이터 갱신이 필요하면 viewModel.loadReservations() 호출
        }

        // 3. 에러 메시지 관찰
        viewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            hidePopup()
        }
    }

    // 동적으로 뷰 생성하는 함수 (View의 역할)
    private fun createReservationView(item: ReservationItem) {
        val itemView = LayoutInflater.from(this).inflate(R.layout.reservation_item, container, false)

        // 뷰 찾기
        val cardTitle = itemView.findViewById<TextView>(R.id.cardTitleTextView)
        val cardDate = itemView.findViewById<TextView>(R.id.cardDateTextView)
        val cardCount = itemView.findViewById<TextView>(R.id.cardCountTextView)
        val cardImage = itemView.findViewById<ImageView>(R.id.cardImageView)
        val cancelButton = itemView.findViewById<Button>(R.id.cardCancelButton)

        // 데이터 포맷팅
        val dateStr = if (item.date != null) dateFormat.format(item.date) else ""
        val countStr = "${item.count}명"

        // UI 적용
        cardTitle.text = item.title
        cardDate.text = dateStr
        cardCount.text = countStr

        if (item.imageUrl.isNotEmpty()) {
            Glide.with(this).load(item.imageUrl).centerCrop().into(cardImage)
        }

        // 취소 버튼 클릭 시 팝업 띄우기 (삭제 아님, 팝업만)
        cancelButton.setOnClickListener {
            targetDocumentId = item.docId
            targetView = itemView

            // 팝업 내용 채우기
            popupTitle.text = item.title
            popupDate.text = dateStr
            popupCount.text = countStr

            showPopup()
        }

        container.addView(itemView)
    }

    private fun showPopup() {
        popupDimBackground.visibility = View.VISIBLE
        cancelPopup.visibility = View.VISIBLE
    }

    private fun hidePopup() {
        popupDimBackground.visibility = View.GONE
        cancelPopup.visibility = View.GONE
        targetDocumentId = null
        targetView = null
    }
}