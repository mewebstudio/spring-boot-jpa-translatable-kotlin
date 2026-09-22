# Translatable for Spring Boot JPA (Kotlin)

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Maven badge](https://maven-badges.herokuapp.com/maven-central/com.mewebstudio/spring-boot-jpa-translatable-kotlin/badge.svg?style=flat)](https://central.sonatype.com/artifact/com.mewebstudio/spring-boot-jpa-translatable-kotlin)
[![javadoc](https://javadoc.io/badge2/com.mewebstudio/spring-boot-jpa-translatable-kotlin/javadoc.svg)](https://javadoc.io/doc/com.mewebstudio/spring-boot-jpa-translatable-kotlin)

This module provides an abstract and reusable foundation for supporting **translatable (multi-language) entities** using Spring Data JPA.  
It defines core interfaces, abstract repositories, and a base service class to handle translations with locale-specific logic.

---

## 📦 Package Structure

```
com.mewebstudio.springboot.jpa.translatable
├── ITranslatable.kt
├── ITranslatableRef.kt
├── ITranslation.kt
├── ITranslationRef.kt
├── JpaTranslatableRepository.kt
├── JpaTranslatableRefRepository.kt
├── JpaTranslationRepository.kt
├── JpaTranslationRefRepository.kt
├── AbstractTranslatableService.kt
├── AbstractTranslatableRefService.kt
├── AbstractTranslationService.kt
└── AbstractTranslationRefService.kt
```

---

## 🧩 Interfaces

### `ITranslatable<ID, T extends ITranslation<ID, ?>>`

Represents an entity that supports translations.

```kotlin
interface ITranslatable<ID, T : ITranslation<ID, *>> {
    val id: ID
    val translations: MutableList<T>
}
```

---

### `ITranslation<ID, T>`

Represents a translation of an entity in a specific locale.

```kotlin
interface ITranslation<ID, T> {
    val id: ID
    val owner: T
    val locale: String
}
```

---

## 🗃 Repositories

### `JpaTranslatableRepository<T : ITranslatable<ID, TR>, ID, TR : ITranslation<ID, *>> : JpaRepository<T, ID>`

Generic JPA repository for translatable entities.

```kotlin
@NoRepositoryBean
interface JpaTranslatableRepository<T : ITranslatable<ID, TR>, ID, TR : ITranslation<ID, *>> : JpaRepository<T, ID> {
    // ...
}
```

---

### `JpaTranslationRepository<T, ID, OWNER>`

Generic JPA repository for translation entities.

```kotlin
@NoRepositoryBean
interface JpaTranslationRepository<T : ITranslation<ID, OWNER>, ID, OWNER> : JpaRepository<T, ID> {
    // ...
}
```

---

## 🧠 Abstract Service

### `AbstractTranslatableService<T, ID, TR>`

Provides a base service class for business logic operations.

```kotlin
abstract class AbstractTranslatableService<T : ITranslatable<ID, TR>, ID, TR : ITranslation<ID, *>>(
    open val repository: JpaTranslatableRepository<T, ID, TR>
) {
    // ...
}
```

### `AbstractTranslationService<T : ITranslation<ID, OWNER>, ID, OWNER>`

Provides a base service class for business logic operations.

```kotlin
abstract class AbstractTranslationService<T : ITranslation<ID, OWNER>, ID, OWNER>(
    open val repository: JpaTranslationRepository<T, ID, OWNER>
) {
    // ...
}
```

---

## 🔗 Ref variant

Every interface/repository/abstract-service above keys a translation row by `locale: String` — the
FK value *is* the human-readable locale code (`"en"`, `"tr-TR"`, ...) itself. That's fine as long
as the code never changes after creation. If your `Locale`-like entity's code/name IS editable
after creation, keying translation rows directly by that mutable string means every rename has to
cascade across every translation table referencing it.

`ITranslationRef`/`ITranslatableRef`/`JpaTranslationRefRepository`/`JpaTranslatableRefRepository`/
`AbstractTranslationRefService`/`AbstractTranslatableRefService` are a **fully independent,
additive** parallel API — identical shape, but the locale is referenced by id (`localeId:
LOCALE_ID`, typically your locale entity's own, immutable primary key) instead of stored by value
(`locale: String`) — hence "Ref". Renaming the locale entity's business code then touches nothing
downstream, since no translation row's FK depends on that value. Pick ONE family per translation
entity — the two are not meant to be mixed on the same entity. Existing code using the
`locale: String` family above is completely unaffected by this addition.

```kotlin
interface ITranslationRef<ID, T, LOCALE_ID> {
    val id: ID
    val owner: T
    val localeId: LOCALE_ID
}
```

```kotlin
@Entity
class CategoryTranslation(
    @Id
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    override val owner: Category,

    // The real FK — points at Locale.id, never changes even if Locale.code does.
    @Column(name = "locale_id", nullable = false)
    override val localeId: Long,

    // Optional: a denormalized, non-FK-constrained display copy of the locale's business code,
    // refreshed explicitly whenever that code changes — read Locale.code via a join instead if
    // you don't need it queryable/sortable on the translation row itself.
    @Column(name = "locale_code", nullable = false)
    var localeCode: String,

    @Column(name = "name", nullable = false, length = 255)
    var name: String,
) : ITranslationRef<Long, Category, Long>
```

---

## 📥 Installation

#### for maven users
Add the following dependency to your `pom.xml` file:
```xml
<dependency>
  <groupId>com.mewebstudio</groupId>
  <artifactId>spring-boot-jpa-translatable-kotlin</artifactId>
  <version>0.1.2</version>
</dependency>
```
#### for gradle users
Add the following dependency to your `build.gradle` file:
```groovy
implementation 'com.mewebstudio:spring-boot-jpa-translatable-kotlin:0.1.2'
```

---

## 📌 Usage

You can extend these interfaces and abstract class to implement your own translatable entities and services:

### Translatable Entity Example
```kotlin
@Entity
@Table(name = "categories")
class Category(
    @Id
    val id: Long,

    @OneToMany(mappedBy = "owner", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("locale ASC")
    override var translations: MutableList<CategoryTranslation> = mutableListOf(),
) : ITranslatable<String, CategoryTranslation> {
    override fun toString(): String = "${this::class.simpleName}(id = $id)"
}
```

### Translation Entity Example
```kotlin
@Entity
class CategoryTranslation(
    @Id
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    override val owner: Category,

    @Column(name = "locale", nullable = false)
    override val locale: String,

    @Column(name = "name", nullable = false, length = 255)
    var name: String,

    @Column(name = "description", columnDefinition = "text")
    var description: String? = null,
) : ITranslation<String, Category> {
    override fun toString(): String =
        "${this::class.simpleName}(id = $id, name = $name, locale = $locale, owner = $owner)"
}
```

### Translatable Repository Example
```kotlin
interface CategoryRepository : JpaTranslatableRepository<Category, String, CategoryTranslation>
```

### Translation Repository Example
```kotlin
interface CategoryTranslationRepository : JpaTranslationRepository<CategoryTranslation, String, Category>
```

### Translatable Service Example
```kotlin
@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val categoryTranslationRepository: CategoryTranslationRepository
) : AbstractTranslatableService<Category, String, CategoryTranslation>(categoryRepository) {
    private val log: Logger by logger()

    init {
        log.debug("CategoryService initialized with repository: {}", repository)
        requireNotNull(repository) { "CategoryRepository cannot be null" }
    }

    // Custom business logic methods can be added here...

}
```

---

## 🛠 Requirements

- Java 17+
- Kotlin 1.9.23+
- Spring Boot 3.x+
- Spring Data JPA

---

## 🔁 Other Implementations

[Spring Boot JPA Translatable (Java Maven Package)](https://github.com/mewebstudio/spring-boot-jpa-translatable)

## 💡 Example Implementations

[Spring Boot JPA Translatable - Kotlin Implementation](https://github.com/mewebstudio/spring-boot-jpa-translatable-kotlin-impl)

[Spring Boot JPA Translatable - Java Implementation](https://github.com/mewebstudio/spring-boot-jpa-translatable-java-impl)

## 📃 License

MIT © [mewebstudio](https://github.com/mewebstudio)