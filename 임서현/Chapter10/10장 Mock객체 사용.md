# 목객체 사용

# 1. 문제

---

**동작**

- 클라이언트가 보낸 위도, 경도를 retrieve()에 전달
- 전달된 좌표 기반으로 생성된 Address 반환

```java
public class AddressRetriever {

    public Address retrieve(double latitude, double longitude)
        throws IOException, ParseException {
        String parms = String.format("lat=%.6flon=%.6f", latitude, longitude); //주소 형태 지정
        String response = new HttpImpl().get(
            "http://open.mapquestapi.com/nominatim/v1/reverse?format=json&"
                + parms);

        JSONObject obj = (JSONObject) new JSONParser().parse(response);

        JSONObject address = (JSONObject) obj.get("address");
        String country = (String) address.get("country_code");
       if (!country.equals("us")) {
          throw new UnsupportedOperationException(
              "cannot support non-US addresses at this time");
       }

        String houseNumber = (String) address.get("house_number");
        String road = (String) address.get("road");
        String city = (String) address.get("city");
        String state = (String) address.get("state");
        String zip = (String) address.get("postcode");
        return new Address(houseNumber, road, city, state, zip);
    }
}
```

```java
public class HttpImpl implements Http {
   public String get(String url) throws IOException {
      CloseableHttpClient client = HttpClients.createDefault();
      HttpGet request = new HttpGet(url);
      CloseableHttpResponse response = client.execute(request);
      try {
         HttpEntity entity = response.getEntity();
         return EntityUtils.toString(entity);
      } finally {
         response.close();
      }
   }
}
```

- HTTP 호출 실행하는 경우
    - 테스트 속도가 느리다.
    - Nominatim HTTP API가 항상 가용한지 보장 불가

→ 의존성이 있는 다른 코드와 분리해 retrieve() 메서드 로직에 관한 단위 테스트를 진행해야 한다.

# 2. 번거로운 동작을 스텁으로 대체

---

## 1. 스텁(Stub)

> 테스트 용도로 하드 코딩한 값을 반환하는 구현체
> 

HTTP 호출에서 반환 되는 JSON 응답을 하드 코딩해보자.

```java
 Http http = (String url) -> 
         "{\"address\":{"
         + "\"house_number\":\"324\","
         + "\"road\":\"North Tejon Street\","
         + "\"city\":\"Colorado Springs\","
         + "\"state\":\"Colorado\","
         + "\"postcode\":\"80903\","
         + "\"country_code\":\"us\"}"
         + "}";
```

테스트 대상 class에 생성자를 이용해 의존성 주입 받는다(주입 방식은 다양함)

**테스트 케이스 작성**

```java
@Test
   public void answersAppropriateAddressForValidCoordinates() 
         throws IOException, ParseException {
      Http http = (String url) ->
         "{\"address\":{"
         + "\"house_number\":\"324\","
         + "\"road\":\"North Tejon Street\","
         + "\"city\":\"Colorado Springs\","
         + "\"state\":\"Colorado\","
         + "\"postcode\":\"80903\","
         + "\"country_code\":\"us\"}"
         + "}";
      AddressRetriever retriever = new AddressRetriever(http);

      Address address = retriever.retrieve(38.0,-104.0);
      
      assertThat(address.houseNumber, equalTo("324"));
      assertThat(address.road, equalTo("North Tejon Street"));
      assertThat(address.city, equalTo("Colorado Springs"));
      assertThat(address.state, equalTo("Colorado"));
      assertThat(address.zip, equalTo("80903"));
   }
```

**동작 과정**

- Http의 스텁 인스턴스 생성 → JSON 문자열 반환
- AddressRetriever 스텁 저장
- retrieve() 내부 동작 실행
- 스텀 JSON 반환
- retrieve() JSON 문자열 파싱 후 Address 객체 구성
- 테스트는 반환 된 Address 객체 요소 검증

# 3. 스텁에 지능 더하기 : 인자 검증

---

### 스텁이 제대로 전달 됐음을 보장할 수 있을까?

```java
 Http http = (String url) ->
      {
         if(!url.contains("lat=38.00000&lon=-104.000000"))
            fail("url "+url + "dose not contain correct parms");
      }
         "{\"address\":{"
         + "\"house_number\":\"324\","
         + "\"road\":\"North Tejon Street\","
         + "\"city\":\"Colorado Springs\","
         + "\"state\":\"Colorado\","
         + "\"postcode\":\"80903\","
         + "\"country_code\":\"us\"}"
         + "}";
```

- 스텁에 필수적으로 받아야하는 값에 대한 조건문을 걸고 만일 조건을 충족하지 못하면 바로 fail() 실패처리한다.

## 목

의도적으로 흉내 낸 동작을 제공하고 수신한 인자가 모두 정산인지 여부를 검증하는 일을 하는 테스트 구조물

# 4. 목 도구를 사용하여 테스트 단순화

---

스텁을 목으로 변환하기

- 테스트에 어떤 인자를 기대하는지 명시
- get() 메서드에 넘겨진 인자 저장
- 저장된 인자가 이대하는 인자인지 테스트가 완료될 때 검증

## 모키토

<Build.Gradle>

```java
implementation group: 'org.mockito', name: 'mockito-core', version: '5.5.0'
```

```java
Http http = mock(Http.class);
      () -> when(http.get(contains("lat=38.00000&lon=-104.000000")))thenReturn(
          "{\"address\":{"
              + "\"house_number\":\"324\","
              + "\"road\":\"North Tejon Street\","
              + "\"city\":\"Colorado Springs\","
              + "\"state\":\"Colorado\","
              + "\"postcode\":\"80903\","
              + "\"country_code\":\"us\"}"
              + "}";
      );
```

- `when()` : 테스트 기대사항 설정
- `thenReturn()` : 기대사항이 충족되었을 때의 처리

→ 기대 사항이 충족되었을 때 목은 지정된 값을 반환

## 주입도구

모키토의 DI를 이용하자.

- `@Mock` : 목 인스턴스 생성
- `@InjectMocks` : 대상 인스턴스 변수 선언
- 대상 인스턴스를 인스턴스화 한 후에 `MockitoAnnotations.initMocks(this)` 호출

```java
public class AddressRetrieverTest {

   @Mock
   private Http http;
   @InjectMocks private AddressRetriever retriever;
   
   @BeforeEach
   public void createRetriever(){
      retriever = new AddressRetriever();
      MockitoAnnotations.initMocks(this);
   }
   
   @Test
   public void answersAppropriateAddressForValidCoordinates() 
         throws IOException, ParseException {
      Http http = mock(Http.class);
      () -> when(http.get(contains("lat=38.00000&lon=-104.000000")))thenReturn(
          "{\"address\":{"
              + "\"house_number\":\"324\","
              + "\"road\":\"North Tejon Street\","
              + "\"city\":\"Colorado Springs\","
              + "\"state\":\"Colorado\","
              + "\"postcode\":\"80903\","
              + "\"country_code\":\"us\"}"
              + "}";
      );

      AddressRetriever retriever = new AddressRetriever(http);

      Address address = retriever.retrieve(38.0,-104.0);
      
      assertThat(address.houseNumber, equalTo("324"));
      assertThat(address.road, equalTo("North Tejon Street"));
      assertThat(address.city, equalTo("Colorado Springs"));
      assertThat(address.state, equalTo("Colorado"));
      assertThat(address.zip, equalTo("80903"));
   }
```

```java
@Mock
   private Http http;
```

- Mock을 합성하고자 하는 곳을 의미

```java
@InjectMocks private AddressRetriever retriever;
```

- 목을 주입하고자 하는 대상

```java
MockitoAnnotations.initMocks(this);
```

- this 인수는 테스트 클래스 자체를 참조한다.
- 모키토는 테스트 클래스에서 @Mock 애너테이션이 붙은 필드를 가져와 각각에 대해 목 인스턴스를 합성한다. (`Http http = *mock*(Http.class);` 와 동일한 동작)
- @InjectMocks 애너테이션이 붙은 필드를 가져와 목 객체를 주입한다.
- 목 객체를 주입할 때 `생성자` → `setter 메서드` 순으로 탐색

```java
public class AddressRetriever {
   private Http http;

   ~~public AddressRetriever(Http http) {
      this.http = http;
   }~~

   public Address retrieve(double latitude, double longitude)
    ...
   }
}
```

- 기존에 목객체 주입을 위해 사용했던 생성자를 지워도 된다.

# 5. 목을 올바르게 사용할 때 중요한 것

---

- 진행하길 원하는 내용을 분명하게 기술해야 한다.
- 목이 프로덕션 코드의 동작을 올바르게 묘사하고 있는가?
- 프로덕션 코드는 생각하지 못한 다른 형식으로 반환하는가?
- 프로덕션 코드는 예외를 던지는가?
- null을 반환하는가?

목은 프로덕션 코드를 직접 테스트하고 있지 않음을 기억하기. 실제 클래스의 사용성을 보여주는 적절한 상위 테스트 준비 필요.