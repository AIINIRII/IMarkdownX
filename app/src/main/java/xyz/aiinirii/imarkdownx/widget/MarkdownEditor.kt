package xyz.aiinirii.imarkdownx.widget

/**
 *
 * @author AIINIRII
 */
interface MarkdownEditor {

    /**
     * undo operation
     * @param preload whether preload the text
     * @return Boolean
     */
    fun undo(preload: Boolean): Boolean

    /**
     * redo operation
     * @param preload whether preload the text
     * @return Boolean
     */
    fun redo(preload: Boolean): Boolean

    /**
     * insert delete mark
     * @return Boolean
     */
    fun delete(): Boolean

    /**
     * insert heading mark
     * @return Boolean
     */
    fun heading(): Boolean

    /**
     * insert bold mark
     * @return Boolean
     */
    fun bold(): Boolean

    /**
     * insert link mark
     * @return Boolean
     */
    fun link(): Boolean

    /**
     * insert code mark
     * @return Boolean
     */
    fun code(): Boolean

    /**
     * insert quote mark
     * @return Boolean
     */
    fun quote(): Boolean

    /**
     * insert linkAlt mark
     * @return Boolean
     */
    fun linkAlt(): Boolean

    /**
     * insert list mark
     * @return Boolean
     */
    fun list(): Boolean

    /**
     * insert table mark
     * @param row Int
     * @param col Int
     * @return Boolean
     */
    fun table(row: Int, col: Int): Boolean

    /**
     * insert image mark
     * @param url the url of the file
     * @return Boolean
     */
    fun image(url: String = ""): Boolean

    /**
     * insert italic mark
     * @return Boolean
     */
    fun italic(): Boolean

    /**
     * insert italic mark
     * @return Boolean
     */
    fun hr(): Boolean
}