
# 3장. Junit 단언 깊게 파기
>📝**3장에서 학습하는 것**
>- Junit이 제공하는 assert
>- 햄크레스트가 제공하는 assert
   >    - 햄크레스트(Hamcrest)란, Matcher 라이브러리로 matchers의 단어에서 철자 순서를 바꾼 말이다.
>- 다양한 햄크레스트 매처
>- 예외를 테스트 하는 방법
   >    - @Test와 expected 옵션
>    - try/catch + fail()
>    - @Rule과 ExpectedException
>    - junit5에서의 예외 테스트
## 3.1 Junit Assert
***
assert 메서드는 테스트 전반에서 사용되기 때문에 **정적 임포트**를 사용한다.

<aside>

>**import static org.junit.Assert.*;**

</aside>

### (1) assertTrue

가장 기본적인 assert메서드는 **`assertTrue(someBooleanExpression)`** 이다.

**assertTrue**를 통해 인자로 보낸 **조건식이 참**인지 확인할 수 있다.

### (2) assertThat

**assertThat**은 **예상 값과 실제 값을 명확하게 비교**할 때 사용한다.

**assertThat(account.getBalance(), equalTo(100));** //assertThat(예상 값, 실제값과 예상값 비교)

## 3.2 다양한 햄크레스트 매처
***
다양한 햄크레스트 매처를 알아보기에 앞서 핵심 햄크레스트 매처를 사용하기 위해 아래의 정적 임포트를 추가해야 한다.


>**import static org.hamcrest.CoreMathchers.*;**
>
>**import java.io.*;**
>
>**import java.util.*;**



- equalTo() : 자바 배열 혹은 컬렉션 객체를 비교할 때 사용하는 메서드
- is() : 인자로 받은 매처를 반환함. 가독성을 높이기 위한 메서드
- not() : 부정하는 단언을 만들 때 사용하는 메서드
- nullValue():  null 값인지 확인하는 메서드
- notNullValue() : null 값이 아닌지 확인하는 메서드

## 3.3 예외 테스트
***
### 3.3.1 단순한 방식: 애너테이션 사용

Junit의 **`@Test`** 어노테이션을 통해 예외를 테스트 할 수 있다.

```java
@Test(expected = InsufficientFundsException.class)
public void throwsWhenWithdrawingTooMuch(){
	account.withdraw(100);
}
```

해당 테스트 코드를 실행하여 **`InsufficientFundsException`** 이 발생하면 테스트가 통과한다.

그러나, **junit5에서는 @Test(expect=)를 지원하지 않는다**. 이를 대체할 방법을 찾아보니 **`assertThrow`** 를 사용하여 예외를 테스트 한다고 한다.

### 3.3.2 옛 방식: try/catch와 fail

```java
try{
    account.withdraw(100);
    fail();
}catch(InsufficientFundsException excepted){
    
}
```

account.withdraw(100);을 실행하면서 예상하는 Exception이 발생하지 않는다면, junit이 제공하는 fail() 메서드를 통해 테스트가 실패할 수 있도록 한다.

예상했던 Exception이 발생한다면 테스트가 통과한다.

이 방식은 **예외가 발생한 후**에 어떤 **상태를 검사**할 때 유용하다.

### 3.3.3 새로운 방식: ExpectedException 규칙

테스트 클래스에 ExpectedException인스턴스를 public으로 선언하고, @Rule 어노테이션을 작성한다.

```java
@Rule
public ExpectedException thrown = ExpectedException.none();

@Test
public void exceptionRule(){
	thrown.expect(InsufficientFundsException.class);
	thrown.expectMessage("balance only 0");
	
	account.withdraw(100);
}
```

### 3.3.4 junit5에서의 예외 테스트

앞서 학습했던 @Test(expected=), @Rule은 junit5에서는 작동하지 않는다. 대신, **`assertThrows`** 로 예외 테스트를 진행할 수 있다. 위에서 junit4로 작성했던 테스트 코드를 **`assertThrows`** 로 변경하면 다음과 같이 작성할 수 있을 것이다.

```java
@Test
public void throwsWhenWithdrawingTooMuch(){
	assertThrows(InsufficientFundsException.class, () -> {
			account.withdraw(100);
	});
}
```

assertThrows를 통해서 예외 발생 여부를 판단할 수 있다. assertThrows가 반환하는 Exception을 받아 **`assertEquals()`** 와 함께 사용하면, 해당 예외 메세지를 확인할 수 있다. try/catch와 assertEquals를 하는 방법도 있다.

키워드 `assertThrows`, `assertEquals`, `try/catch`
