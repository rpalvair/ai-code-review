# ai-code-review

AI-powered code review assistant. Soumet du code → analyse via Claude API → feedback structuré (bugs, sécurité, refactoring, score).

## Stack

- **Backend** : Java 25, Spring Boot 4.x, REST JSON
- **IA** : Anthropic Claude API
- **DB** : PostgreSQL + Liquibase
- **Frontend** : Angular 

---

## Lancer le backend en local

### Prérequis

- Java 25
- Maven 3.6.3+

### Configuration de la clé API

La clé Anthropic est lue depuis la variable d'environnement `ANTHROPIC_API_KEY`.  

**Linux / macOS / Git Bash :**
```bash
export ANTHROPIC_API_KEY=sk-ant-xxxx
mvn spring-boot:run
```

**Ou en inline :**
```bash
ANTHROPIC_API_KEY=sk-ant-xxxx mvn spring-boot:run
```

**Windows CMD :**
```cmd
set ANTHROPIC_API_KEY=sk-ant-xxxx && mvn spring-boot:run
```

---

## Tester l'API

### POST /api/review

```bash
curl -X POST http://localhost:8080/api/review \
  -H "Content-Type: application/json" \
  -d '{"code": "public int divide(int a, int b) { return a / b; }", "language": "java"}'
```

**Réponse attendue :**
```json
{
  "id": "uuid",
  "language": "java",
  "createdAt": "2026-...",
  "feedback": {
    "bugs": ["..."],
    "security": ["..."],
    "refactoring": ["..."],
    "quality": "...",
    "score": 7
  }
}
```
