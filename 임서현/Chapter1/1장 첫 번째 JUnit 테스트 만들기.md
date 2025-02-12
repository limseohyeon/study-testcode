# 테스트 준비, 실행, 단언

## 준비(arrange)

테스트 상태를 설정한다.

## 실행(act)

검증하려는 코드를 실행한다.

## 단언(assert)

기대하는 결과에 대해 작성한다.

# 실습 코드

Scoreable.java

```java
@FunctionalInterface
public interface Scoreable {
    int getScore();
}
```

ScoreColleciton.java

```java
public class ScoreCollection {

    private List<Scoreable> score = new ArrayList<>();

    public void add(Scoreable scoreable) {
        score.add(scoreable);
    }

    public int arithmeticMean() {
        int total = score.stream().mapToInt(Scoreable::getScore).sum();
        return total / score.size();
    }
}
```

ScoreCollectionTest.java

```java
public class ScoreCollectionTest {

    @Test
    public void test(){
        // 준비
        ScoreCollection collection = new ScoreCollection();
        collection.add(()->5);
        collection.add(()->7);

        //실행
        int actualResult = collection.arithmeticMean();

        //단언
        assertThat(actualResult).isEqualTo(6);
    }
}
```

- `@Test` : Junit이 메서드를 테스트로 실행하게 한다.
- 테스트 클래스 작명 법 : 일반적으로 `테스트 대상 클래스 + Test` 와 같이 작성한다.
- `assertThat(실제 결과).isEqulTo(예상값)`