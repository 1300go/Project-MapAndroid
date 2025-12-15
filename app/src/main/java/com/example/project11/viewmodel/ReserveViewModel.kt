package com.example.project11

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Date

class ReserveViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val ticketPrice = 20000

    // --- LiveData (View가 관찰할 데이터) ---

    // 1. 이미지 URL
    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String> get() = _imageUrl

    // 2. 캘린더 날짜 범위 (최소, 최대, 초기설정값)
    private val _calendarConstraints = MutableLiveData<Triple<Long, Long, Long?>>()
    val calendarConstraints: LiveData<Triple<Long, Long, Long?>> get() = _calendarConstraints

    // 3. 티켓 수량
    private val _ticketCount = MutableLiveData(1)
    val ticketCount: LiveData<Int> get() = _ticketCount

    // 4. 총 가격
    private val _totalPrice = MutableLiveData(ticketPrice)
    val totalPrice: LiveData<Int> get() = _totalPrice

    // 5. 토스트 메시지 이벤트
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    // 6. 예매 성공 여부 (화면 이동용)
    private val _reservationSuccess = MutableLiveData<Boolean>()
    val reservationSuccess: LiveData<Boolean> get() = _reservationSuccess

    // 내부 관리 데이터
    private var selectedDate: Date = Date()

    // --- 초기화 및 로직 ---

    fun loadFestivalData() {
        db.collection("festivals").document("andong")
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // 이미지 URL 처리
                    val url = document.getString("imageUrl")
                    if (!url.isNullOrEmpty()) {
                        _imageUrl.value = url!!
                    }

                    // 날짜 제한 처리
                    val openTimestamp = document.getTimestamp("open_date")
                    val closeTimestamp = document.getTimestamp("close_date")

                    if (openTimestamp != null && closeTimestamp != null) {
                        val minDateMillis = openTimestamp.seconds * 1000
                        val maxDateMillis = closeTimestamp.seconds * 1000

                        // 현재 시간이 범위 밖이면 오픈일로 초기화
                        var defaultDateMillis: Long? = null
                        if (System.currentTimeMillis() < minDateMillis) {
                            defaultDateMillis = minDateMillis
                            selectedDate = openTimestamp.toDate()
                        }

                        _calendarConstraints.value = Triple(minDateMillis, maxDateMillis, defaultDateMillis)
                    }
                }
            }
            .addOnFailureListener {
                _toastMessage.value = "데이터를 불러오는데 실패했습니다."
            }
    }

    fun increaseTicket() {
        val currentCount = _ticketCount.value ?: 1
        updateTicketInfo(currentCount + 1)
    }

    fun decreaseTicket() {
        val currentCount = _ticketCount.value ?: 1
        if (currentCount > 1) {
            updateTicketInfo(currentCount - 1)
        }
    }

    private fun updateTicketInfo(count: Int) {
        _ticketCount.value = count
        _totalPrice.value = count * ticketPrice
    }

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        selectedDate = calendar.time
    }

    fun reserveTicket() {
        val count = _ticketCount.value ?: 1

        val reservationData = hashMapOf(
            "rsv_count" to count,
            "rsv_date" to Timestamp(selectedDate),
            "user_id" to "user1"
        )

        db.collection("festivals").document("andong")
            .collection("reservations")
            .add(reservationData)
            .addOnSuccessListener {
                _toastMessage.value = "예매가 완료되었습니다."
                _reservationSuccess.value = true
            }
            .addOnFailureListener {
                _toastMessage.value = "예매가 실패하였습니다."
            }
    }
}