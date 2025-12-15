package com.example.project11

// 장소 정보를 담는 데이터 모델
data class Place(
    val name: String,
    val address: String,
    val imageUrl: String,
    val lat: Double,
    val lng: Double,
    val localName: String
)