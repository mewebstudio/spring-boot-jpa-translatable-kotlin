package com.mewebstudio.springboot.jpa.translatable.kotlin

/**
 * Variant of [ITranslatable] for translatable entities whose translations implement
 * [ITranslationRef] instead of [ITranslation]. See [ITranslationRef] for why you'd pick this
 * pairing over the string-locale one.
 *
 * @param ID The type of the identifier for the translatable entity.
 * @param T The type of the translation entity.
 */
interface ITranslatableRef<ID, T : ITranslationRef<ID, *, *>> {
    /**
     * Get the ID of the translatable entity.
     */
    val id: ID

    /**
     * Get the translations of the translatable entity.
     */
    val translations: MutableList<T>
}
