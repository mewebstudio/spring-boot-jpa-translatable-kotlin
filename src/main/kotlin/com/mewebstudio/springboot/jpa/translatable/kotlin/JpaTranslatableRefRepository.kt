package com.mewebstudio.springboot.jpa.translatable.kotlin

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean

/**
 * [JpaTranslatableRepository]'s counterpart for [ITranslatableRef]/[ITranslationRef] — every
 * query below is the same shape, just keyed by `t.localeId` instead of `t.locale`.
 *
 * @param T The type of the translatable entity.
 * @param ID The type of the entity's identifier.
 * @param TR The type of the translation entity.
 * @param LOCALE_ID The type of the referenced locale entity's own identifier.
 */
@NoRepositoryBean
interface JpaTranslatableRefRepository<
    T : ITranslatableRef<ID, TR>,
    ID : Any,
    TR : ITranslationRef<ID, *, LOCALE_ID>,
    LOCALE_ID,
    > : JpaRepository<T, ID> {
    /**
     * Checks if a translatable entity exists by its ID and locale id.
     *
     * @param id The ID of the entity.
     * @param localeId The locale id of the entity.
     * @return True if the entity exists, false otherwise.
     */
    @Query(
        "SELECT COUNT(e) > 0 FROM #{#entityName} e JOIN e.translations t WHERE e.id = :id AND t.localeId = :localeId"
    )
    fun existsByIdAndLocaleId(id: ID, localeId: LOCALE_ID): Boolean

    /**
     * Finds a translatable entity by its ID and locale id.
     *
     * @param id The ID of the entity.
     * @param localeId The locale id of the entity.
     * @return The translatable entity, or null if not found.
     */
    @Query("SELECT e FROM #{#entityName} e JOIN e.translations t WHERE e.id = :id AND t.localeId = :localeId")
    fun findByIdAndLocaleId(id: ID, localeId: LOCALE_ID): T?

    /**
     * Finds all translatable entities that have a translation with the given locale id.
     *
     * @param localeId The locale id to filter by.
     * @return List of translatable entities.
     */
    @Query("SELECT DISTINCT e FROM #{#entityName} e JOIN e.translations t WHERE t.localeId = :localeId")
    fun findAllByLocaleId(localeId: LOCALE_ID): List<T>

    /**
     * Finds all translatable entities that have a translation with the given locale id, with pagination.
     *
     * @param localeId The locale id to filter by.
     * @param pageable Pagination information.
     * @return Page of translatable entities.
     */
    @Query("SELECT DISTINCT e FROM #{#entityName} e JOIN e.translations t WHERE t.localeId = :localeId")
    fun findAllByLocaleId(localeId: LOCALE_ID, pageable: Pageable): Page<T>

    /**
     * Finds all translations for a specific translatable entity by its ID.
     *
     * @param id The ID of the entity.
     * @return List of translations for the entity.
     */
    @Query("SELECT t FROM #{#entityName} e JOIN e.translations t WHERE e.id = :id")
    fun findTranslationsById(id: ID): List<TR>

    /**
     * Finds all translations for a specific translatable entity by its ID, with pagination.
     *
     * @param id The ID of the entity.
     * @param pageable Pagination information.
     * @return Page of translations for the entity.
     */
    @Query("SELECT t FROM #{#entityName} e JOIN e.translations t WHERE e.id = :id")
    fun findTranslationsById(id: ID, pageable: Pageable): Page<TR>

    /**
     * Deletes all translatable entities that have a translation with the given locale id.
     *
     * @param localeId The locale id of the translations to delete.
     * @return The number of deleted entities.
     */
    @Modifying
    @Query(
        """
        DELETE FROM #{#entityName} e
        WHERE EXISTS (
            SELECT 1 FROM e.translations t WHERE t.localeId = :localeId
        )
        """
    )
    fun deleteByLocaleId(localeId: LOCALE_ID): Int

    /**
     * Deletes a translatable entity if it has a translation with the given ID and locale id.
     *
     * @param id The ID of the entity.
     * @param localeId The locale id of the translation.
     * @return The number of deleted entities.
     */
    @Modifying
    @Query(
        """
        DELETE FROM #{#entityName} e
        WHERE e.id = :id
        AND EXISTS (
            SELECT 1 FROM e.translations t WHERE t.localeId = :localeId
        )
        """
    )
    fun deleteByIdAndLocaleId(id: ID, localeId: LOCALE_ID): Int
}
