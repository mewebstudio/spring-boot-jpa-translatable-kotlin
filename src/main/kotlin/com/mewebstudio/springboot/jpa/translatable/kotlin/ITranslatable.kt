package com.mewebstudio.springboot.jpa.translatable.kotlin

/**
 * Interface representing a translatable entity.
 *
 * @param ID The type of the identifier for the translatable entity.
 * @param T The type of the translation entity.
 */
interface ITranslatable<ID, T : ITranslation<ID, *>> {
    /**
     * Get the ID of the translatable entity.
     */
    val id: ID

    /**
     * Get the translations of the translatable entity.
     */
    val translations: MutableList<T>
}
