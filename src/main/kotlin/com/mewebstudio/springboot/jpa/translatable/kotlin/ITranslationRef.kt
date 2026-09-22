package com.mewebstudio.springboot.jpa.translatable.kotlin

/**
 * Variant of [ITranslation] for translation rows whose locale reference is the owning locale
 * entity's own (typically immutable) primary key, rather than a human-readable locale string
 * (e.g. `"en"`, `"tr-TR"`). Use this when the locale a translation belongs to is modeled as a
 * real FK to a `Locale`-like entity whose business code (name, ISO code, ...) can change after
 * creation — keying by that entity's id means such a rename never requires touching any
 * translation row, unlike [ITranslation.locale], which IS the FK value itself.
 *
 * Fully independent of [ITranslation] — implement this one instead, not both, for a given
 * translation entity. Existing code using [ITranslation] is entirely unaffected by this addition.
 *
 * @param ID The type of the identifier for the translation entity.
 * @param T The type of the owner entity.
 * @param LOCALE_ID The type of the referenced locale entity's own identifier.
 */
interface ITranslationRef<ID, T, LOCALE_ID> {
    /**
     * Get the ID of the translation entity.
     */
    val id: ID

    /**
     * Get the owner of the translation entity.
     */
    val owner: T

    /**
     * Get the id of the locale entity this translation belongs to.
     */
    val localeId: LOCALE_ID
}
