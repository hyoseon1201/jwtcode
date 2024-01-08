# jwtcode
jwt토큰 코드입니다

로그인 테스트할때 json으로 아이디, 비밀번호 적어서 넘기면 안되고, x-www-form에다가 username이랑 password로 담아서 보내야됨 (시큐리티 기본 로그인폼임)

/test로 get요청 보낼때는 헤더 Authorization에다가 Bearer 토큰값 <- 이거 넣어서 요청 하면 됨. 인증 제대로 적용되고 있나 확인하려고 만든 api 엔드포인트임

리프레시 토큰은 json 바디로 refreshToken 키에다가 토큰값 밸류로 넣은다음, 만료된 엑세스 토큰을 헤더에다가 위에처럼 넣어서 보내면

새로운 엑세스 토큰이 발급된다

회원가입할때는

{
    "id":"test",
    "password":"qwer1234"
}

이런식으로 id랑 password로 넣어서 보내주면 됨

인증처리할때는 시큐리티 config 클래스와 jwtCheckFilter 클래스 두 곳을 전부 설정 해줘야 한다.

UserDTO는 시큐리티 UserDetails 인터페이스 구현해놓은 시큐리티의 User 클래스를 상속받아서 만든거임.

엑세스토큰이랑 리프레시 토큰은 시큐리티 패키지의 ApiLoginSuccessHandler 클래스 내에서 설정한다.


부트 3.1.7 버전에서 컨벤션에 넣어놨던 디펜던시들 추가해놓았음. 그냥 기억나는대로 해서 정확하진 않음..

그 외에 따로 추가한 디펜던시들은 주석으로 표시해놓음

Gson 이랑 jjwt 어쩌구 인데, 

gson은 자바 객체랑 json 서로 변환시켜주는 라이브러리임

아까 말한 ApiLoginSuccessHandler 클래스 들어가면 시큐리티 User 상속받아서 만들었던 UserDTO에서

getClaims 메서드로 데이터 가져오는데, 여기다가 엑세스토큰이랑 리프레시토큰 만들어서 넣어준다.

그다음 Gson써서 제이슨으로 바꿔가지고 뭐 어떻게 저떻게 하는듯

jjwt 어쩌구들은 jwt토큰 만드는데  쓰는 라이브러리들인데, 자세한건 잘 모르겠다.

토큰 만드는 코드는 JwtUtil에 있다. key는 맘대로 바꿔도 되는데 30자 이상이어야 정상적으로 jwt토큰이 나온다고 하니까

너무 짧게만 안쓰면 되는것같음.




