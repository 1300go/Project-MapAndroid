package com.example.project11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class Reserve2 : AppCompatActivity() {

    // 팝업 관련 변수
    private lateinit var popupDimBackground: View
    private lateinit var cancelPopup: LinearLayout
    private lateinit var popupTitle: TextView
    private lateinit var popupDate: TextView
    private lateinit var popupCount: TextView
    private lateinit var confirmCancelButton: Button
    private lateinit var closePopupButton: Button

    // 삭제할 대상을 임시 저장할 변수
    private var targetDocumentId: String? = null
    private var targetView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reserve2)

        // 1. 팝업 UI 연결
        initPopupViews()

        // 2. 데이터 불러오기 및 목록 생성
        loadReservations()
    }

    private fun initPopupViews() {
        popupDimBackground = findViewById(R.id.popupDimBackground)
        cancelPopup = findViewById(R.id.cancelPopup)
        popupTitle = findViewById(R.id.popupTitle)
        popupDate = findViewById(R.id.popupDate)
        popupCount = findViewById(R.id.popupCount)
        confirmCancelButton = findViewById(R.id.confirmCancelButton)
        closePopupButton = findViewById(R.id.closePopupButton)

        // [팝업] 취소 버튼 (그냥 닫기) - 요구사항 5
        closePopupButton.setOnClickListener {
            hidePopup()
        }

        // [팝업] 확인 버튼 (실제 삭제) - 요구사항 5, 6
        confirmCancelButton.setOnClickListener {
            deleteReservation()
        }
    }

    private fun loadReservations() {
        val db = FirebaseFirestore.getInstance()
        val container = findViewById<LinearLayout>(R.id.reservationContainer)
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)

        // 먼저 축제 정보(이미지, 제목)를 가져옴 (req 2)
        db.collection("festivals").document("andong").get()
            .addOnSuccessListener { festivalDoc ->
                val festivalTitle = festivalDoc.getString("title") ?: "안동 축제"
                val imageUrl = festivalDoc.getString("imageUrl") ?: ""

                // 예약 목록 가져오기 (req 1)
                db.collection("festivals").document("andong")
                    .collection("reservations")
                    .get()
                    .addOnSuccessListener { result ->

                        // 기존 목록 비우기 (중복 방지)
                        container.removeAllViews()

                        for (document in result) {
                            // 1. reservation_item.xml 레이아웃을 가져와서 뷰 생성
                            val itemView = LayoutInflater.from(this).inflate(R.layout.reservation_item, container, false)

                            // 2. 데이터 추출
                            val timestamp = document.getTimestamp("rsv_date")
                            val count = document.getLong("rsv_count")?.toInt() ?: 0
                            val dateStr = if (timestamp != null) dateFormat.format(timestamp.toDate()) else ""
                            val countStr = "${count}명"

                            // 3. 카드뷰 UI 연결 및 데이터 넣기 (req 3)
                            val cardTitle = itemView.findViewById<TextView>(R.id.cardTitleTextView)
                            val cardDate = itemView.findViewById<TextView>(R.id.cardDateTextView)
                            val cardCount = itemView.findViewById<TextView>(R.id.cardCountTextView)
                            val cardImage = itemView.findViewById<ImageView>(R.id.cardImageView)
                            val cancelButton = itemView.findViewById<Button>(R.id.cardCancelButton)

                            cardTitle.text = festivalTitle
                            cardDate.text = dateStr
                            cardCount.text = countStr

                            // 이미지 로드 (req 2)
                            if (imageUrl.isNotEmpty()) {
                                Glide.with(this).load(imageUrl).centerCrop().into(cardImage)
                            }

                            // 4. [카드] 취소 버튼 클릭 이벤트 (req 4)
                            cancelButton.setOnClickListener {
                                // 삭제할 대상 정보 임시 저장
                                targetDocumentId = document.id
                                targetView = itemView

                                // 팝업 텍스트 채우기
                                popupTitle.text = festivalTitle
                                popupDate.text = dateStr
                                popupCount.text = countStr

                                showPopup()
                            }

                            // 컨테이너에 카드 추가
                            container.addView(itemView)
                        }
                    }
            }
    }

    private fun deleteReservation() {
        val docId = targetDocumentId ?: return
        val viewToRemove = targetView ?: return

        val db = FirebaseFirestore.getInstance()

        // DB에서 문서 삭제 (req 5)
        db.collection("festivals").document("andong")
            .collection("reservations").document(docId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "예매가 취소되었습니다.", Toast.LENGTH_SHORT).show()

                // 화면에서 해당 카드 제거 (req 6 - 화면 이동 없이 유지)
                val container = findViewById<LinearLayout>(R.id.reservationContainer)
                container.removeView(viewToRemove)

                hidePopup()
            }
            .addOnFailureListener {
                Toast.makeText(this, "취소 실패: 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                hidePopup()
            }
    }

    private fun showPopup() {
        popupDimBackground.visibility = View.VISIBLE
        cancelPopup.visibility = View.VISIBLE
    }

    private fun hidePopup() {
        popupDimBackground.visibility = View.GONE
        cancelPopup.visibility = View.GONE
        // 타겟 초기화
        targetDocumentId = null
        targetView = null
    }
}