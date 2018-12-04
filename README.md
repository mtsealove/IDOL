# IDOL- I Design on Link

<H1>1. 개요</H1>
<pre>
IDoL은 I Design on Link의 약자로 퀵서비스, 
KTX특송, 지하철 택배 등에 고속버스 특송업계가 합쳐진 ‘여객운송수단 복합운송’을 구축하는 플랫폼입니다.
각 업체들의 데이터들을 통합하여 하나의 모바일 애플리케이션에서 한번에 신청할 수 있는 플랫폼을 구축하는 것이 목표입니다.
</pre>

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
운영체제: Ubuntu 18.04 lts<br>
Middle ware: JDK8<br>
개발 IDE: Android Studio 3.1<br>
개발 언어: JAVA
SDK: SDK version 23
<h5>하드웨어</h5>
CPU: Intel i7-8550u<br>
RAM: 8Gb<br>
SSD: 256Gb
<hr>
<h3>Server</h3>
IDoL은 Firebase의 실시간 데이터베이스를 이용하기 때문에 서버에 대한 개발은 실시하지 않는다

<h1>4. 운영환경</h1>
<h3>권장사양</h3>
<h5>하드웨어</h5>
CPU: Qualcomm Snapdragon 625 이상 || Samsung Exynos Exynos 5422 Octa 이상<br>
RAM: 1Gb 이상<br>
저장공간: 여유공간 1Gb 이상<br>
<h5>소프트웨어</h5>
운영체제: Android Android 6.0 (SDK 23) 이상
<hr>
* 권장사양의 하드웨어적인 요건을 만족하지 못해도 소프트웨어가 실행될 수는 있으나 소프트웨어 요건이 충분하지 않으면 실행이 되지 않을 수도 있습니다.

<h1>5. 데이터베이스</h1>
 IDoL은 SQL을 이용한 MySQL이나 OracleDB를 이용하지 않고 구글의 Firebase의 Realtime Database를 이용합니다.<br>
 <image width=200 height=100 src="https://github.com/mtsealove/IDOL/blob/master/doc/firebase.jpeg">
