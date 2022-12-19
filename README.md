## Textbook - 실무에 바로 적용하는 안드로이드 프로그래밍 제4판 (코틀린)

- TaeungQuiz (GeoQuiz 변형)

- Button - clickable 적용해서 답을 누른 문제는 재터치 불가능

- ImageButton - drawable vector asset 사용

- onBackPressed - 뒤로가기 2번 터치해야 종료 구현

- ViewModel 적용해서 화면회전과 같은 구성 변경시 데이터 보존
- Home 버튼을 눌러서 Stop상태인 경우 안드로이드 OS가 메모리에서 삭제할 수 있는데 이때는 보존되지 않음. onDestroy, onCleared 호출 안됨
- 이를 해결하기 위해 SIS 사용. 일시적으로 외부에 데이터를 저장. 소량의 데이터를 저장할 때 유용. 
### 2022.12.18 4장까지
