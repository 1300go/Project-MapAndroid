

package com.example.project11

//  [이미지 관련] (Coil)
// [파이어베이스 관련] (Firebase)
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // ... class MainActivity ... onCreate ...
        setContent {
            Project11Theme {
                val context = LocalContext.current

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
                            onFestivalClicked = { currentScreen = 3 }, // ⬅️ 여기가 해결되는 부분!
                            onListClicked = {
                                val intent = Intent(context, Reserve2::class.java)
                                context.startActivity(intent)
                            } ,
                            onTravelClicked = {
                                val intent = Intent(context, TravelActivity::class.java)
                                context.startActivity(intent)
                            }

                        )
                    }

                    3 -> {
                        // [3. 상세 화면] 뒤로가기 누르면 -> 2번(지도)으로 이동
                        DetailScreen(
                            modifier = Modifier.fillMaxSize(),
                            onBackClicked = { currentScreen = 2 },
                            onReserveClicked = {
                                // Reserve1::class.java 는 이동하려는 액티비티 클래스 이름입니다.
                                // 실제 파일명(클래스명)과 정확히 일치해야 합니다.
                                val intent = Intent(context, Reserve1::class.java)
                                context.startActivity(intent)
                            }

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
    // 1. 배경 설정: Surface 대신 Box를 사용하여 배경색 위에 이미지를 겹칩니다.
    Box(
        modifier = modifier
            .fillMaxSize()
            // 피그마의 톤다운된 올리브 색상 배경 (0xFFF0F5E8에 가까움)
            .background(Color(0xFFF0F5E8)) // 기존 아이보리(0xFFF5F5F0)보다 톤다운
    ) {
        // [클로버 배경 패턴]이 res/drawable에 'clover_pattern' 같은 이름으로 있다고 가정하고 추가
        // 만약 배경 패턴 이미지가 없다면 이 코드를 제거하고 배경색만 사용합니다.
        Image(
            painter = painterResource(id = R.drawable.cloverimage),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.5f // 배경 패턴의 투명도를 낮춰 메인 요소가 잘 보이도록 설정
        )


        // 2. Column: 모든 요소를 세로로, 가운데 정렬
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp), // 피그마처럼 여백을 더 넓게 설정
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // '로고' (Image): 피그마처럼 크게 중앙에 배치
            Image(
                // R.drawable.ic_eco를 클로버 모양 아이콘으로 가정합니다.
                painter = painterResource(id = R.drawable.ic_eco),
                contentDescription = "앱 로고",
                modifier = Modifier.size(120.dp), // 로고 크기 확대
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFF556B2F)) // 로고 색상을 올리브 톤으로 지정
            )

            // 로고와 앱 이름 사이에 공간
            Spacer(modifier = Modifier.height(32.dp))

            // '앱 이름' (Text)
            Text(
                text = "쉼표",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray // 텍스트 색상도 톤다운
            )

            // 한 줄 소개
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "지친 일상 속, 쉼표가 되는 여행",
                fontSize = 18.sp,
                color = Color.Gray
            )

            // 내용과 버튼/학번 사이에 큰 공간
            Spacer(modifier = Modifier.weight(1f))

            // 3. '쉼표 찾으러 가기' 버튼 (피그마 디자인 적용)
            // 'OutlinedButton'을 사용하여 테두리가 있는 디자인 구현
            androidx.compose.material3.OutlinedButton(
                onClick = onStartClicked,
                modifier = Modifier
                    .fillMaxWidth(0.7f) // 버튼 가로 길이 70%로 조정 (피그마와 유사)
                    .height(56.dp), // 버튼 높이 증가
                shape = RoundedCornerShape(8.dp), // 모서리 둥글게
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF556B2F)) // 올리브색 테두리
            ) {
                Text(
                    text = "쉼표 찾으러 가기",
                    fontSize = 18.sp,
                    color = Color.DarkGray, // 텍스트 색상
                    fontWeight = FontWeight.SemiBold
                )
            }

            // 버튼과 학번 사이에 공간
            Spacer(modifier = Modifier.height(48.dp))

            // '학번' (Text) - 피그마처럼 글자 크기를 키워 강조
            Text(
                text = "2022125032 유승\n2023128006 김민준\n2022125078 신진성",
                fontSize = 14.sp, // 글자 크기 조금 키움
                color = Color.DarkGray,
                lineHeight = 24.sp, // 줄 간격 추가
                // 피그마처럼 중앙 정렬
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
    onFestivalClicked: () -> Unit,
    onListClicked: () -> Unit,
    onTravelClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
        ) {
            Text(
                text = "숨겨진 소도시 찾기",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "소도시 검색",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                color = Color.White
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "검색",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "안동",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "카테고리",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CategoryItem(text = "[ 전체 ]", isSelected = true)
                CategoryItem(text = "[ 축제 ]")
                CategoryItem(text = "[ 카페 ]")
                CategoryItem(text = "[ 맛집 ]")
                CategoryItem(text = "[ 놀거리 ]")
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.andongmap),
                contentDescription = "지도",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_place),
                contentDescription = "핀",
                tint = Color.Red,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)
                    .padding(bottom = 20.dp)
            )

            Button(
                onClick = onFestivalClicked,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                shape = RoundedCornerShape(50),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3F51B5)
                )
            ) {
                Text(text = "🎪 축제·행사")
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                .clickable { onTravelClicked() },
                shape = RoundedCornerShape(50),
                color = Color(0xFFEEEEEE),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = "📍 여행 명소",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                BottomNavItem(icon = Icons.Default.Home, text = "홈", isSelected = false)
                BottomNavItem(icon = Icons.Default.Place, text = "지도", isSelected = true)
                BottomNavItem(icon = Icons.Default.DateRange, text = "내 일정", isSelected = false)
                BottomNavItem(
                    icon = Icons.Default.List,
                    text = "목록",
                    isSelected = false,
                    onClick = onListClicked
                )
            }
        }
    }
}

@Composable
fun CategoryItem(text: String, isSelected: Boolean = false) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = if (isSelected) Color.Black else Color.Gray
    )
}

@Composable
fun BottomNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, isSelected: Boolean,onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = if (isSelected) Color.Black else Color.Gray,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = text,
            fontSize = 10.sp,
            color = if (isSelected) Color.Black else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ... (MapScreen 함수 끝) ...
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit,
    onReserveClicked: () -> Unit
) {
    val scrollState = rememberScrollState()
    // 1. 하트 상태 관리 (처음엔 빈 하트 false로 시작)
    var isFavorite by remember { mutableStateOf(false) }

    // 데이터 변수
    var festivalTitle by remember { mutableStateOf("안동 국제 탈춤 페스티벌") }
    var festivalImageUrl by remember { mutableStateOf("") }

    // 파이어베이스 데이터 가져오기
    LaunchedEffect(Unit) {
        val db = Firebase.firestore
        db.collection("festivals").document("andong").get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    festivalTitle = document.getString("title") ?: "안동 국제 탈춤 페스티벌"
                    festivalImageUrl = document.getString("imageUrl") ?: ""
                }
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
    ) {
        // [1] 상단 이미지 영역
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            AsyncImage(
                model = festivalImageUrl,
                contentDescription = "축제 사진",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(id = R.drawable.andongmap),
                error = painterResource(id = R.drawable.andongmap)
            )

            // 상단 버튼들 (공유, 찜하기)
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "공유",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))


                IconButton(onClick = { isFavorite = !isFavorite }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "찜하기",
                        // 눌리면 빨강, 안 눌리면 흰색
                        tint = if (isFavorite) Color.Red else Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // 뒤로가기 버튼
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
            ) {
                // 아이콘 필요 시 추가
            }
        }

        // [2] 상세 정보 내용
        Column(modifier = Modifier.padding(20.dp)) {

            // 제목
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = festivalTitle,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                // 제목 옆 작은 하트 (상태 연동)
                IconButton(onClick = { isFavorite = !isFavorite }) {
                    Icon(
                        imageVector = if (isFavorite)
                            Icons.Default.Favorite
                        else
                            Icons.Default.FavoriteBorder,
                        contentDescription = "찜하기",
                        tint = if (isFavorite) Color.Red else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 정보 행
            InfoRow(icon = "🗓️", text = "기간: 2024.11.10 ~ 11.13")
            InfoRow(icon = "⏰", text = "시간: 매일 10:00 ~ 21:00")
            InfoRow(icon = "📍", text = "장소: 안동 탈춤 공원 일대")

            Spacer(modifier = Modifier.height(24.dp))
            Divider(thickness = 1.dp, color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(24.dp))

            // 티켓 정보
            Text("🎟️ 티켓 정보", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color(0xFF555555), shape = RoundedCornerShape(4.dp)) {
                    Text("[유료]", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(6.dp, 2.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("1일권 20,000원", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Text("(성인 기준 / 상세 요금 보기 >)", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))

            Spacer(modifier = Modifier.height(20.dp))

            // 예매 버튼
            Button(
                onClick = onReserveClicked, // 화면 이동 추가
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("예매하러 가기 >", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(30.dp))
            Divider(thickness = 8.dp, color = Color(0xFFF5F5F5))
            Spacer(modifier = Modifier.height(30.dp))

            // 주요 행사
            Text("🎪 주요 행사", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            EventItem(icon = "🎭", title = "탈춤 체험 부스", desc = "직접 탈을 쓰고 춤을 배워보세요!")
            EventItem(icon = "🥘", title = "안동 먹거리 장터", desc = "찜닭, 간고등어 등 지역 별미")
            EventItem(icon = "🌍", title = "세계 탈 전시관", desc = "희귀한 전 세계 탈 구경")

            Text("(행사 더보기 >)", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.height(30.dp))
            Divider(thickness = 8.dp, color = Color(0xFFF5F5F5))
            Spacer(modifier = Modifier.height(30.dp))

            // 축제 후기
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💬 축제 후기(327)", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("⭐️ 4.9", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(" / 5.0", fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                ReviewTag("📸 사진 후기")
                Spacer(modifier = Modifier.width(8.dp))
                ReviewTag("👍 추천")
                Spacer(modifier = Modifier.width(8.dp))
                ReviewTag("🚗 주차 팁")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 리뷰 내용
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(24.dp), shape = androidx.compose.foundation.shape.CircleShape, color = Color.LightGray) {}
                Spacer(modifier = Modifier.width(8.dp))
                Text("덩실덩실 님", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(" · 1일 전", color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("직접 탈춤 체험도 가능하고 먹거리도 많아서 아이들과 함께 방문하기 너무 좋아요!!! 내년에도 또 오고 싶네요.", fontSize = 14.sp, lineHeight = 20.sp)

            Spacer(modifier = Modifier.height(12.dp))

            // 하단 사진들
            Row(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(model = festivalImageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(8.dp)), placeholder = painterResource(R.drawable.andongmap))
                Spacer(modifier = Modifier.width(8.dp))
                Image(painter = painterResource(id = R.drawable.andongmap), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(8.dp)))
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f).height(80.dp)) {
                    Image(painter = painterResource(id = R.drawable.ic_eco), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)))
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp)))
                    Text("+3", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                }
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

// 보조 함수

@Composable
fun InfoRow(icon: String, text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 15.sp, color = Color(0xFF444444))
    }
}

@Composable
fun EventItem(icon: String, title: String, desc: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(desc, fontSize = 13.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ReviewTag(text: String) {
    Surface(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(4.dp)) {
        Text(text, fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}

@Composable
fun Divider(thickness: androidx.compose.ui.unit.Dp, color: Color) {
    Box(modifier = Modifier.fillMaxWidth().height(thickness).background(color))
}

data class Place(
    val name: String = "",       // place_name
    val address: String = "",    // place_address
    val imageUrl: String = "",   // place_image
    val lat: Double = 0.0,       // place_geo [0]
    val lng: Double = 0.0,        // place_geo [1]
    val localName: String = ""
)