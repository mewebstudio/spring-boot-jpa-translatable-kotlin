package com.mewebstudio.springboot.jpa.translatable.kotlin

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean

/**
 * [JpaTranslationRepository]'s counterpart for [ITranslationRef] — every query below is the same
 * shape, just keyed by `t.localeId` instead of `t.locale`.
 *
 * @param T The type of the translation entity.
 * @param ID The type of the entity's identifier.
 * @param OWNER The type of the owner entity.
 * @param LOCALE_ID The type of the referenced locale entity's own identifier.
 */
@NoRepositoryBean
interface JpaTranslationRefRepository<T : ITranslationRef<ID, OWNER, LOCALE_ID>, ID : Any, OWNER, LOCALE_ID> :
    JpaRepository<T, ID> {
    /**
     * Checks if a translation exists for a specific locale id.
     *
     * @param localeId The locale id to check for.
     * @return True if at least one translation with the given locale id exists, false otherwise.
     */
    @Query(
        """
        SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END
        FROM #{#entityName} t WHERE t.localeId = :localeId
        """
    )
    fun existsByLocaleId(localeId: LOCALE_ID): Boolean

    /**
     * Finds all translations for a specific owner.
     *
     * @param ownerId The ID of the owner entity.
     * @return Boolean indicating if the translation exists.
     */
    @Query("SELECT t FROM #{#entityName} t WHERE t.owner.id = :ownerId")
    fun existsByOwnerId(ownerId: ID): Boolean

    /**
     * Checks if a translation exists for a specific owner and locale id.
     *
     * @param ownerId The ID of the owner entity.
     * @param localeId The locale id to check for.
     * @return True if a translation exists for the owner and locale id, false otherwise.
     */
    @Query(
        """
        SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END
        FROM #{#entityName} t WHERE t.owner.id = :ownerId AND t.localeId = :localeId
        """
    )
    fun existsByOwnerIdAndLocaleId(ownerId: ID, localeId: LOCALE_ID): Boolean

    /**
     * Finds all translations for a specific owner by its ID.
     *
     * @param ownerId The ID of the owner entity.
     * @return List of translations for the owner.
     */
    @Query("SELECT t FROM #{#entityName} t WHERE t.owner.id = :ownerId")
    fun findByOwnerId(ownerId: ID): List<T>

    /**
     * Finds all translations for a specific owner by its ID, with pagination.
     *
     * @param ownerId The ID of the owner entity.
     * @param pageable Pagination information.
     * @return Page of translations for the owner.
     */
    @Query("SELECT t FROM #{#entityName} t WHERE t.owner.id = :ownerId")
    fun findByOwnerId(ownerId: ID, pageable: Pageable): Page<T>

    /**
     * Finds a translation for a specific owner and locale id.
     *
     * @param ownerId The ID of the owner entity.
     * @param localeId The locale id of the translation.
     * @return The translation, or null if not found.
     */
    @Query("SELECT t FROM #{#entityName} t WHERE t.owner.id = :ownerId AND t.localeId = :localeId")
    fun findByOwnerIdAndLocaleId(ownerId: ID, localeId: LOCALE_ID): T?

    /**
     * Deletes all translations for a specific locale id.
     *
     * @param localeId The locale id of the translations to delete.
     */
    @Modifying
    @Query("DELETE FROM #{#entityName} t WHERE t.localeId = :localeId")
    fun deleteByLocaleId(localeId: LOCALE_ID): Int

    /**
     * Deletes a translation for a specific owner and locale id.
     *
     * @param ownerId The ID of the owner entity.
     * @param localeId The locale id of the translation to delete.
     */
    @Modifying
    @Query("DELETE FROM #{#entityName} t WHERE t.owner.id = :ownerId AND t.localeId = :localeId")
    fun deleteByOwnerIdAndLocaleId(ownerId: ID, localeId: LOCALE_ID): Int
}
