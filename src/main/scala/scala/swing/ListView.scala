/*
 * scala-swing (https://www.scala-lang.org)
 *
 * Copyright EPFL, Lightbend, Inc., contributors
 *
 * Licensed under Apache License 2.0
 * (http://www.apache.org/licenses/LICENSE-2.0).
 *
 * See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 */

package scala.swing

import event.*
import javax.swing.*
import javax.swing.event.*

import scala.collection.immutable

object ListView:
    /**
   * The supported modes of user selections.
   */
    object IntervalMode extends Enumeration:
        import ListSelectionModel.*
        val Single: IntervalMode.Value         = Value(SINGLE_SELECTION)
        val SingleInterval: IntervalMode.Value = Value(SINGLE_INTERVAL_SELECTION)
        val MultiInterval: IntervalMode.Value  = Value(MULTIPLE_INTERVAL_SELECTION)
    end IntervalMode

    def wrap[A](c: JList[A]): ListView[A] = new ListView[A]:
        override lazy val peer: JList[A] = c

    object Renderer:
        def wrap[A](r: ListCellRenderer[A]): Renderer[A] = new Wrapped[A](r)

        /**
     * Wrapper for <code>javax.swing.ListCellRenderer<code>s
     */
        class Wrapped[A](override val peer: ListCellRenderer[A]) extends Renderer[A]:
            def componentFor(list: ListView[? <: A], isSelected: Boolean, focused: Boolean, a: A,
                index: Int): Component =
                Component.wrap(peer.getListCellRendererComponent(list.peer, a, index, isSelected, focused).asInstanceOf[
                        JComponent])
        end Wrapped

        /**
     * Returns a renderer for items of type <code>A</code>. The given function
     * converts items of type <code>A</code> to items of type <code>B</code>
     * for which a renderer is implicitly given. This allows chaining of
     * renderers, e.g.:
     *
     * <code>
     * case class Person(name: String, email: String)
     * val persons = List(Person("John", "j.doe@a.com"), Person("Mary", "m.jane@b.com"))
     * new ListView(persons) {
     *   renderer = ListView.Renderer(_.name)
     * }
     * </code>
     */
        def apply[A, B](f: A => B)(implicit renderer: Renderer[B]): Renderer[A] = new Renderer[A]:
            def componentFor(list: ListView[? <: A], isSelected: Boolean, focused: Boolean, a: A,
                index: Int): Component =
                renderer.componentFor(list.asInstanceOf[ListView[? <: B]], isSelected, focused, f(a), index)
    end Renderer

    /**
   * Item renderer for a list view. This is contravariant on the type of the
   * items, so a more general renderer can be used in place of a more specific
   * one. For instance, an <code>Any</code> renderer can be used for a list view
   * of strings.
   *
   * @see javax.swing.ListCellRenderer
   */
    abstract class Renderer[-A]:
        def peer: ListCellRenderer[? >: A] = new ListCellRenderer[A]:
            def getListCellRendererComponent(list: JList[? <: A], a: A, index: Int, isSelected: Boolean,
                focused: Boolean): JComponent =
                componentFor(ListView.wrap[A](list.asInstanceOf[JList[A]]), isSelected, focused, a, index).peer
        def componentFor(list: ListView[? <: A], isSelected: Boolean, focused: Boolean, a: A, index: Int): Component
    end Renderer

    /**
   * A default renderer that maintains a single component for item rendering
   * and preconfigures it to sensible defaults. It is polymorphic on the
   * component's type so clients can easily use component specific attributes
   * during configuration.
   */
    abstract class AbstractRenderer[-A, C <: Component](protected val component: C) extends Renderer[A]:
        // The renderer component is responsible for painting selection
        // backgrounds. Hence, make sure it is opaque to let it draw
        // the background.
        component.opaque = true

        /**
     * Standard preconfiguration that is commonly done for any component.
     * This includes foreground and background colors, as well as colors
     * of item selections.
     */
        def preConfigure(list: ListView[?], isSelected: Boolean, focused: Boolean, a: A, index: Int): Unit =
            if isSelected then
                component.background = list.selectionBackground
                component.foreground = list.selectionForeground
            else
                component.background = list.background
                component.foreground = list.foreground

        /**
     * Configuration that is specific to the component and this renderer.
     */
        def configure(list: ListView[?], isSelected: Boolean, focused: Boolean, a: A, index: Int): Unit

        /**
     * Configures the component before returning it.
     */
        def componentFor(list: ListView[? <: A], isSelected: Boolean, focused: Boolean, a: A, index: Int): Component =
            preConfigure(list, isSelected, focused, a, index)
            configure(list, isSelected, focused, a, index)
            component
        end componentFor
    end AbstractRenderer

    /**
   * A generic renderer that uses Swing's built-in renderers. If there is no
   * specific renderer for a type, this renderer falls back to a renderer
   * that renders the string returned from an item's <code>toString</code>.
   */
    implicit object GenericRenderer extends Renderer[Any]:
        override lazy val peer: ListCellRenderer[Any] =
            (new DefaultListCellRenderer).asInstanceOf[ListCellRenderer[Any]]
        def componentFor(list: ListView[? <: Any], isSelected: Boolean, focused: Boolean, a: Any,
            index: Int): Component =
            val c = peer.getListCellRendererComponent(list.peer, a, index, isSelected, focused)
            Component.wrap(c.asInstanceOf[JComponent])
        end componentFor
    end GenericRenderer
end ListView

/**
 * A component that displays a number of elements in a list. A list view does
 * not support inline editing of items. If you need it, use a table view instead.
 *
 * Named <code>ListView</code> to avoid a clash with the frequently used
 * <code>scala.List</code>
 *
 * @see javax.swing.JList
 */
class ListView[A] extends Component:
    import ListView.*
    override lazy val peer: JList[A] = new JList[A] with SuperMixin

    def this(items: scala.collection.Seq[A]) =
        this()
        listData = items

    protected class ModelWrapper[B](val items: scala.collection.Seq[B]) extends AbstractListModel[B]:
        def getElementAt(n: Int): B = items(n)
        def getSize: Int            = items.size

    def listData: scala.collection.Seq[A] = peer.getModel match
        case model: ModelWrapper[a] => model.items
        case model => new immutable.Seq[A]:
                selfSeq =>
                def length: Int = model.getSize
                def iterator: Iterator[A] = new Iterator[A]:
                    var idx = 0
                    def next(): A =
                        idx += 1; apply(idx - 1)
                    def hasNext: Boolean = idx < selfSeq.length
                def apply(n: Int): A = model.getElementAt(n)

    def listData_=(items: scala.collection.Seq[A]): Unit =
        peer.setModel(new AbstractListModel[A]:
            def getElementAt(n: Int): A = items(n)
            def getSize: Int            = items.size
        )

    /**
   * The current item selection.
   */
    object selection extends Publisher:
        protected abstract class Indices[B](a: => scala.collection.Seq[B]) extends SetWrapper[B]:
            def contains(n: B): Boolean = a.contains(n)

            override def size: Int = a.length

            def iterator: Iterator[B] = a.iterator
        end Indices

        def leadIndex: Int   = peer.getSelectionModel.getLeadSelectionIndex
        def anchorIndex: Int = peer.getSelectionModel.getAnchorSelectionIndex

        /**
     * The indices of the currently selected items.
     */
        object indices extends Indices(peer.getSelectedIndices):
            override def addOne(n: Int): this.type =
                peer.addSelectionInterval(n, n); this
            override def subtractOne(n: Int): this.type =
                peer.removeSelectionInterval(n, n); this

            override def clear(): Unit = peer.clearSelection()
        end indices

        /**
     * The currently selected items.
     */
        def items: immutable.Seq[A] =
            // note: we should be using `getSelectedValuesList`, but it would break the Scala 2.11
            // promise of working with Java 6 (requires Java 7).
            // Note: in Scala <= 2.12, toSeq might produce a Stream.
            peer.getSelectedValues.iterator.map(_.asInstanceOf[A]).toIndexedSeq

        def intervalMode: IntervalMode.Value            = IntervalMode(peer.getSelectionModel.getSelectionMode)
        def intervalMode_=(m: IntervalMode.Value): Unit = peer.getSelectionModel.setSelectionMode(m.id)

        peer.getSelectionModel.addListSelectionListener(new ListSelectionListener:
                def valueChanged(e: javax.swing.event.ListSelectionEvent): Unit =
                    publish(new ListSelectionChanged(ListView.this, e.getFirstIndex to e.getLastIndex,
                            e.getValueIsAdjusting))
        )

        def adjusting: Boolean = peer.getSelectionModel.getValueIsAdjusting
    end selection

    def renderer: ListView.Renderer[A]            = ListView.Renderer.wrap(peer.getCellRenderer)
    def renderer_=(r: ListView.Renderer[A]): Unit = peer.setCellRenderer(r.peer)

    def fixedCellWidth: Int            = peer.getFixedCellWidth
    def fixedCellWidth_=(x: Int): Unit = peer.setFixedCellWidth(x)

    def fixedCellHeight: Int            = peer.getFixedCellHeight
    def fixedCellHeight_=(x: Int): Unit = peer.setFixedCellHeight(x)

    def prototypeCellValue: A            = peer.getPrototypeCellValue
    def prototypeCellValue_=(a: A): Unit = peer.setPrototypeCellValue(a)

    def visibleRowCount: Int            = peer.getVisibleRowCount
    def visibleRowCount_=(n: Int): Unit = peer.setVisibleRowCount(n)

    def ensureIndexIsVisible(idx: Int): Unit = peer.ensureIndexIsVisible(idx)

    def selectionForeground: Color            = peer.getSelectionForeground
    def selectionForeground_=(c: Color): Unit = peer.setSelectionForeground(c)
    def selectionBackground: Color            = peer.getSelectionBackground
    def selectionBackground_=(c: Color): Unit = peer.setSelectionBackground(c)

    def selectIndices(ind: Int*): Unit = peer.setSelectedIndices(ind.toArray)

    peer.getModel.addListDataListener(new ListDataListener:
        def contentsChanged(e: ListDataEvent): Unit = publish(ListChanged(ListView.this))
        def intervalRemoved(e: ListDataEvent): Unit = publish(ListElementsRemoved(ListView.this,
                e.getIndex0 to e.getIndex1))
        def intervalAdded(e: ListDataEvent): Unit =
            publish(ListElementsAdded(ListView.this, e.getIndex0 to e.getIndex1))
    )
end ListView
