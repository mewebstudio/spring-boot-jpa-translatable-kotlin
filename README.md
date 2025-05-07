# Translatable for Spring Boot JPA

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
├── ITranslation.kt
├── JpaTranslatableRepository.kt
├── JpaTranslationRepository.kt
└── AbstractTranslatableService.kt
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

## 📥 Installation

#### for maven users
Add the following dependency to your `pom.xml` file:
```xml
<dependency>
  <groupId>com.mewebstudio</groupId>
  <artifactId>spring-boot-jpa-translatable-kotlin</artifactId>
  <version>0.1.0</version>
</dependency>
```
#### for gradle users
Add the following dependency to your `build.gradle` file:
```groovy
implementation 'com.mewebstudio:spring-boot-jpa-translatable-kotlin:0.1.0'
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
- Spring Boot 3.x
- Spring Data JPA

---

## 🔁 Other Implementations

[Spring Boot JPA Translatable (Java Maven Package)](https://github.com/mewebstudio/spring-boot-jpa-translatable)

## 💡 Example Implementations

[Spring Boot JPA Translatable - Kotlin Implementation](https://github.com/mewebstudio/spring-boot-jpa-translatable-kotlin-impl)

[Spring Boot JPA Translatable - Java Implementation](https://github.com/mewebstudio/spring-boot-jpa-translatable-java-impl)

## 📃 License

MIT © [mewebstudio](https://github.com/mewebstudio)