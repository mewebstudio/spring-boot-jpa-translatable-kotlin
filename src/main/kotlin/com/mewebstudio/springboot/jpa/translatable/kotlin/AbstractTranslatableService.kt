package com.mewebstudio.springboot.jpa.translatable.kotlin

import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Abstract service class for managing translatable entities.
 *
 * @param T The type of the translatable entity.
 * @param ID The type of the ID of the translatable entity.
 * @property repository The repository for accessing translatable entities.
 */
abstract class AbstractTranslatableService<T : ITranslatable<ID, TR>, ID, TR : ITranslation<ID, *>>(
    open val repository: JpaTranslatableRepository<T, ID, TR>
) {
    /**
     * Checks if a translatable entity exists by its ID and locale.
     *
     * @param id The ID of the entity.
     * @param locale The locale to check for.
     * @return True if the entity exists with the given locale, false otherwise.
     */
    open fun existsByIdAndLocale(id: ID, locale: String): Boolean = repository.existsByIdAndLocale(id, locale)

    /**
     * Finds a translatable entity by its ID and locale.
     *
     * @param id The ID of the entity.
     * @param locale The locale of the translation.
     * @return T? The translatable entity, or null if not found.
     */
    open fun findByIdAndLocale(id: ID, locale: String): T? = repository.findByIdAndLocale(id, locale)

    /**
     * Finds all translatable entities that have a translation with the given locale.
     *
     * @param locale The locale to filter by.
     * @return List of translatable entities.
     */
    open fun findAllByLocale(locale: String): List<T> = repository.findAllByLocale(locale)

    /**
     * Finds all translatable entities that have a translation with the given locale, with pagination.
     *
     * @param locale The locale to filter by.
     * @param pageable Pagination information.
     * @return Page of translatable entities.
     */
    open fun findAllByLocale(locale: String, pageable: Pageable): Page<T> = repository.findAllByLocale(locale, pageable)

    /**
     * Finds all translations for a specific translatable entity by its ID.
     *
     * @param id The ID of the entity.
     * @return List of translations for the entity.
     */
    open fun findTranslationsById(id: ID): List<TR> = repository.findTranslationsById(id)

    /**
     * Finds all translations for a specific translatable entity by its ID, with pagination.
     *
     * @param id The ID of the entity.
     * @param pageable Pagination information.
     * @return Page of translations for the entity.
     */
    open fun findTranslationsById(id: ID, pageable: Pageable): Page<TR> =
        repository.findTranslationsById(id, pageable)

    /**
     * Saves a translatable entity.
     *
     * @param entity The entity to save.
     * @return The saved entity.
     */
    @Transactional
    open fun save(entity: T): T = repository.save(entity)

    /**
     * Deletes all translatable entities that have a translation with the given locale.
     *
     * @param locale The locale of the translations to delete.
     * @return The number of deleted entities.
     */
    @Transactional
    open fun deleteByLocale(locale: String): Int = repository.deleteByLocale(locale)

    /**
     * Deletes a translatable entity if it has a translation with the given ID and locale.
     *
     * @param id The ID of the entity.
     * @param locale The locale of the translation.
     * @return The number of deleted entities (usually 0 or 1).
     */
    @Transactional
    open fun deleteByIdAndLocale(id: ID, locale: String): Int = repository.deleteByIdAndLocale(id, locale)
}
