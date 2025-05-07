package com.mewebstudio.springboot.jpa.translatable.kotlin

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean

/**
 * Repository interface for translatable entities.
 * This interface extends the JpaRepository interface and provides methods for working with translatable entities.
 *
 * @param T The type of the translatable entity.
 * @param ID The type of the entity's identifier.
 */
@NoRepositoryBean
interface JpaTranslationRepository<T : ITranslation<ID, OWNER>, ID, OWNER> : JpaRepository<T, ID> {
    /**
     * Checks if a translation exists for a specific locale.
     *
     * @param locale The locale to check for.
     * @return True if at least one translation with the given locale exists, false otherwise.
     */
    @Query(
        """
        SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END 
        FROM #{#entityName} t WHERE t.locale = :locale
        """
    )
    fun existsByLocale(locale: String): Boolean

    /**
     * Finds all translations for a specific owner.
     *
     * @param ownerId ID The locale to filter by.
     * @return Boolean indicating if the translation exists.
     */
    @Query("SELECT t FROM #{#entityName} t WHERE t.locale = :locale")
    fun existsByOwnerId(ownerId: ID): Boolean

    /**
     * Checks if a translation exists for a specific owner and locale.
     *
     * @param ownerId The ID of the owner entity.
     * @param locale The locale to check for.
     * @return True if a translation exists for the owner and locale, false otherwise.
     */
    @Query(
        """
        SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END 
        FROM #{#entityName} t WHERE t.owner.id = :ownerId AND t.locale = :locale
        """
    )
    fun existsByOwnerIdAndLocale(ownerId: ID, locale: String): Boolean

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
     * Finds a translation for a specific owner and locale.
     *
     * @param ownerId The ID of the owner entity.
     * @param locale The locale of the translation.
     * @return The translation, or null if not found.
     */
    @Query("SELECT t FROM #{#entityName} t WHERE t.owner.id = :ownerId AND t.locale = :locale")
    fun findByOwnerIdAndLocale(ownerId: ID, locale: String): T?

    /**
     * Finds all translations with a specific name and locale.
     *
     * @param name The name to search for in translations.
     * @param locale The locale of the translation.
     * @return List of translations.
     */
    @Query("SELECT t FROM #{#entityName} t WHERE t.name = :name AND t.locale = :locale")
    fun findByNameAndLocale(name: String, locale: String): List<T>

    /**
     * Deletes all translations for a specific locale.
     *
     * @param locale The locale of the translations to delete.
     */
    @Modifying
    @Query("DELETE FROM #{#entityName} t WHERE t.locale = :locale")
    fun deleteByLocale(locale: String): Int

    /**
     * Deletes a translation for a specific owner and locale.
     *
     * @param ownerId The ID of the owner entity.
     * @param locale The locale of the translation to delete.
     */
    @Modifying
    @Query("DELETE FROM #{#entityName} t WHERE t.owner.id = :ownerId AND t.locale = :locale")
    fun deleteByOwnerIdAndLocale(ownerId: ID, locale: String): Int
}
