package com.example.project11

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

// Enum 정의
enum class Region(val label: String) {
    SEOUL("서울"), GYEONGGI("경기"), INCHEON("인천"), GANGWON("강원"),
    CHUNGBUK("충북"), CHUNGNAM("충남"), DAEJEON("대전"), SEJONG("세종"),
    JEONBUK("전북"), JEONNAM("전남"), GWANGJU("광주"),
    GYEONGBUK("경북"), GYEONGNAM("경남"), DAEGU("대구"), ULSAN("울산"), BUSAN("부산"), JEJU("제주")
}

class TravelActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var mapContainer: View

    // XML 뷰 변수
    private lateinit var btnSearchToggle: Button
    private lateinit var scrollRegions: View
    private lateinit var llRegionList: LinearLayout
    private lateinit var scrollLocals: View
    private lateinit var llLocalList: LinearLayout
    private lateinit var rvPlaceList: RecyclerView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel)

        // 1. XML 뷰 연결
        mapContainer = findViewById(R.id.map_container)
        btnSearchToggle = findViewById(R.id.btn_search_toggle)
        scrollRegions = findViewById(R.id.scroll_regions)
        llRegionList = findViewById(R.id.ll_region_list)
        scrollLocals = findViewById(R.id.scroll_locals)
        llLocalList = findViewById(R.id.ll_local_list)
        rvPlaceList = findViewById(R.id.rv_place_list)

        // 리사이클러뷰 설정
        rvPlaceList.layoutManager = LinearLayoutManager(this)

        // 2. 지도 초기화
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // 3. 버튼 이벤트
        btnSearchToggle.setOnClickListener {
            toggleRegionMenu()
        }

        // 4. 지역 버튼 생성
        setupRegionButtons()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val korea = LatLng(36.5, 127.5)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(korea, 7f))
    }

    private fun toggleRegionMenu() {
        if (scrollRegions.visibility == View.VISIBLE) {
            scrollRegions.visibility = View.GONE
            scrollLocals.visibility = View.GONE
            setMapSplitView(false)
            if (::map.isInitialized) map.clear()
        } else {
            scrollRegions.visibility = View.VISIBLE
            setMapSplitView(false)
        }
    }

    private fun setupRegionButtons() {
        for (region in Region.values()) {
            val button = Button(this)
            button.text = region.label
            button.setBackgroundColor(android.graphics.Color.WHITE)
            button.setTextColor(android.graphics.Color.BLACK)

            button.setOnClickListener {
                // [수정 포인트 1] Travels 컬렉션에서 찾기
                fetchLocalNamesFromTravels(region.label)
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 8)
            llRegionList.addView(button, params)
        }
    }

    // --- [DB 로직 1] Travels 컬렉션에서 세부 지역(안성 등) 찾기 ---
    private fun fetchLocalNamesFromTravels(regionLabel: String) {
        Toast.makeText(this, "$regionLabel 지역 불러오는 중...", Toast.LENGTH_SHORT).show()

        // 1. travels 컬렉션 조회
        db.collection("travels")
            .whereEqualTo("local_state", regionLabel) // 예: local_state가 "경기"인 문서들
            .get()
            .addOnSuccessListener { result ->
                val names = mutableSetOf<String>()
                for (document in result) {
                    // 문서 필드에 있는 local_name을 가져옴
                    val localName = document.getString("local_name")
                    if (!localName.isNullOrEmpty()) {
                        names.add(localName)
                    }
                }

                if (names.isEmpty()) {
                    Toast.makeText(this, "데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    setupLocalButtons(names.toList().sorted())
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error getting documents: ", it)
                Toast.makeText(this, "로딩 실패", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupLocalButtons(localNames: List<String>) {
        llLocalList.removeAllViews()
        scrollLocals.visibility = View.VISIBLE

        for (name in localNames) {
            val button = Button(this)
            button.text = name
            button.setBackgroundColor(android.graphics.Color.WHITE)
            button.setTextColor(android.graphics.Color.BLACK)

            button.setOnClickListener {
                // [수정 포인트 2] 해당 지역의 places 하위 컬렉션 찾기
                fetchPlacesFromSubCollection(name)
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 8)
            llLocalList.addView(button, params)
        }
    }

    // --- [DB 로직 2] Travels 문서 안의 Places 하위 컬렉션 조회 ---
    private fun fetchPlacesFromSubCollection(localName: String) {
        Toast.makeText(this, "$localName 검색 중...", Toast.LENGTH_SHORT).show()

        // 1. 먼저 local_name이 "안성"인 부모 문서(Travels)를 찾는다.
        db.collection("travels")
            .whereEqualTo("local_name", localName)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "해당 지역 문서를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // 2. 부모 문서를 찾았으면, 그 문서 안의 'places' 컬렉션으로 들어간다.
                val parentDoc = documents.documents[0] // 첫 번째 일치하는 문서

                parentDoc.reference.collection("places")
                    .get()
                    .addOnSuccessListener { placeResult ->
                        val list = mutableListOf<Place>()

                        for (document in placeResult) {
                            val name = document.getString("place_name") ?: ""
                            val address = document.getString("place_address") ?: ""
                            val imageUrl = document.getString("place_image") ?: ""

                            // GeoPoint 처리
                            val geoPoint = document.getGeoPoint("place_geo")
                            val lat = geoPoint?.latitude ?: 0.0
                            val lng = geoPoint?.longitude ?: 0.0

                            // 리스트에 추가 (localName은 부모에서 가져온 값 사용)
                            list.add(Place(name, address, imageUrl, lat, lng, localName))
                        }

                        if (list.isEmpty()) {
                            Toast.makeText(this, "등록된 장소가 없습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            // UI 업데이트
                            updateUI(list)
                        }
                    }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error getting places: ", it)
            }
    }

    // UI 업데이트 (지도 & 리스트)
    private fun updateUI(list: List<Place>) {
        updateMapMarkers(list)

        val adapter = PlaceAdapter(list)
        rvPlaceList.adapter = adapter

        setMapSplitView(true)
        scrollRegions.visibility = View.GONE
        scrollLocals.visibility = View.GONE
    }

    private fun setMapSplitView(isSplit: Boolean) {
        val mapParams = mapContainer.layoutParams as ConstraintLayout.LayoutParams
        val listParams = rvPlaceList.layoutParams as ConstraintLayout.LayoutParams

        if (isSplit) {
            mapParams.matchConstraintPercentHeight = 0.5f
            listParams.matchConstraintPercentHeight = 0.5f
            listParams.height = 0
            listParams.topToBottom = R.id.map_container
            rvPlaceList.visibility = View.VISIBLE
        } else {
            mapParams.matchConstraintPercentHeight = 1.0f
            rvPlaceList.visibility = View.GONE
        }
        mapContainer.layoutParams = mapParams
        rvPlaceList.layoutParams = listParams
    }

    private fun updateMapMarkers(places: List<Place>) {
        if (!::map.isInitialized) return
        map.clear()
        for (place in places) {
            val position = LatLng(place.lat, place.lng)
            map.addMarker(
                MarkerOptions().position(position).title(place.name).snippet(place.address)
            )
        }
        if (places.isNotEmpty()) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(places[0].lat, places[0].lng), 11f))
        }
    }

    // Adapter 클래스
    class PlaceAdapter(private val places: List<Place>) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {
        class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val image: ImageView = view.findViewById(R.id.iv_place_image)
            val name: TextView = view.findViewById(R.id.tv_place_name)
            val address: TextView = view.findViewById(R.id.tv_place_address)
            val tag: TextView = view.findViewById(R.id.tv_tag_local)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place_card, parent, false)
            return PlaceViewHolder(view)
        }

        override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
            val place = places[position]
            holder.name.text = place.name
            holder.address.text = place.address
            holder.tag.text = "#${place.localName}"
            Glide.with(holder.itemView.context).load(place.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background).into(holder.image)
        }

        override fun getItemCount() = places.size
    }
}