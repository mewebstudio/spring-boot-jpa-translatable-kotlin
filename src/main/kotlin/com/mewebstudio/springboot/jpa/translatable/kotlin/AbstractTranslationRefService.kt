package com.mewebstudio.springboot.jpa.translatable.kotlin

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * [AbstractTranslationService]'s counterpart for [ITranslationRef] / [JpaTranslationRefRepository].
 *
 * @param T The type of the translation entity.
 * @param ID The type of the entity's identifier.
 * @param OWNER The type of the translatable entity that owns the translation.
 * @param LOCALE_ID The type of the referenced locale entity's own identifier.
 * @param repository The JPA repository for the translation entity.
 */
abstract class AbstractTranslationRefService<T : ITranslationRef<ID, OWNER, LOCALE_ID>, ID : Any, OWNER, LOCALE_ID>(
    open val repository: JpaTranslationRefRepository<T, ID, OWNER, LOCALE_ID>
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
     * Checks if a translation exists for a specific locale id.
     *
     * Note: This method does not require a transaction as it performs a single COUNT query.
     *
     * @param localeId The locale id to check for.
     * @return True if at least one translation with the given locale id exists, false otherwise.
     */
    open fun existsByLocaleId(localeId: LOCALE_ID): Boolean = repository.existsByLocaleId(localeId)

    /**
     * Checks if a translation exists for a specific owner and locale id.
     *
     * Note: This method does not require a transaction as it performs a single COUNT query.
     *
     * @param ownerId The ID of the owner entity.
     * @param localeId The locale id to check for.
     * @return True if a translation exists for the owner and locale id, false otherwise.
     */
    open fun existsByOwnerIdAndLocaleId(ownerId: ID, localeId: LOCALE_ID): Boolean =
        repository.existsByOwnerIdAndLocaleId(ownerId, localeId)

    /**
     * Finds all translations for a specific owner by its ID, with pagination.
     *
     * @param ownerId The ID of the owner entity.
     * @param pageable Pagination information.
     * @return Page of translations for the owner.
     */
    open fun findByOwnerId(ownerId: ID, pageable: Pageable): Page<T> = repository.findByOwnerId(ownerId, pageable)

    /**
     * Finds a translation for a specific owner and locale id.
     *
     * @param ownerId The ID of the owner entity.
     * @param localeId The locale id of the translation.
     * @return The translation, or null if not found.
     */
    open fun findByOwnerIdAndLocaleId(ownerId: ID, localeId: LOCALE_ID): T? =
        repository.findByOwnerIdAndLocaleId(ownerId, localeId)

    /**
     * Saves a translation entity.
     *
     * @param translation The translation entity to save.
     * @return The saved translation entity.
     */
    @Transactional
    open fun save(translation: T): T = repository.save(translation)

    /**
     * Deletes a translation for a specific owner and locale id.
     *
     * @param ownerId The ID of the owner entity.
     * @param localeId The locale id of the translation to delete.
     * @return The number of deleted translations (usually 0 or 1).
     * @throws IllegalArgumentException If the translation is not found.
     */
    @Transactional
    open fun deleteByOwnerIdAndLocaleId(ownerId: ID, localeId: LOCALE_ID): Int = run {
        repository.findByOwnerIdAndLocaleId(ownerId, localeId)
            ?: throw EntityNotFoundException("Translation for owner id $ownerId and locale id $localeId not found")

        repository.deleteByOwnerIdAndLocaleId(ownerId, localeId)
    }

    /**
     * Deletes all translations for a specific locale id.
     *
     * @param localeId The locale id of the translations to delete.
     * @return The number of deleted translations.
     * @throws IllegalArgumentException If no translations are found for the given locale id.
     */
    @Transactional
    open fun deleteByLocaleId(localeId: LOCALE_ID): Int = run {
        if (!existsByLocaleId(localeId)) {
            throw IllegalArgumentException("No translations found for locale id $localeId")
        }

        repository.deleteByLocaleId(localeId)
    }
}
