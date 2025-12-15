package com.example.project11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
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


enum class Region(val label: String) {
    SEOUL("서울"), GYEONGGI("경기"), INCHEON("인천"), GANGWON("강원"),
    CHUNGBUK("충북"), CHUNGNAM("충남"), DAEJEON("대전"), SEJONG("세종"),
    JEONBUK("전북"), JEONNAM("전남"), GWANGJU("광주"),
    GYEONGBUK("경북"), GYEONGNAM("경남"), DAEGU("대구"), ULSAN("울산"), BUSAN("부산"), JEJU("제주")
}

class TravelActivity : AppCompatActivity(), OnMapReadyCallback {

    // ViewModel 연결
    private val viewModel: TravelViewModel by viewModels()

    private lateinit var map: GoogleMap
    private lateinit var mapContainer: View

    // XML 뷰 변수
    private lateinit var btnSearchToggle: Button
    private lateinit var scrollRegions: View
    private lateinit var llRegionList: LinearLayout
    private lateinit var scrollLocals: View
    private lateinit var llLocalList: LinearLayout
    private lateinit var rvPlaceList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel)

        initViews()
        setupRegionButtons() // 지역 버튼(경기, 서울 등)은 고정이므로 미리 생성
        setupObservers()     // ViewModel 관찰 시작
    }

    private fun initViews() {
        mapContainer = findViewById(R.id.map_container)
        btnSearchToggle = findViewById(R.id.btn_search_toggle)
        scrollRegions = findViewById(R.id.scroll_regions)
        llRegionList = findViewById(R.id.ll_region_list)
        scrollLocals = findViewById(R.id.scroll_locals)
        llLocalList = findViewById(R.id.ll_local_list)
        rvPlaceList = findViewById(R.id.rv_place_list)

        rvPlaceList.layoutManager = LinearLayoutManager(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // 버튼 클릭 -> ViewModel에 알림
        btnSearchToggle.setOnClickListener {
            viewModel.toggleMenu()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val korea = LatLng(36.5, 127.5)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(korea, 7f))
    }

    // --- Observer Setup (핵심) ---
    private fun setupObservers() {
        // 1. 세부 지역 목록(안성, 평택 등)이 바뀌면 버튼 생성
        viewModel.localNames.observe(this) { names ->
            setupLocalButtons(names)
        }

        // 2. 장소 목록이 바뀌면 지도 마커 & 리스트 갱신
        viewModel.places.observe(this) { places ->
            updateMapMarkers(places)
            rvPlaceList.adapter = PlaceAdapter(places)
        }

        // 3. 메뉴 보임/숨김 상태 관찰
        viewModel.isMenuVisible.observe(this) { isVisible ->
            if (isVisible) {
                scrollRegions.visibility = View.VISIBLE
                // 세부 지역 버튼이 있다면 같이 보여줌
                if (llLocalList.childCount > 0) {
                    scrollLocals.visibility = View.VISIBLE
                }
            } else {
                scrollRegions.visibility = View.GONE
                scrollLocals.visibility = View.GONE
            }
        }

        // 4. 지도 분할 모드 관찰
        viewModel.isMapSplit.observe(this) { isSplit ->
            setMapSplitView(isSplit)
            if (!isSplit && ::map.isInitialized) {
                map.clear() // 분할 해제 시 지도 초기화
            }
        }

        // 5. 토스트 메시지
        viewModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    // --- UI Logic ---

    // [경기, 서울...] 버튼 생성 (고정 데이터)
    private fun setupRegionButtons() {
        for (region in Region.values()) {
            val button = Button(this)
            button.text = region.label
            button.setBackgroundColor(android.graphics.Color.WHITE)
            button.setTextColor(android.graphics.Color.BLACK)

            button.setOnClickListener {
                // ViewModel에 요청
                viewModel.onRegionSelected(region.label)
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 8)
            llRegionList.addView(button, params)
        }
    }

    // [안성, 수원...] 버튼 생성 (동적 데이터)
    private fun setupLocalButtons(localNames: List<String>) {
        llLocalList.removeAllViews()
        scrollLocals.visibility = View.VISIBLE

        for (name in localNames) {
            val button = Button(this)
            button.text = name
            button.setBackgroundColor(android.graphics.Color.WHITE)
            button.setTextColor(android.graphics.Color.BLACK)

            button.setOnClickListener {
                // ViewModel에 요청
                viewModel.onLocalSelected(name)
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 8)
            llLocalList.addView(button, params)
        }
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

    // Adapter는 Activity 내부에 유지 (사용자 선호 반영)
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