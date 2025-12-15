package com.example.project11

import java.util.Date

// 예약 정보를 담는 데이터 그릇
data class ReservationItem(
    val docId: String,       // 문서 ID (삭제할 때 필요)
    val title: String,       // 축제 제목
    val imageUrl: String,    // 이미지 주소
    val date: Date?,         // 예약 날짜
    val count: Int           // 인원 수
)