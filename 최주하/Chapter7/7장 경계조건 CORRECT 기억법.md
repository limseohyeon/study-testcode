# 7장. 경계조건: CORRECT 기억법

>📝 **7장 학습 내용**
>
>- 단위 테스트 작성할 때, 고려해야 하는 경계 조건 CORRECT
>- **C**onformance(준수): 값이 기대한 양식을 준수하고 있는지
>- **O**rdering(순서): 값의 집합이 적절하게 정렬되거나 정렬되지 않았는지
>- **R**ange(범위): 이성적인 최솟값과 최댓값 안에 있는지
>- **R**eference(참조): 코드 자체에서 통제할 수 없는 어떤 외부 참조를 포함하고 있는지
>- **E**xistence(존재): 값이 존재하는지
>- **C**ardinality(기수): 정확히 충분한 값들이 있는지
>- **T**ime(시간): 모든 것이 순서대로 일어나는지. 정확한 시간에 일어나는지.


# CORRECT: Conformance

---

양식이 있는 데이터들이 있다. 예를 들면, 전화번호, 이메일, 파일 이름 등

이 경우, 데이터 흐름을 이해하고 불필요한 검사를 최소화 하도록 한다.

→ 필드가 처음 입력될 때 검증하면, 인자로 넘길 때 마다 검사하지 않아도 된다.

# CORRECT: Ordering

---

값들이 특정 순서대로 처리되는지 테스트 한다. 예를 들어, 배열이나 리스트가 올바르게 정렬되는지 등

# CORRECT: Range

---

- 기본형 데이터를 과도하게 사용함으로써 생기는 **코드 악취**
    - 기본형(primitive type)데이터 타입을 과도하게 사용하는 것이 코드 품질에 문제를 일으킬 수 있고, 이를 **Primitive obsession**이라고 한다.

- 해당 파트에서 제시하는 예제는 방위를 기본형인 int 값을 사용해서 저장하는 것보다 Bearing이라는 클래스를 사용하여 값에 대한 범위를 제한 할 수 있도록 하는 것을 권장한다.

    ```java
    public class Bearing{
    	public static final int MAX = 359;
    	private int value;
    	
    	public Bearing(int value){
    		if(value < 0 || value > MAX) throw new BearingOutOfRangeException();
    		this.value = value;
    	}
    	
    	public int value(){
    		return value;
    	}
    	
    	public int angleBetween(Bearing bearing){
    		return value - bearing.value;
    	}
    }
    ```


# CORRECT: Reference

---

메서드를 테스트 할 때 고려해야 하는 상황은 다음과 같다.

- 범위를 넘어서는 것을 참조하고 있지 않은지
- 외부 의존성은 무엇인지(DB, 네트워크, 파일 등)
- 특정 상태에 있는 객체를 의존하고 있는지
- 반드시 존재해야 하는 그 외의 다른 조건

그리고, 책에서 제시하는 예시들은 다음과 같다.

- 회원의 계정 히스토리를 표시하는 웹 앱에서 고객이 먼저 로그인해야 한다.
- 스택의 pop() 메서드를 호출할 때, 스택이 비어있어서는 안된다.
- 차량 변속기를 D→P로 변경할 때는, 먼저 차를 멈추어야 한다.

해당 예시들 중, 차량 변속기에 관한 예시를 코드로 살펴본다.

차량이 이동 중일 때와 멈춰있을 때 변속기 동작이 어떻게 달라지는지 테스트 하고자 한다.

테스트 하고자 하는 대상은 **`Transmission`** 클래스이다. 세 가지의 시나리오를 가지고 테스트 코드를 작성하고자 한다.

각각에서 요구하는 사전 조건과 사후 조건이 있다.

- **`사전 조건` :** 테스트 전에 특정 상태를 만들기 위해 필요한 조건
- **`사후 조건` :** 테스트 후 시스템이 충족해야 하는 조건. 테스트의 단언으로 명시함. 또한, 메서드를 호출하면서 발생하는 상태 변화를 검사해야 할 필요가 있음.

① 가속 이후에 변속기를 주행으로 유지하는가

- 사전 조건
    - 차량의 변속기 상태가 D여야 함.
    - 차량의 속도가 0 이상이어야 함.
- 사후 조건
    - 변속기 상태가 D 상태를 유지해야 함.

```java
@Test
@DisplayName("가속 후 변속기의 상태는 D를 유지해야 한다.")
public void remainsInDriveAfterAcceleration(){
	
	transmission.shift(Gear.DRIVE);
	car.accelerateTo(35);
	assertThat(transmission.getGear(), equalTo(Gear.DRIVE));
}
```

② 주행 중에 변속기를 P로 바꾸는 요청을 적절하게 처리하는가 (P로 바꿀 수 없도록 하는 것을 적절하게 처리하는 것으로 봄)

- 사전 조건
    - 차량의 변속기 상태가 D
    - 차량의 속도가 0 이상
- 사후 조건
    - 변속기 상태가 P로 변하면 안된다.

```java
@Test
@DisplayName("주행 중 변속기를 P로 바꾸는 요청을 무시한다.")
public void ignoreShiftToParkWhileInDrive(){
	transmission.shitf(Gear.DRIVE);
	car.accelerateTo(30);
	
	transmission.shift(Gear.PARK);
	assertThat(transmission.getGear(), equalTo(Gear.DRIVE));
}
```

③ 차량이 움직이지 않는 경우, 변속기를 P로 변경할 수 있는가

- 사전 조건
    - 변속기 상태가 D
    - 차량 속도가 0
- 사후 조건
    - 변속기 상태가 P로 변한다.

이 테스트를 진행하며, car.breakToStop();을 호출할 때, 자동차 속도가 0으로 설정되는 상태 변경이 발생한다.

```java
@Test
@DisplayName("차량이 움직이지 않는 경우, 변속기를 P로 변경할 수 있다.")
public void allowsShiftToParkWhenNnotMoving(){
	transmission.shitf(Gear.DRIVE);
	car.accelerateTo(30);
	car.breakToStop();
	
	
	transmission.shift(Gear.PARK);
	assertThat(transmission.getGear(), equalTo(Gear.PARK));
}
```

객체의 특정 상태에 의존하는 부분에 대해 테스트 코드를 작성해보았다.

# CORRECT: Existence

---

데이터가 없는 경우에 대한 테스트 코드를 작성해야 한다.

null, 0, 빈 문자열 처럼 주어진 값이 없는 경우, 어떻게 작동하는지 확인해야 한다.

# CORRECT: Cardinality

---

정확히 충분한 값들이 있는지를 체크해야 한다.

- 울타리 기둥 오류(fencepost errors): 반복문에서 경계 조건을 잘못 설정하여 한 개 부족하거나 한 개 초과하는 오류를 의미한다.

- 앞서 정리했던 Existence는 cardinality의 특수한 경우이다.
- 기수에 대한 테스트 작업 목록을 도출하는 데 유용한 법칙에는 **0-1-n 법칙**이 있다.
- 즉, 하나도 없는 경우(0), 한 개만 있는 경우(1), 여러 개가 있는 경우(n)를 경계 조건으로 테스트 항목을 도출한다면 코드 작성이 수월해진다.

# CORRECT: TIME

---

시간에 관해 주의 깊게 볼 부분은 다음과 같다.

- 상대적 시간 (시간 순서) : 특정 동작이 올바른 순서대로 수행되는가?
    - 로그인과 로그아웃, open()과 read()
    - 타임아웃
- 절대적 시간 (측정된 시간 & 벽시계 시간)
- 동시성 문제들: 멀티 스레드 환경에서 올바르게 동작하는가?