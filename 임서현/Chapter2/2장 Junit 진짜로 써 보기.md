# 예제
---

```java
public class ProfileTest {

    @Test
    public void matchAnswerFalseWhenMustMatchCriteriaNotMet() {
        // 질문
        Question question = new BooleanQuestion(1, "상여를 받았습니까?");

        // 사용자 프로파일 생성하기
        Profile profile = new Profile("Bull Hockey, Inc.");  // 매칭 요청 사용자
        Answer profileAnswer = new Answer(question, Bool.FALSE);
        profile.add(profileAnswer);

        // 기준 프로파일 생성하기
        Criteria criteria = new Criteria();
        Answer criteriaAnswer = new Answer(question, Bool.TRUE);
        Criterion criterion = new Criterion(criteriaAnswer, Weight.MustMatch);
        criteria.add(criterion);

        boolean matches = profile.matches(criteria);

        assertFalse(matches);
    }

    @Test
    public void matchAnswerTrueWhenMustMatchCriteriaNotMet() {
        // 질문
        Question question = new BooleanQuestion(1, "상여를 받았습니까?");

        // 사용자 프로파일 생성하기
        Profile profile = new Profile("Bull Hockey, Inc.");  // 매칭 요청 사용자
        Answer profileAnswer = new Answer(question, Bool.FALSE);
        profile.add(profileAnswer);

        // 기준 프로파일 생성하기
        Criteria criteria = new Criteria();
        Answer criteriaAnswer = new Answer(question, Bool.TRUE);
        Criterion criterion = new Criterion(criteriaAnswer, Weight.DontCare);
        criteria.add(criterion);

        boolean matches = profile.matches(criteria);

        assertTrue(matches);
    }

}

```

현재 질문, 사용자 프로파일, 기준 프로파일 생성하는 부분에서 상당하게 중복된다.

이 때 사용하는 것이 `@Before` 이다.

# @BeforeEach

(교재에는 Before를 사용하고 있으나 Junit5에서 BeforeEach로 변경되어 BeforeEach를 사용한다.)

---

- 각각의 @Test 메소드 보다 먼저 실행되는 메소드이다. 이를 이용해 초기화할 수 있다.

아래는 BeforeEach를 이용해 질문 생성을 초기화해 사용하는 방법이다.

```java
public class ProfileTest {
    private Profile profile;
    private BooleanQuestion question;
    private Criteria criteria;

    @BeforeEach
    public void create(){
        question = new BooleanQuestion(1, "상여를 받았습니까?");
        profile = new Profile("Bull Hockey, Inc.");
        Criteria criteria = new Criteria();
    }

    @Test
    public void matchAnswerFalseWhenMustMatchCriteriaNotMet() {
     
        Answer profileAnswer = new Answer(question, Bool.FALSE);
        profile.add(profileAnswer);
        
        Answer criteriaAnswer = new Answer(question, Bool.TRUE);
        Criterion criterion = new Criterion(criteriaAnswer, Weight.MustMatch);
        criteria.add(criterion);

        boolean matches = profile.matches(criteria);

        assertFalse(matches);
    }

    @Test
    public void matchAnswerTrueWhenMustMatchCriteriaNotMet() {

        Answer profileAnswer = new Answer(question, Bool.FALSE);
        profile.add(profileAnswer);
        
        Answer criteriaAnswer = new Answer(question, Bool.TRUE);
        Criterion criterion = new Criterion(criteriaAnswer, Weight.DontCare);
        criteria.add(criterion);

        boolean matches = profile.matches(criteria);

        assertTrue(matches);
    }

}
```

## **Jnunit의 동작 과정**

1. 새로운 Test 인스턴스를 만든다. (이때 profile, question, criteria 필드는 초기화 되지 않는다.)
2. `@BeforeEach`를 호출해 profile, question, criteria를 인스턴스로 초기화한다.
3. `@test` 메서드를 실행하고 테스트 결과를 표기한다.
4. 다른 테스트를 위해 다시 Test 인스턴스를 새로 생성한다
5. 이하 생략

**최종 리펙터링 코드(테스트 코드 인사이드)**

```java
public class ProfileTest {

    private Profile profile;
    private BooleanQuestion question;
    private Criteria criteria;

    @BeforeEach
    public void create() {
        question = new BooleanQuestion(1, "상여를 받았습니까?");
        profile = new Profile("Bull Hockey, Inc.");
        criteria = new Criteria();
    }

    @Test
    public void matchAnswerFalseWhenMustMatchCriteriaNotMet() {

        profile.add(new Answer(question, Bool.FALSE));
        criteria.add(new Criterion(new Answer(question, Bool.TRUE), Weight.MustMatch));

        boolean matches = profile.matches(criteria);

        assertFalse(matches);
    }

    @Test
    public void matchAnswerTrueWhenMustMatchCriteriaNotMet() {

        profile.add(new Answer(question, Bool.FALSE));
        criteria.add(new Criterion(new Answer(question, Bool.TRUE), Weight.DontCare));

        boolean matches = profile.matches(criteria);

        assertTrue(matches);
    }
}
```