/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2007-2013, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package scala.swing

import javax.swing.JEditorPane
import javax.swing.text.EditorKit

/**
 * A text component that allows multi-line text input and display.
 *
 * @see javax.swing.JEditorPane
 */
class EditorPane(contentType0: String, text0: String) extends TextComponent {
	override lazy val peer: JEditorPane = new JEditorPane(contentType0, text0) with SuperMixin
	def this() = this("text/plain", "")

	def contentType: String = peer.getContentType
	def contentType_=(t: String): Unit = peer.setContentType(t)

	def editorKit: EditorKit = peer.getEditorKit
	def editorKit_=(k: EditorKit): Unit = peer.setEditorKit(k)
}
