package com.mewebstudio.springboot.jpa.translatable.kotlin

import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * [AbstractTranslatableService]'s counterpart for [ITranslatableRef] / [JpaTranslatableRefRepository].
 *
 * @param T The type of the translatable entity.
 * @param ID The type of the ID of the translatable entity.
 * @param TR The type of the translation entity.
 * @param LOCALE_ID The type of the referenced locale entity's own identifier.
 * @property repository The repository for accessing translatable entities.
 */
abstract class AbstractTranslatableRefService<
    T : ITranslatableRef<ID, TR>,
    ID : Any,
    TR : ITranslationRef<ID, *, LOCALE_ID>,
    LOCALE_ID,
    >(
    open val repository: JpaTranslatableRefRepository<T, ID, TR, LOCALE_ID>
) {
    /**
     * Checks if a translatable entity exists by its ID and locale id.
     *
     * @param id The ID of the entity.
     * @param localeId The locale id to check for.
     * @return True if the entity exists with the given locale id, false otherwise.
     */
    open fun existsByIdAndLocaleId(id: ID, localeId: LOCALE_ID): Boolean =
        repository.existsByIdAndLocaleId(id, localeId)

    /**
     * Finds a translatable entity by its ID and locale id.
     *
     * @param id The ID of the entity.
     * @param localeId The locale id of the translation.
     * @return T? The translatable entity, or null if not found.
     */
    open fun findByIdAndLocaleId(id: ID, localeId: LOCALE_ID): T? = repository.findByIdAndLocaleId(id, localeId)

    /**
     * Finds all translatable entities that have a translation with the given locale id.
     *
     * @param localeId The locale id to filter by.
     * @return List of translatable entities.
     */
    open fun findAllByLocaleId(localeId: LOCALE_ID): List<T> = repository.findAllByLocaleId(localeId)

    /**
     * Finds all translatable entities that have a translation with the given locale id, with pagination.
     *
     * @param localeId The locale id to filter by.
     * @param pageable Pagination information.
     * @return Page of translatable entities.
     */
    open fun findAllByLocaleId(localeId: LOCALE_ID, pageable: Pageable): Page<T> =
        repository.findAllByLocaleId(localeId, pageable)

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
     * Deletes all translatable entities that have a translation with the given locale id.
     *
     * @param localeId The locale id of the translations to delete.
     * @return The number of deleted entities.
     */
    @Transactional
    open fun deleteByLocaleId(localeId: LOCALE_ID): Int = repository.deleteByLocaleId(localeId)

    /**
     * Deletes a translatable entity if it has a translation with the given ID and locale id.
     *
     * @param id The ID of the entity.
     * @param localeId The locale id of the translation.
     * @return The number of deleted entities (usually 0 or 1).
     */
    @Transactional
    open fun deleteByIdAndLocaleId(id: ID, localeId: LOCALE_ID): Int = repository.deleteByIdAndLocaleId(id, localeId)
}
