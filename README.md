# 🌿 [소도시 쉼표] - 지친 현대인을 위한 힐링 여행 가이드

## 1. 프로젝트 개요 (Project Overview)
주제: 복잡한 대도시 생활에 지친 현대인들에게 '쉼표'를 선물하는 소도시 여행 추천 앱. 힐링을 위한 '숨겨진 장소'와 '지역 축제' 정보를 시각적이고 효율적으로 제공합니다.

핵심 문제 해결:  기존 앱의 한계인 '정적인 장소' 위주 검색에서 벗어나, 사용자가 방문한 소도시의 '동적인 지역 행사(축제)' 정보를 실시간으로 제공합니다.

---

## 2. 주요 기능 및 구현 (Core Features & Implementation)

| 기능 | 사용자 경험 | 구현 기술 (Android Studio) |
| :--- | :--- | :--- |
| 화면 전환 | 시작 버튼 클릭 시 부드럽게 지도 화면으로 전환 | Jetpack Compose State Management (`remember { mutableStateOf }`, `when` 문) |
| 데이터 로딩 | 축제 이름, 기간, 가격 등이 실시간으로 표시됨 | Firebase Firestore에서 JSON 데이터를 직접 `GET` 요청하여 화면에 표시 (API 연동 대체) |
| 이미지 처리 | 축제 대표 이미지가 URL에서 로딩됨 | Coil 라이브러리를 사용해 인터넷 주소(URL)에서 사진을 비동기적으로 다운로드 및 표시 |
| 찜하기 (Like) | 하트(♡)를 누르면 빨간색 하트(❤️)로 변경 | Button Click Listener 및 이미지 리소스 변경 (`setImageResource`)을 이용한 UI 상태 토글 구현 |
| 예매/공유 | 버튼 클릭 시 외부 앱(브라우저/카톡)이 열림 | Android Intent (공유: `ACTION_SEND`, 예매: `ACTION_VIEW`)를 사용해 OS 기능 호출 |
| 필터 UI | 후기 섹션에 사진/추천/주차 팁 태그가 표시됨 | Jetpack Compose의 `ChipGroup` 레이아웃을 사용해 추후 필터링 로직을 위한 기초 UI 구축 |

---

## 3. 기술 스택 (Tech Stack)

* Frontend UI: Kotlin / Jetpack Compose
* Backend (Data): Google Firebase Firestore (NoSQL Database)
* Image Loading: Coil (Async Image Loading)
* Navigation: Compose State Management (`when` based routing)
* Core Feature Logic: Intent (공유/예매), ClickListener (찜하기)

---

## 4. 실행 및 시연 방법 (Demo Guide)

1.  앱 실행 후 [여행 시작하기] 버튼을 클릭합니다.
2.  지도 화면에서 [ 🎪 축제·행사 ] 버튼을 클릭하여 상세 페이지로 이동합니다.
3.  상세 페이지에서 [ 안동 국제 탈춤 페스티벌 ] 제목이 '로딩 중...'에서 Firebase 데이터로 바뀌는 것을 확인합니다.
4.  [ ❤️ ] 아이콘을 클릭하여 '찜하기' 기능이 작동하는 것을 시연합니다.
5.  [ 예매하러 가기 > ] 버튼을 클릭하여 웹 브라우저가 열리는 것을 확인합니다.
