package com.mewebstudio.springboot.jpa.translatable.kotlin

/**
 * Interface representing a translation entity.
 *
 * @param ID The type of the identifier for the translation entity.
 * @param T The type of the owner entity.
 */
interface ITranslation<ID, T> {
    /**
     * Get the ID of the translation entity.
     */
    val id: ID

    /**
     * Get the owner of the translation entity.
     */
    val owner: T

    /**
     * Get the locale of the translation entity.
     */
    val locale: String
}
