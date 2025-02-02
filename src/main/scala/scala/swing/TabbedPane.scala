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

import javax.swing.JTabbedPane

object TabbedPane:
    object Layout extends Enumeration:
        import JTabbedPane.*
        val Wrap: Layout.Value   = Value(WRAP_TAB_LAYOUT)
        val Scroll: Layout.Value = Value(SCROLL_TAB_LAYOUT)
    end Layout

    class Page protected[TabbedPane] (parent0: TabbedPane, title0: String, content0: Component, tip0: String)
        extends Proxy:
        def self: Any = content0

        def this(title0: String, content0: Component, tip0: String) =
            this(null, title0, content0, tip0)
        def this(title0: String, content0: Component) =
            this(title0, content0, "")
        content = content0 // first add component, *then* set other things
        title = title0
        tip = tip0

        protected[TabbedPane] var parent: TabbedPane = parent0

        protected var _title: String = title0
        def title: String            = _title
        def title_=(t: String): Unit =
            // beware to keep this order since, index depends on the _old_ title
            if parent != null then parent.peer.setTitleAt(index, t)
            _title = t
        end title_=
        protected var _content: Component = content0
        def content: Component =
            _content // UIElement.cachedWrapper(peer.getComponentAt(index).asInstanceOf[JComponent])
        def content_=(c: Component): Unit =
            _content = c; if parent != null then parent.peer.setComponentAt(index, c.peer)
        protected var _tip: String = tip0
        def tip: String            = _tip // peer.getToolTipTextAt(index)
        def tip_=(t: String): Unit =
            _tip = t; if parent != null then parent.peer.setToolTipTextAt(index, if t == "" then null else t)
        protected var _enabled = true
        def enabled: Boolean   = _enabled // peer.isEnabledAt(index)
        def enabled_=(b: Boolean): Unit =
            _enabled = b; if parent != null then parent.peer.setEnabledAt(index, b)
        protected var _mnemonic: Int = -1
        def mnemonic: Int            = _mnemonic // peer.getMnemonicAt(index)
        def mnemonic_=(k: Int): Unit =
            _mnemonic = k; if parent != null then parent.peer.setMnemonicAt(index, k)
        protected var _foreground: Color = null
        def foreground: Color            = _foreground // peer.getForegroundAt(index)
        def foreground_=(c: Color): Unit =
            _foreground = c; if parent != null then parent.peer.setForegroundAt(index, c)
        protected var _background: Color = null
        def background: Color            = _background // peer.getBackgroundAt(index)
        def background_=(c: Color): Unit =
            _background = c; if parent != null then parent.peer.setBackgroundAt(index, c)
        def bounds: Rectangle = parent.peer.getBoundsAt(index)

        // TODO: icon, disabledIcon

        def index: Int = if parent != null then parent.peer.indexOfTab(title) else 0 // _index
        // protected[TabbedPane] var _index: Int = index0
    end Page
end TabbedPane

/**
 * Displays the contents of one of several pages at a time. For each page a tab is
 * visible at all times. The user can click on one of these tabs to move the
 * corresponding page to the front.
 *
 * @see javax.swing.JTabbedPane
 */
class TabbedPane extends Component with Publisher:
    override lazy val peer: JTabbedPane = new JTabbedPane with SuperMixin
    import TabbedPane.*

    object pages extends BufferWrapper[Page]:
        def runCount: Int = peer.getTabRunCount

        def remove(n: Int): Page =
            val t = apply(n)
            peer.removeTabAt(n)
            t.parent = null
            // for(i <- n to length) apply(i)._index -= 1
            t
        end remove

        override def insert(n: Int, t: Page): Unit =
            // for(i <- n to length) apply(i)._index += 1
            t.parent = TabbedPane.this
            peer.insertTab(t.title, null, t.content.peer, if t.tip == "" then null else t.tip, n)
        end insert

        override def addOne(t: Page): this.type =
            t.parent = TabbedPane.this
            peer.addTab(t.title, null, t.content.peer, if t.tip == "" then null else t.tip)
            this
        end addOne

        def length: Int = peer.getTabCount

        def apply(n: Int): Page = new Page(TabbedPane.this, peer.getTitleAt(n),
            UIElement.cachedWrapper[Component](peer.getComponentAt(n).asInstanceOf[javax.swing.JComponent]),
            peer.getToolTipTextAt(n))
    end pages

    def tabLayoutPolicy: Layout.Value            = Layout(peer.getTabLayoutPolicy)
    def tabLayoutPolicy_=(p: Layout.Value): Unit = peer.setTabLayoutPolicy(p.id)

    def tabPlacement: Alignment.Value = Alignment(peer.getTabPlacement)

    /**
   * Possible values are Left, Right, Top, Bottom.
   */
    def tabPlacement_=(b: Alignment.Value): Unit = peer.setTabPlacement(b.id)

    /**
   * The current page selection
   */
    object selection extends Publisher:
        def page: Page            = pages(index)
        def page_=(p: Page): Unit = index = p.index

        def index: Int            = peer.getSelectedIndex
        def index_=(n: Int): Unit = peer.setSelectedIndex(n)

        peer.addChangeListener(new javax.swing.event.ChangeListener:
                def stateChanged(e: javax.swing.event.ChangeEvent): Unit =
                    publish(event.SelectionChanged(TabbedPane.this))
        )
    end selection
end TabbedPane
