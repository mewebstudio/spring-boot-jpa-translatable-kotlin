package com.mewebstudio.springboot.jpa.translatable.kotlin

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Abstract service class for translation entities.
 *
 * @param T The type of the translation entity.
 * @param ID The type of the entity's identifier.
 * @param OWNER The type of the translatable entity that owns the translation.
 * @param repository The JPA repository for the translation entity.
 */
abstract class AbstractTranslationService<T : ITranslation<ID, OWNER>, ID, OWNER>(
    open val repository: JpaTranslationRepository<T, ID, OWNER>
) {
    /**
     * Finds a translation for a specific owner.
     *
     * @param ownerId ID The ID of the translation entity.
     * @return Boolean indicating if the translation exists.
     */
    open fun existsByOwnerId(ownerId: ID): Boolean = repository.existsByOwnerId(ownerId)

    /**
     * Finds all translations for a specific owner by its ID.
     *
     * @param ownerId The ID of the owner entity.
     * @return List of translations for the owner.
     */
    open fun findByOwnerId(ownerId: ID): List<T> = repository.findByOwnerId(ownerId)

    /**
     * Checks if a translation exists for a specific locale.
     *
     * Note: This method does not require a transaction as it performs a single COUNT query.
     *
     * @param locale The locale to check for.
     * @return True if at least one translation with the given locale exists, false otherwise.
     */
    open fun existsByLocale(locale: String): Boolean = repository.existsByLocale(locale)

    /**
     * Checks if a translation exists for a specific owner and locale.
     *
     * Note: This method does not require a transaction as it performs a single COUNT query.
     *
     * @param ownerId The ID of the owner entity.
     * @param locale The locale to check for.
     * @return True if a translation exists for the owner and locale, false otherwise.
     */
    open fun existsByOwnerIdAndLocale(ownerId: ID, locale: String): Boolean =
        repository.existsByOwnerIdAndLocale(ownerId, locale)

    /**
     * Finds all translations for a specific owner by its ID, with pagination.
     *
     * @param ownerId The ID of the owner entity.
     * @param pageable Pagination information.
     * @return Page of translations for the owner.
     */
    open fun findByOwnerId(ownerId: ID, pageable: Pageable): Page<T> = repository.findByOwnerId(ownerId, pageable)

    /**
     * Finds a translation for a specific owner and locale.
     *
     * @param ownerId The ID of the owner entity.
     * @param locale The locale of the translation.
     * @return The translation, or null if not found.
     * @throws IllegalArgumentException If the translation is not found.
     */
    open fun findByOwnerIdAndLocale(ownerId: ID, locale: String): T? =
        repository.findByOwnerIdAndLocale(ownerId, locale)

    /**
     * Saves a translation entity.
     *
     * @param translation The translation entity to save.
     * @return The saved translation entity.
     */
    @Transactional
    open fun save(translation: T): T = repository.save(translation)

    /**
     * Deletes a translation for a specific owner and locale.
     *
     * @param ownerId The ID of the owner entity.
     * @param locale The locale of the translation to delete.
     * @return The number of deleted translations (usually 0 or 1).
     * @throws IllegalArgumentException If the translation is not found.
     */
    @Transactional
    open fun deleteByOwnerIdAndLocale(ownerId: ID, locale: String): Int = run {
        repository.findByOwnerIdAndLocale(ownerId, locale)
            ?: throw EntityNotFoundException("Translation for owner id $ownerId and locale $locale not found")

        repository.deleteByOwnerIdAndLocale(ownerId, locale)
    }

    /**
     * Deletes all translations for a specific locale.
     *
     * @param locale The locale of the translations to delete.
     * @return The number of deleted translations.
     * @throws IllegalArgumentException If no translations are found for the given locale.
     */
    @Transactional
    open fun deleteByLocale(locale: String): Int = run {
        if (!existsByLocale(locale)) {
            throw IllegalArgumentException("No translations found for locale $locale")
        }

        repository.deleteByLocale(locale)
    }
}
