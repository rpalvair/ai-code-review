# Intégration SonarCloud

## 1. Création du compte et du projet

1. Aller sur [sonarcloud.io](https://sonarcloud.io)
2. Se connecter avec GitHub
3. Importer le repo `rpalvair/ai-code-review`
4. Récupérer le **`SONAR_TOKEN`** généré
5. L'ajouter dans **GitHub Secrets** : `Settings → Secrets → Actions → New repository secret`

---

## 2. Configuration du projet

Créer le fichier `sonar-project.properties` à la racine du projet :

```properties
sonar.projectKey=rpalvair_ai-code-review
sonar.organization=rpalvair
sonar.sources=src/main/java
sonar.tests=src/test/java
sonar.java.coveragePlugin=jacoco
sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
```

---

## 3. Configuration JaCoCo (rapport XML)

Pour que SonarCloud affiche la couverture de code, JaCoCo doit générer un rapport au format **XML** en plus du HTML. Ajouter dans le `pom.xml` :

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>verify</phase>
            <goals><goal>report</goal></goals>
            <configuration>
                <formats>
                    <format>HTML</format>
                    <format>XML</format>
                </formats>
                <excludes>
                    <exclude>**/IaCodeReviewApplication.class</exclude>
                    <exclude>**/config/**</exclude>
                    <exclude>**/AnthropicRestClientBuilder.class</exclude>
                </excludes>
            </configuration>
        </execution>
    </executions>
</plugin>
```

---

## 4. Workflow GitHub Actions

Créer le fichier `.github/workflows/sonar.yml` :

```yaml
name: Sonar Analysis

on:
  push:
    branches: [ main, feat/** ]
  pull_request:

jobs:
  sonar:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0  # requis par Sonar pour le blame

      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Cache Maven
        uses: actions/cache@v4
        with:
          path: ~/.m2/repository
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
          restore-keys: ${{ runner.os }}-maven-

      - name: Build, test & analyze
        run: |
          mvn verify sonar:sonar \
            -Dsonar.projectKey=rpalvair_ai-code-review \
            -Dsonar.organization=rpalvair \
            -Dsonar.host.url=https://sonarcloud.io
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
```

---

## 5. Interpréter le dashboard SonarCloud

### Les 4 axes d'analyse

| Axe | Ce qu'il mesure |
|---|---|
| 🐛 **Bugs** | Erreurs potentielles à l'exécution |
| 🔒 **Security** | Vulnérabilités et hotspots de sécurité |
| 🧹 **Code Smells** | Dette technique, mauvaises pratiques |
| 📊 **Coverage** | Couverture de code (via JaCoCo) |

### Les niveaux de sévérité

| Niveau | Signification |
|---|---|
| 🔴 **Blocker** | À corriger immédiatement — risque critique |
| 🟠 **Critical** | À corriger avant le prochain merge |
| 🟡 **Major** | Dette technique significative |
| 🔵 **Minor / Info** | Améliorations cosmétiques, non bloquantes |

### Le Quality Gate

C'est le verdict final **✅ Passed / ❌ Failed** affiché directement sur la PR GitHub.

Par défaut il vérifie sur le **nouveau code uniquement** :
- Couverture ≥ 80%
- 0 bug ou vulnérabilité
- Code smells sous un certain ratio de dette

> ⚠️ Un Quality Gate ❌ bloque le merge si la branche protection est configurée en conséquence.

---

## 6. Bonnes pratiques

- **Ne pas viser 100% de couverture** — se concentrer sur la qualité des assertions, pas le chiffre.
- **Traiter les hotspots de sécurité** en priorité, même s'ils ne sont pas des vulnérabilités avérées.
- **Exclure les classes sans logique métier** de l'analyse de couverture (point d'entrée Spring Boot, configuration pure).
