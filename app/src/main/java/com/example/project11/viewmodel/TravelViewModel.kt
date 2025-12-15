package com.example.project11

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class TravelViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // --- State (View가 관찰할 데이터) ---

    // 1. 세부 지역 이름 목록 (예: [안성, 평택...])
    private val _localNames = MutableLiveData<List<String>>()
    val localNames: LiveData<List<String>> get() = _localNames

    // 2. 장소 목록 (지도와 리스트에 표시할 데이터)
    private val _places = MutableLiveData<List<Place>>()
    val places: LiveData<List<Place>> get() = _places

    // 3. 메뉴 표시 상태 (true=보임, false=숨김)
    private val _isMenuVisible = MutableLiveData(false)
    val isMenuVisible: LiveData<Boolean> get() = _isMenuVisible

    // 4. 지도 분할 모드 (true=반반, false=전체)
    private val _isMapSplit = MutableLiveData(false)
    val isMapSplit: LiveData<Boolean> get() = _isMapSplit

    // 5. 로딩/에러 메시지
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage


    // --- User Actions (사용자 동작) ---

    // 메뉴 토글 버튼 클릭 시
    fun toggleMenu() {
        val current = _isMenuVisible.value ?: false
        if (current) {
            // 닫을 때는 모두 초기화
            _isMenuVisible.value = false
            _isMapSplit.value = false
        } else {
            // 열기
            _isMenuVisible.value = true
            _isMapSplit.value = false
        }
    }

    // 지역(경기, 서울) 버튼 클릭 시 -> DB에서 세부 지역 가져오기
    fun onRegionSelected(regionLabel: String) {
        _toastMessage.value = "$regionLabel 지역 불러오는 중..."

        db.collection("travels")
            .whereEqualTo("local_state", regionLabel)
            .get()
            .addOnSuccessListener { result ->
                val names = mutableSetOf<String>()
                for (document in result) {
                    val localName = document.getString("local_name")
                    if (!localName.isNullOrEmpty()) {
                        names.add(localName)
                    }
                }

                if (names.isEmpty()) {
                    _toastMessage.value = "데이터가 없습니다."
                } else {
                    _localNames.value = names.toList().sorted()
                    // 세부 지역 목록이 로드되면 메뉴는 계속 열려있어야 함
                    _isMenuVisible.value = true
                }
            }
            .addOnFailureListener {
                _toastMessage.value = "로딩 실패"
            }
    }

    // 세부 지역(안성) 버튼 클릭 시 -> DB에서 장소(places) 가져오기
    fun onLocalSelected(localName: String) {
        _toastMessage.value = "$localName 검색 중..."

        // 1. 부모 문서 찾기
        db.collection("travels")
            .whereEqualTo("local_name", localName)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    _toastMessage.value = "해당 지역 문서를 찾을 수 없습니다."
                    return@addOnSuccessListener
                }

                val parentDoc = documents.documents[0]

                // 2. 하위 컬렉션(places) 조회
                parentDoc.reference.collection("places")
                    .get()
                    .addOnSuccessListener { placeResult ->
                        val list = mutableListOf<Place>()

                        for (document in placeResult) {
                            val name = document.getString("place_name") ?: ""
                            val address = document.getString("place_address") ?: ""
                            val imageUrl = document.getString("place_image") ?: ""
                            val geoPoint = document.getGeoPoint("place_geo")
                            val lat = geoPoint?.latitude ?: 0.0
                            val lng = geoPoint?.longitude ?: 0.0

                            list.add(Place(name, address, imageUrl, lat, lng, localName))
                        }

                        if (list.isEmpty()) {
                            _toastMessage.value = "등록된 장소가 없습니다."
                        } else {
                            _places.value = list
                            // 데이터가 로드되면 지도 분할 모드로 변경하고 메뉴 닫기
                            _isMapSplit.value = true
                            _isMenuVisible.value = false
                        }
                    }
            }
            .addOnFailureListener {
                _toastMessage.value = "장소 불러오기 실패"
            }
    }
}