package com.example.project11

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class Reserve2ViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // 1. 예약 목록 데이터 (View가 관찰함)
    private val _reservationList = MutableLiveData<List<ReservationItem>>()
    val reservationList: LiveData<List<ReservationItem>> get() = _reservationList

    // 2. 삭제 성공 여부 이벤트
    private val _deleteSuccess = MutableLiveData<String>() // 삭제된 ID 전달
    val deleteSuccess: LiveData<String> get() = _deleteSuccess

    // 3. 에러 메시지
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // --- 로직: 데이터 불러오기 ---
    fun loadReservations() {
        // 1. 축제 정보(제목, 이미지) 먼저 가져오기
        db.collection("festivals").document("andong").get()
            .addOnSuccessListener { festivalDoc ->
                val title = festivalDoc.getString("title") ?: "안동 축제"
                val imageUrl = festivalDoc.getString("imageUrl") ?: ""

                // 2. 예약 목록 가져오기
                fetchReservations(title, imageUrl)
            }
            .addOnFailureListener {
                _errorMessage.value = "축제 정보를 불러오지 못했습니다."
            }
    }

    private fun fetchReservations(title: String, imageUrl: String) {
        db.collection("festivals").document("andong")
            .collection("reservations")
            .get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<ReservationItem>()

                for (document in result) {
                    val timestamp = document.getTimestamp("rsv_date")
                    val count = document.getLong("rsv_count")?.toInt() ?: 0

                    // Model 객체 생성
                    list.add(
                        ReservationItem(
                            docId = document.id,
                            title = title,
                            imageUrl = imageUrl,
                            date = timestamp?.toDate(),
                            count = count
                        )
                    )
                }
                // View에 데이터 전달
                _reservationList.value = list
            }
            .addOnFailureListener {
                _errorMessage.value = "예약 목록을 불러오지 못했습니다."
            }
    }

    // --- 로직: 예약 삭제하기 ---
    fun deleteReservation(docId: String) {
        db.collection("festivals").document("andong")
            .collection("reservations").document(docId)
            .delete()
            .addOnSuccessListener {
                // 삭제 성공 시 삭제된 문서 ID를 View에 알림
                _deleteSuccess.value = docId
            }
            .addOnFailureListener {
                _errorMessage.value = "취소 실패: 잠시 후 다시 시도해주세요."
            }
    }
}