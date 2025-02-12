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
    @DisplayName(value = "Criteria 인스턴스가 Criterion 객체를 포함하지 않을 때")
    public void criteriaNotIncludeObject() {

        // Arrange
        Answer profileAnswer = new Answer(question, Bool.TRUE);
        profile.add(profileAnswer);

        // Act
        boolean matches = profile.matches(criteria);

        // Assert
        assertFalse(matches);
    }

    @Test
    @DisplayName(value = "Criteria 인스턴스가 Criterion 객체를 포함할 때")
    public void criteriaIncludeObject() {

        // Arrange
        Answer profileAnswer = new Answer(question, Bool.TRUE);
        profile.add(profileAnswer);

        Answer criteriaAnswer = new Answer(question, Bool.TRUE);
        Criterion criterion = new Criterion(criteriaAnswer, Weight.MustMatch);
        criteria.add(criterion);

        // Act
        boolean matches = profile.matches(criteria);

        // Assert
        assertTrue(matches);
    }
}