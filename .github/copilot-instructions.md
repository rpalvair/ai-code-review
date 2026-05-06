# Instructions générales de développement

## Posture de développeur

- Adopter la posture d'un développeur **full stack expérimenté, adepte du Software Craftsmanship**.
- Respecter les principes **SOLID** : responsabilité unique, ouverture/fermeture, substitution de Liskov, ségrégation des interfaces, inversion des dépendances.
- **Pas d'over-engineering** : toujours rechercher la solution la plus simple, maintenable et testable.
- Utiliser l'**architecture hexagonale** (ports & adapters) pour le développement back-end.

## Autonomie et livraison

- Développer les fonctionnalités en **totale autonomie** : ne s'arrêter que lorsque le code compile et que tous les tests passent.
- Faire des **commits réguliers** entre chaque étape significative (une étape = un commit).
- Ne jamais laisser le projet dans un état non compilable ou avec des tests en échec.

---

# Instructions pour l'écriture des tests unitaires

## Stack de test

- **JUnit 5** (`@Test`, `@ExtendWith`)
- **Mockito** via `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`, `@MockitoBean`
- **AssertJ** (`assertThat`, `assertThatThrownBy`)
- **Spring Boot Test** (`@WebMvcTest`, `MockMvc`) pour la couche web

## Conventions de nommage

Les méthodes de test suivent la convention `should_<résultat_attendu>_when_<contexte>` :

```java
void should_return_review_with_analysis_when_code_is_submitted()
void should_throw_when_no_text_block_present()
void should_return_400_when_code_is_blank()
```

## Structure d'un test

Chaque test suit **Arrange / Act / Assert** sans commentaires explicites — le code doit être suffisamment lisible pour se passer d'annotations.

```java
@Test
void should_return_review_with_analysis_when_code_is_submitted() {
    // Arrange
    CodeAnalysis analysis = new CodeAnalysis(List.of(), List.of(), List.of(), "Good", 90);
    when(aiReviewPort.analyzeCode("code", "Java")).thenReturn(analysis);

    // Act
    Review review = reviewCodeService.execute("code", "Java");

    // Assert
    assertThat(review.language()).isEqualTo("Java");
    assertThat(review.analysis()).isEqualTo(analysis);
}
```

## Règles de lisibilité et maintenabilité

1. **Un test = un comportement** — ne pas vérifier plusieurs comportements distincts dans le même test.
2. **Pas de logique dans les tests** — pas de `if`, `for`, ni de variables intermédiaires inutiles.
3. **Données de test inline** — construire les objets directement dans le test, sans factories partagées sauf si la duplication est significative.
4. **Mocks limités au strict nécessaire** — ne mocker que les dépendances externes (ports, HTTP), pas les records ni les mappers purs.
5. **Pas de `@BeforeEach` sauf pour MockMvc** — préférer l'initialisation directe dans chaque test pour une meilleure lisibilité.

## Que tester par couche

### Couche application (`application/`)
- Utiliser `@ExtendWith(MockitoExtension.class)` + `@Mock` sur les ports.
- Vérifier que le service orchestre correctement les appels aux ports et construit le bon résultat.

```java
@ExtendWith(MockitoExtension.class)
class ReviewCodeServiceTest {
    @Mock AiReviewPort aiReviewPort;
    @InjectMocks ReviewCodeService reviewCodeService;
    // ...
}
```

### Couche domaine / DTOs
- Tests purs (pas de Spring, pas de Mockito).
- Instancier les classes directement et vérifier les comportements (ex. `AnthropicResponse#firstText()`).

```java
class AnthropicResponseTest {
    @Test
    void should_return_first_text_block_content() {
        var response = new AnthropicResponse(List.of(new ContentBlock("text", "hello")));
        assertThat(response.firstText()).isEqualTo("hello");
    }
}
```

### Mappers (`CodeAnalysisMapper`, `ReviewMapper`)
- Tests purs : instancier le mapper avec `new`, vérifier que tous les champs sont bien mappés.
- Placer le test dans le **même package** que le mapper (ils sont package-private).

```java
class ReviewMapperTest {
    private final ReviewMapper mapper = new ReviewMapper();
    // ...
}
```

### Couche web (`infrastructure/web/`)
- Utiliser `@WebMvcTest(ReviewController.class)`.
- Mocker `ReviewCodeUseCase` et `ReviewMapper` avec `@MockitoBean`.
- Tester : réponse 200 sur une requête valide, 400 sur une requête invalide.

```java
@WebMvcTest(ReviewController.class)
class ReviewControllerTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean ReviewCodeUseCase reviewCodeUseCase;
    @MockitoBean ReviewMapper reviewMapper;
    // ...
}
```

## Ce qu'on ne teste PAS en tests unitaires

- `AnthropicAiAdapter` — dépend d'un vrai client HTTP ; à tester avec un test d'intégration (WireMock).
- `AnthropicRestClientBuilder` — configuration Spring pure, testable via un test d'intégration.
- `IaCodeReviewApplication` — point d'entrée Spring Boot, couvert par les tests d'intégration.
