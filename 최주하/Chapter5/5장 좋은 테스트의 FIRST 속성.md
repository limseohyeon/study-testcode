# 5장. 좋은 테스트의 FIRST 속성

>📝 **5장 학습 내용**
>
>- FIRST 원리를 따라 좋은 테스트를 만들어라!
>- Fast(빠른)
>- Isolated(고립된)
>- Repeatable(반복가능한)
>- Self-validating(스스로 검증가능한)
>- Timely(적시의)


## Fast

---

- **⭐빠른 테스트: 코드만 실행⭐**
- 느린 테스트: 외부 자원을 다루는 코드 호출

## Isolated

---

테스트가 서로 의존하지 않고 **독립적**으로 실행되어야 한다.

테스트 코드가 다른 코드와 상호 작용하는 경우, 테스트가 실패했을 때 원인을 파악하기 힘들다.

SRP 원칙을 지키자!

하나의 테스트 메서드에서 assert 코드를 추가하기 전에, ✅**새로운 테스트 메서드로 분리할 수 있는지**✅를 체크해야 한다.

## Repeatable

---

**테스트를 실행할 때마다 결과가 똑같이** 나와야 한다! 그러기 위해서는 **직접 통제할 수 없는 외부 환경의 자원과 격리**시켜야 한다.

책에서는 Clock 객체를 사용하는 예시를 든다.

```java
@Test
public void questionAnserDateAdded(){
	Instant now = new Date().toInstant();
	controller.setClock(Clock.fixed(now, ZoneId.of("America/Denver")));
	int id = controller.addBooleanQuestion("text");
	
	Question question = controller.find(id);
	
	assertThat(question.getCreateTimestamp(), equalTo(now));
}
```

LooclDateTime.now()의 시간으로 사용했다면, 실행하는 순간의 시간이 반영되기 때문에 테스트 결과가 일정하게 나오지 않을 확률이 높다.

그래서, Clock클래스의 fixed 메서드를 사용하여 고정된 값을 반환하도록 설정하여 같은 결과를 보장하는 테스트를 작성했다.

## Self-validating

---

Self-validating은 **테스트가 자동으로 실행되고, 결과가 명확하게 판별(성공/실패)되어야 함을 의미한다.**

Self-validating이 되지 않는 테스트의 예시로는 직접 일일이 **로그를 출력**하며 확인하는 것을 말한다. 그러나, 이 방법은 실수할 가능성이 크다.

## Timely

---

**테스트 코드가 적절한 시점에 작성되어야 함**을 의미한다.

테스트 코드를 너무 늦게 작성하려고 하면, 어떤 부분을 테스트 해야 하는지 놓칠 가능성이 있고, 기능이 너무 복잡해서 테스트가 어려워질 수 있다.