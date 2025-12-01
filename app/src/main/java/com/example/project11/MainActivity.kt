package com.example.project11

//  [이미지 관련] (Coil)
// [파이어베이스 관련] (Firebase)
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project11.ui.theme.Project11Theme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // ... class MainActivity ... onCreate ...
        setContent {
            Project11Theme {
                // '현재 화면' 상태 (1=표지, 2=지도, 3=상세정보)
                // 처음엔 1번(표지)부터 시작!
                var currentScreen by remember { mutableStateOf(1) }

                // '현재 화면' 숫자에 따라 보여줄 화면을 바꿉니다.
                when (currentScreen) {
                    1 -> {
                        // [1. 표지 화면] 시작 버튼 누르면 -> 2번(지도)으로 이동
                        SplashScreen(
                            modifier = Modifier.fillMaxSize(),
                            onStartClicked = { currentScreen = 2 }
                        )
                    }

                    2 -> {
                        // [2. 지도 화면] 축제 버튼 누르면 -> 3번(상세)으로 이동
                        MapScreen(
                            modifier = Modifier.fillMaxSize(),
                            onFestivalClicked = { currentScreen = 3 } // ⬅️ 여기가 해결되는 부분!
                        )
                    }

                    3 -> {
                        // [3. 상세 화면] 뒤로가기 누르면 -> 2번(지도)으로 이동
                        DetailScreen(
                            modifier = Modifier.fillMaxSize(),
                            onBackClicked = { currentScreen = 2 }
                        )
                    }
                }
            }
        }
// ...
    }
}


@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onStartClicked: () -> Unit
) {
    // 여기에 로고, 앱 이름, 버튼을 차곡차곡 쌓을 겁니다!
    // 1. 힐링 배경색 (아이보리)을 칠한 '표면'을 만듭니다.
    Surface(
        modifier = modifier, // (전체 화면을 채우도록 설정됨)
        color = Color(0xFFF5F5F0) // 힐링 배경색 (아이보리)
    ) {
        // 2. 'Column'을 사용해 모든 요소를 세로로, 가운데 정렬합니다.
        Column(
            modifier = Modifier
                .fillMaxSize() // Column도 꽉 채우고
                .padding(16.dp), // 화면 좌우에 약간의 여백
            verticalArrangement = Arrangement.Center, // 세로로 '가운데' 정렬
            horizontalAlignment = Alignment.CenterHorizontally // 가로로도 '가운데' 정렬
        ) {
            // 3. 여기에 '로고', '앱 이름', '버튼' 등을 넣을 겁니다!
            // '로고' (Image)
            Image(
                painter = painterResource(id = R.drawable.ic_eco), // 1-1에서 만든 아이콘
                contentDescription = "앱 로고" // (앱 설명)
            )

            // 로고와 앱 이름 사이에 약간의 공간을 줍니다.
            Spacer(modifier = Modifier.height(16.dp))
            // '앱 이름' (Text)
            Text(
                text = "쉼표",
                fontSize = 32.sp, // 글자 크기
                fontWeight = FontWeight.Bold // 굵게
            )

            // 앱 이름과 한 줄 소개 사이에 약간의 공간을 줍니다.
            Spacer(modifier = Modifier.height(8.dp))

            // '한 줄 소개' (Text)
            Text(
                text = "지친 일상 속, 쉼표가 되는 여행",
                fontSize = 16.sp
            )

            // '한 줄 소개' (Text)
            Text(
                text = "지친 일상 속, 쉼표가 되는 여행",
                fontSize = 16.sp
            )

            // --- 🚀 [1] 지금부터 이 아래 코드를 추가하세요! ---

            // 내용(소개)과 버튼/학번 사이에 큰 공간을 줍니다.
            // .weight(1f)는 '남은 공간을 모두 차지하라'는 뜻입니다.
            Spacer(modifier = Modifier.weight(1f))

            // '시작하기' 버튼
            Button(
                onClick = onStartClicked,  // TODO: 1-1. 클릭하면 다음 화면으로 넘어가기
                modifier = Modifier.fillMaxWidth() // 버튼 가로로 꽉 채우기
            ) {

            }

            // 버튼과 학번 사이에 약간의 공간을 줍니다.
            Spacer(modifier = Modifier.height(16.dp))

            // '학번' (Text)
            Text(
                text = "2022125032 유승, 2023128006 김민준, 2022125078 신진성 ", // (팀원 학번 추가)
                fontSize = 12.sp,
                color = Color.Gray // 눈에 덜 띄게 회색으로
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    Project11Theme {
        SplashScreen(onStartClicked = {})
    }
}

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    onFestivalClicked: () -> Unit
) {
    // 'Box'는 샌드위치처럼 요소를 겹쳐서 쌓을 때 사용합니다.
    Box(modifier = modifier.fillMaxSize()) {

        // 1. [맨 아래] 지도 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.andongmap), // 아까 넣은 지도 파일명
            contentDescription = "지도 배경",
            contentScale = ContentScale.Crop, // 화면에 꽉 차게 자르기
            modifier = Modifier.fillMaxSize()
        )

        // 2. [중간] 핀 아이콘 (지도 위에 둥둥 떠있음)
        Icon(
            painter = painterResource(id = R.drawable.ic_place), // 아까 만든 핀 아이콘
            contentDescription = "위치 핀",
            tint = Color.Red, // 빨간색으로 칠하기
            modifier = Modifier
                .size(48.dp) // 크기 키우기
                .align(Alignment.Center) // 화면 정중앙에 배치
        )

        // 3. [맨 위] '축제/행사' 버튼 (우측 하단 배치)
        Button(
            onClick = onFestivalClicked,
            modifier = Modifier
                .align(Alignment.BottomEnd) // 우측 하단 정렬
                .padding(16.dp) // 여백 주기
        ) {
            Text(text = "🎪 축제·행사")
        }

        // 4. [맨 위] 검색창 (상단 배치 - 일단 모양만)
        // (복잡하니까 일단 텍스트만 띄워볼게요)
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
                .fillMaxWidth(0.9f), // 가로 90% 채우기
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
            shadowElevation = 4.dp
        ) {
            Text(
                text = "🔍 안동",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

// ... (MapScreen 함수 끝) ...
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit // '뒤로가기(홈)' 기능
) {
    // 1. 세로 스크롤을 위한 상태 저장
    val scrollState = rememberScrollState()

    // 2. 찜하기 상태 저장 (눌렀는지 안 눌렀는지 기억)
    var isFavorite by remember { mutableStateOf(false) }


    var festivalTitle by remember { mutableStateOf("로딩 중...") }
    var festivalImageUrl by remember { mutableStateOf("") } // 이미지 주소 담을 변수

    LaunchedEffect(Unit) {
        val db = Firebase.firestore
        db.collection("festivals").document("andong").get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    festivalTitle = document.getString("title") ?: "제목 없음"
                    festivalImageUrl = document.getString("imageUrl") ?: "" // 주소 가져오기
                }
            }
    }



    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White) // 배경 흰색
            .verticalScroll(scrollState) // ⭐ 핵심: 세로 스크롤 기능 추가!
    ) {
        // --- [1] 대표 이미지 영역 ---
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)) {
            // [Coil] 인터넷 이미지 불러오기
            AsyncImage(
                model = festivalImageUrl, // Firebase에서 가져온 주소
                contentDescription = "축제 대표 사진",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                // 로딩 중이거나 실패했을 때 보여줄 임시 이미지 (지도)
                placeholder = painterResource(id = R.drawable.andongmap),
                error = painterResource(id = R.drawable.andongmap)
            )

            // 상단 아이콘 (공유, 찜하기)
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                // 공유 버튼 (기능은 나중에)
                IconButton(onClick = { /* TODO: 공유 Intent */ }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "공유",
                        tint = Color.Black
                    )
                }
                // 찜하기 버튼 (클릭하면 하트가 바뀜!)
                IconButton(onClick = { isFavorite = !isFavorite }) {
                    Icon(
                        // 찜 상태에 따라 아이콘 변경 (빈 하트 vs 꽉 찬 하트)
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "찜하기",
                        tint = Color.Red
                    )
                }
            }
        }

        // --- [2] 축제 기본 정보 ---
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "안동 국제 탈춤 페스티벌",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("🗓️ 기간: 2025.11.10 ~ 11.13", fontSize = 16.sp)
            Text("⏰ 시간: 매일 10:00 ~ 21:00", fontSize = 16.sp)
            Text("📍 장소: 안동 탈춤 공원 일대", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(24.dp))

            // --- [3] 티켓 정보 (유료) ---
            Text("🎟️ 티켓 정보", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color.DarkGray, shape = RoundedCornerShape(4.dp)) {
                    Text("[유료]", color = Color.White, modifier = Modifier.padding(4.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("1일권 20,000원", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Text("(성인 기준 / 상세 요금 보기 >)", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            // 예매 버튼
            Button(
                onClick = { /* TODO: 예매 사이트 연결 */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("예매하러 가기 >")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- [4] 축제 후기 ---
            Text("💬 축제 후기 (327)", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("⭐️ 4.9 / 5.0", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // 후기 필터 태그 (디자인만)
            Row {
                Surface(color = Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp)) {
                    Text("[ 📸 사진 후기 ]", modifier = Modifier.padding(8.dp), fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(color = Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp)) {
                    Text("[ 👍 추천 ]", modifier = Modifier.padding(8.dp), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("양실양실 님: 직접 탈춤 체험도 가능하고 먹거리도 많아서...", fontSize = 14.sp)

            Spacer(modifier = Modifier.height(50.dp)) // 맨 아래 여백
        }
    }
}
