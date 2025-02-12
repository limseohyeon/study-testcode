# 1. 단언

---

단언은 아래와 같이 두가지 종류가 있다.

- 전통적인 방식의 단언
- 햄크레스트 단언

## 1.1. assertTrue

- 전통적인 단언
- `org.junit.Assert.assertTrue(someBooleanExpression)`보통 static import를 사용한다.

```java
@Test
    public void hasPositiveBalance() {
        account.deposit(50);
        assertTrue(account.hasPositiveBalance());
    }
```

## 1.2. assertThat

- 햄크레스트 단언
- 검증하고자 하는 값을 **명확한 값으로 비교**한다.

```java
 @Test
    public void depositIncreasesBalance() {
        int initialBalance = account.getBalance();
        account.deposit(100);
        assertThat(account.getBalance(), equalTo(100));
    }
```

- `assertThat(actual, matcher)`
    - actual : 검증 값
    - matcher : 비교 값
    - actual과 matcher은 같아야 한다.
- `equalTo()`
    - 어떤 자바 인스턴스나 기본형 값이든 넣을 수 있다.
    - equals()를 사용하기 때문에 기본형은 객체형으로 오토박싱 되기 때문이다.

### 1.3 전통적인 단언과 햄크레스트 단언 차이

- 햄크레스트 단언은 일반적인 단언보다 실패할 경우 오류 메시지에 더 많은 정보를 알 수 있다.

**AssertTrue (전동 단언)**

```java
Expected :true
Actual   :false
<Click to see difference>

org.opentest4j.AssertionFailedError: expected: <true> but was: <false>
```

**AssertThat (햄크레스트 단언)**

```java
Expected: <101>
     but: was <100>
java.lang.AssertionError: 
Expected: <101>
     but: was <100>
	at org.hamcrest.MatcherAssert.assertThat(MatcherAssert.java:20)
```

# 2. 햄크레스트 매처

---

### 2.1 중요한 매처

```java
@Test
    public void comparesArraysFailing() {
    
		    // equlTo
        assertThat(new String[]{"a", "b", "c"}, equalTo(new String[]{"a", "b"}));
        
        // is 장식자
        assertThat(new String[]{"a", "b", "c"},is(equalTo(new String[]{"a", "b"})));
        
        // 부정의 not
        assertThat(new String[]{"a", "b", "c"},not(equalTo(new String[]{"a", "b"})));
        
        // null 검사
        assertThat(acccount.getName(), is(not(nullValue())));
        assertThat(acccount.getName(), is(notNullValue())));
    }
```

- is 장식자는 가독성을 위해 사용할 뿐 별다른 역할은 없다.
- not null 값을 자주 검사하는 것은 불필요하다.

## 2.2 그 밖의 햄크레스트 매처

[CoreMatchers (Hamcrest)](https://hamcrest.org/JavaHamcrest/javadoc/1.3/org/hamcrest/CoreMatchers.html)

아래와 같은 매처와 더불어 많은 종류의 매처를 제공한다.

- 객체 타입 검사
- 두 객체 감조가 같은 인스턴스인지 검사
- 다수의 매처를 결합해 어떤 것이 성공하는지 검사
- 어떤 컬렉션이 요소를 포함하거나 조건에 부합하는지 검사
- 어떤 컬렉션이 아이템 몇 개를 모두 포함하는지 검사
- 어떤 걸렉션에 있는 모든 요소가 매처를 준수하는지 검사
- 사용자 정의 매처도 가능

# 3. 햄크레스트 매처를 사용해보기

## **3.1 부동소수점 테스트**

부동소수점은 오차율로 인해 원하는 대로 테스트하기 어렵다. 그렇기 때문에 float 혹은 double로 비교할 때는 공차 또는 허용 오차를 지정해야 한다.

**전통 단언을 사용한 비교**

- 가독성이 낮다

```java
aassertTrue(Math.abs((2.32 * 3) - 6.96) < 0.0005);
```

**`isCloseTo()`, 햄크레스트 매처를 사용한 비교**

- 훨씬 보기 쉽다.

```java
import static org.hamcrest.number.IsCloseTo.*closeTo*;

assertThat(2.32 * 3, closeTo(6.96, 0.0005))
```

## 3.2 예외 테스트

**단순한 방식 - 애너테이션 사용**

- Junit5에서 사용 불가

```java
@Test(expected = InsufficientFundsException.class)
    public void throwsWhenWithdrawingTooMuch() {
        account.withdraw(100);
    }
```

**try/catch와 fail - 옛 방식**

```java
@Test
   public void throwsWhenWithdrawingTooMuchTry(){
      try{
         account.withdraw(100);
         fail();
      }catch (InsufficientFundsException e){
         assertThat(e.getMessage(), equalTo("balance only 0"));
      }
}
```

- 예외가 발생하지 않으면 org.junit.Assert.fail()를 호출해 강제로 실패
- catch 로 넘어가면 테스트 성공

**ExcpectedException 규칙 - 단순한 방식 + 옛 방식**

- Junit5 사용 불가

```java
@Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void exceptionRule() {
        thrown.expect(InsufficientFundsException.class);
        thrown.expectMessage("balance only 0");

        account.withdraw(100);
    }
```

## assertThrows

- 위 방식은 더이상 제공되지 않는다 Junit5에서는 **assertThrows 를 사용한다.**

```java
@Test
public void assertThrowsWhenWithdrawingTooMuchTry(){
  Account account = new Account("abc");

   Assertions.assertThrows(InsufficientFundsException.class, () -> account.withdraw(100));
}
```

혹은 아래와 같이 사용 가능.

```java
@Test
    public void assertThrowsWhenWithdrawingTooMuchTry() {
        Account account = new Account("abc");

        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
            () -> account.withdraw(100));
        assertEquals("balance only 0", exception.getMessage());
    }
```

**예외 무시**

- 예외를 무시해야 하는 경우 try catch 말고 예외를 던지는 것이 좋다.