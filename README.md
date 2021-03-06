# IDOL- I Design on Link

<H1>1. 개요</H1>
<image src="https://github.com/mtsealove/IDOL/blob/master/doc/icon.png">
IDoL은 I Design on Link의 약자로로, <br>
퀵 , KTX특송, 지하철 택배 등에 고속버스 특송업계가 합쳐진 ‘여객운송수단 복합운송’을 구축하는 플랫폼입니다.<br>
각 업체들의 데이터들을 통합하여 하나의 모바일 애플리케이션에서 한번에 신청할 수 있는 플랫폼을 구축하는 것이 목표입니다.<br>
18.09.26~18.11.22의 기간동안 개발했으며 이후 유지보수를 실시했습니다.

<H1>2. 요구사항</h1>
<pre>
- 사용자는 키보드 입력을 통해 보내는 사람의 이름, 연락처, 받는 사람의 이름, 연락처, 화물의 종류, 무게를 입력한다.
- 사용자는 검색을 통해 출발지, 위치를 선택한다.
- 사용자는 최소비용, 최단경로, 추천경로를 선택한다.
- 애플리케이션은 사용자에게 사용자가 요구한 경로의 경유지와 비용을 보여준다.
- 사용자는 결제 방법을 선택한다.
- 사용자는 배송 기사에게 보낼 메세지를 입력한다.(옵션)
- 사용자는 자신의 이용 기록을 확인할 수 있다.
- 사용자는 공지사항과 FAQ를 확인하고 댓글을 작성할 수 있다.
</pre>
<H1>3. 개발환경</H3>
<h3>Client</h3>
<h5>소프트웨어</h5>
<image width="200" height="200" src="https://github.com/mtsealove/IDOL/blob/master/doc/ubuntu.svg">
운영체제: Ubuntu 18.04 lts<br>
Middle ware: JDK8<br>
개발 IDE: Android Studio 3.1<br>
개발 언어: JAVA<br>
SDK: SDK version 23<br>
사용 API: Google Map
<h5>하드웨어</h5>
CPU: Intel i7-8550u<br>
RAM: 8Gb<br>
SSD: 256Gb
<h5>테스트 기기</h5>
 기종: 삼성 Galaxy S8<br>
 CPU: Exynos 8895 Octa<br>
 RAM: 4Gb<br>
 운영체제: Anroid 8.0 (SDK 26)
<hr>
<h3>Server</h3>
IDoL은 Firebase의 실시간 데이터베이스를 이용하기 때문에 서버에 대한 개발은 실시하지 않습니다.

<h1>4. 운영환경</h1>
<h3>권장사양</h3>
<h5>하드웨어</h5>
CPU: Qualcomm Snapdragon 625 이상 || Samsung Exynos Exynos 5422 Octa 이상<br>
RAM: 2Gb 이상<br>
저장공간: 여유공간 1Gb 이상<br>
<h5>소프트웨어</h5>
운영체제: Android Android 6.0 (SDK 23) 이상
<image witdh="400" height="300" src="https://github.com/mtsealove/IDOL/blob/master/doc/Android.png">
<hr>
* 권장사양의 하드웨어적인 요건을 만족하지 못해도 소프트웨어가 실행될 수는 있으나 소프트웨어 요건이 충분하지 않으면 실행이 되지 않을 수도 있습니다.

<h1>5. 데이터베이스</h1>
 IDoL은 SQL을 이용한 MySQL이나 OracleDB를 이용하지 않고 구글의 Firebase의 Realtime Database를 이용합니다.<br>
 <image width=500 height=250 src="https://github.com/mtsealove/IDOL/blob/master/doc/firebase.jpeg">
 구글의 Firebase를 이용함으로서 개발자는 서버를 구축할 필요가 없습니다.<br>
 그리고 실시간으로 데이터의 Create, Reat, Update, Delete를 진행하고 웹과 애플리케이션에서는<br>
 실시간으로 업데이트된 데이터에 접근할 수 있습니다.
 <h3>child</h3>
 IDoL 프로젝트의 Child는 5가지로 분류합니다.<br>
 Account, Links, FAQ, Notice가 그것입니다.<br>
 <h5>Account</h5>
 <pre>
 Account는 계정을 관리하는 child입니다.
 ID, 비밀번호, 이름, 전화번호 그리고 사용 기록을 관리합니다.
 사용 기록은 Account의 하위 클래스<계정이름>의 하위 클래스LOG에 저장됩니다.
 LOG에는 사용시 입력한 모든 정보가 저장됩니다.
 로그인 시 Client에서 계정의 ID와 비밀번호에 대한 정보를 Firebase Server에서 요청하고
 Firebase Server는 해당 계정 정보를 반환합니다.</pre>
 <h5>Links</h5>
 <pre>
 Links는 경로에 대한 데이터가 저장되어 있습니다.
 Links의 하위 클래스에는 업체명, 경로, 비용, 소요 시간 등의 경유지와 업체의 가격에 대한 정보 등이 저장됩니다.
 Client에서는 Links의 모든 요청에 대한 정보를 요청하고 Firebase Server는 모든 하위 클래스들을 Client에 전송합니다.</pre>
 <h5>FAQ, Notice</h5>
 <pre>
 FAQ와 공 게시글에 관한 하위 크래스입니다.
 제목과 내용에 대한 정보가 저장되어 있으며 각 하위 클래스는 댓글 정보를 포함하고 있습니다.</pre>

<h1>6. 알고리즘</h1>
1. 먼저 Firebase 데이터베이스에 접근하여 Links의 Children을 읽어와 Logic 클래스로 메모리에 저장합니다.<br>
2. 경로를 선택하면 해당 경로에서 경유해야될 경로를 새 Logic인스턴스의 배열에 저장합니다.<br>
3. 최적의 경우의 수를 저장할 2개의 Logic 인스턴스 배열 best, best2를 생성합니다.<br>
   첫 번째 경유지에서 경유지 1을 선택할 경우와 경유지 2를 선택할 경우입니다.
<h3>6.1 최소비용</h3>
4. 경유지 1을 선택한 경우 각 케이스에서 비용이 가장 적은 인스턴스를 best에 저장합니다.<br>
5. 경유지 2을 선택한 경우 각 케이스에서 비용이 가장 적은 인스턴스를 best2에 저장합니다.<br>
6. best와 best2의 각 비용을 모두 합쳐 비교합니다.<br>
7. 더 적은 비용을 가진 인스턴스 배열을 출력합니다.
<h3>6.2 최단시간</h3>
4. 경유지 1을 선택한 경우 각 케이스에서 시간이 가장 적은 인스턴스를 best에 저장합니다.<br>
5. 경유지 2을 선택한 경우 각 케이스에서 시간이 가장 적은 인스턴스를 best2에 저장합니다.<br>
6. best와 best2의 각 시간을 모두 합쳐 비교합니다.<br>
7. 더 적은 시간을 가진 인스턴스 배열을 출력합니다.
 <h3>6.3 추천경로</h3>
4. 모든 경우에 대하여 시간과 비용을 곱하여 '가성비' 변수를 가져옵니다.(작을수록 좋음)<br>
5. 경유지 1을 선택한 경우 각 케이스에서 가성비 변수 값이 가장 작은 인스턴스를 best에 저장합니다.<br>
6. 경유지 2을 선택한 경우 각 케이스에서 가성비 변수 값이 가장 작은 인스턴스를 best2에 저장합니다.<br>
7. best와 best2의 각 가성비 값을 모두 합쳐 비교합니다.<br>
8. 더 작은 가성비 변수 값을 가진 인스턴스 배열을 출력합니다.

<h3>6.4 직접 설정</h3>
3. Logic 인스턴스 배열의 모든 내용을 사용자에게 출력합니다.<br>
4. 사용자는 앞의 경유지부터 차례대로 선택하고 다음 사용자의 선택에 따라 해당되지 않는 경유지는 비활성화됩니다.<br>
ex) 사용자가 고속터미널을 선택하면 KTX의 선택지는 사용자에게 보여지지 않는다.<br>
5. 사용자가 선택한 경로의 시간과 비용을 출력합니다.

<h1>7. 주요 UI</h1>
로딩 UI
<image width="300" height="600" src="https://github.com/mtsealove/IDOL/blob/master/doc/1.jpg">
 메인 UI
 <image width="300" height="600" src="https://github.com/mtsealove/IDOL/blob/master/doc/2.jpg">
  메인 UI - 슬라이드 메뉴
  <image width="300" height="600" src="https://github.com/mtsealove/IDOL/blob/master/doc/3.jpg">
   로그인 UI
   <image width="300" height="600" src="https://github.com/mtsealove/IDOL/blob/master/doc/14.jpg">
    회원가입 UI
   <image width="300" height="600" src="https://github.com/mtsealove/IDOL/blob/master/doc/15.jpg">
   위치 검색 UI
   <image width="300" height="600" src="https://github.com/mtsealove/IDOL/blob/master/doc/4.jpg">
    화물 종류 설정 UI
    <image width="300" height="600" src="https://github.com/mtsealove/IDOL/blob/master/doc/5.jpg">
     차량 종류 선택 UI
     <image width="300" height="600" src="https://github.com/mtsealove/IDOL/blob/master/doc/6.jpg">
      추천경로 출력 UI (최단시간과 최소 시간은 동일한 UI를 가집니다)
      <image width="300" height="600" src="https://github.com/mtsealove/IDOL/blob/master/doc/7.jpg">
       추천경로 출력 UI (자세히)
       <image width="300" height="600" src="https://github.com/mtsealove/IDOL/blob/master/doc/8.jpg">
        경로 직접 선택 UI
        <image width="300" height="600" src="https://github.com/mtsealove/IDOL/blob/master/doc/9.jpg">
         결제수단 선택 UI
         <image width="300" height="600" src="https://github.com/mtsealove/IDOL/blob/master/doc/10.jpg">
          결제 확인 UI
          <image width="300" height="600" src="https://github.com/mtsealove/IDOL/blob/master/doc/11.jpg">
           주문 내역 UI (슬라이드 메뉴 / 사용자 이름 클릭)
           <image width="300" height="600" src="https://github.com/mtsealove/IDOL/blob/master/doc/12.jpg">
            관리자 UI (로그인 화면/ ID: manager pw: password)
            <image width="300" height="600" src="https://github.com/mtsealove/IDOL/blob/master/doc/13.jpg">
            <h1>8. 설치파일 위치</h1>
            https://github.com/mtsealove/IDOL/blob/master/doc/IDOL.apk
