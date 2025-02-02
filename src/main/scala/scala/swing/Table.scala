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

import javax.swing.event.{ListSelectionListener, TableModelEvent, TableModelListener}
import javax.swing.table.{AbstractTableModel, DefaultTableModel, TableCellEditor, TableCellRenderer, TableModel}
import javax.swing.{Icon, JComponent, JTable, ListSelectionModel, RowSorter, SortOrder}

import scala.collection.mutable
import scala.swing.event.{
    TableChanged, TableColumnsSelected, TableRowsAdded, TableRowsRemoved, TableRowsSelected, TableStructureChanged,
    TableUpdated
}

object Table:
    object AutoResizeMode extends Enumeration:
        import JTable.*
        val Off: AutoResizeMode.Value               = Value(AUTO_RESIZE_OFF, "Off")
        val NextColumn: AutoResizeMode.Value        = Value(AUTO_RESIZE_NEXT_COLUMN, "NextColumn")
        val SubsequentColumns: AutoResizeMode.Value = Value(AUTO_RESIZE_SUBSEQUENT_COLUMNS, "SubsequentColumns")
        val LastColumn: AutoResizeMode.Value        = Value(AUTO_RESIZE_LAST_COLUMN, "LastColumn")
        val AllColumns: AutoResizeMode.Value        = Value(AUTO_RESIZE_ALL_COLUMNS, "AllColumns")
    end AutoResizeMode

    object IntervalMode extends Enumeration:
        import ListSelectionModel.*
        val Single: IntervalMode.Value         = Value(SINGLE_SELECTION)
        val SingleInterval: IntervalMode.Value = Value(SINGLE_INTERVAL_SELECTION)
        val MultiInterval: IntervalMode.Value  = Value(MULTIPLE_INTERVAL_SELECTION)
    end IntervalMode
    object ElementMode extends Enumeration:
        val Row, Column, Cell, None = Value

    /**
   * A table item renderer.
   *
   * @see javax.swing.table.TableCellRenderer
   */
    abstract class Renderer[-A]:
        def peer: TableCellRenderer = new TableCellRenderer:
            def getTableCellRendererComponent(table: JTable, value: AnyRef, isSelected: Boolean, hasFocus: Boolean,
                row: Int, column: Int): JComponent =
                componentFor(table match
                        case t: JTableMixin => t.tableWrapper
                        case _              => assert(false); null
                    , isSelected, hasFocus, value.asInstanceOf[A], row, column).peer
        def componentFor(table: Table, isSelected: Boolean, hasFocus: Boolean, a: A, row: Int, column: Int): Component
    end Renderer

    abstract class AbstractRenderer[-A, C <: Component](val component: C) extends Renderer[A]:
        // The renderer component is responsible for painting selection
        // backgrounds. Hence, make sure it is opaque to let it draw
        // the background.
        component.opaque = true

        /**
     * Standard pre-configuration that is commonly done for any component.
     */
        def preConfigure(table: Table, isSelected: Boolean, hasFocus: Boolean, a: A, row: Int, column: Int): Unit =
            if isSelected then
                component.background = table.selectionBackground
                component.foreground = table.selectionForeground
            else
                component.background = table.background
                component.foreground = table.foreground

        /**
     * Configuration that is specific to the component and this renderer.
     */
        def configure(table: Table, isSelected: Boolean, hasFocus: Boolean, a: A, row: Int, column: Int): Unit

        /**
     * Configures the component before returning it.
     */
        def componentFor(table: Table, isSelected: Boolean, hasFocus: Boolean, a: A, row: Int, column: Int): Component =
            preConfigure(table, isSelected, hasFocus, a, row, column)
            configure(table, isSelected, hasFocus, a, row, column)
            component
        end componentFor
    end AbstractRenderer

    class LabelRenderer[A](convert: A => (Icon, String)) extends AbstractRenderer[A, Label](new Label):
        def this() =
            this(a => (null, a.toString))

        def configure(table: Table, isSelected: Boolean, hasFocus: Boolean, a: A, row: Int, column: Int): Unit =
            val (icon, text) = convert(a)
            component.icon = icon
            component.text = text
        end configure
    end LabelRenderer

    private[swing] trait JTableMixin:
        def tableWrapper: Table
end Table

/**
 * Displays a matrix of items.
 *
 * To obtain a scrollable table or row and columns headers,
 * wrap the table in a scroll pane.
 *
 * @see javax.swing.JTable
 */
class Table extends Component with Scrollable.Wrapper:
    override lazy val peer: JTable = new JTable with Table.JTableMixin with SuperMixin:
        def tableWrapper: Table = Table.this
        override def getCellRenderer(r: Int, c: Int): TableCellRenderer = new TableCellRenderer:
            def getTableCellRendererComponent(table: JTable, value: AnyRef, isSelected: Boolean,
                hasFocus: Boolean, row: Int, column: Int): JComponent =
                Table.this.rendererComponent(isSelected, hasFocus, row, column).peer
        override def getCellEditor(r: Int, c: Int): TableCellEditor = editor(r, c)
        override def getValueAt(r: Int, c: Int): AnyRef             = Table.this.apply(r, c).asInstanceOf[AnyRef]
    import Table.*

    // TODO: use IndexedSeq[_ <: IndexedSeq[Any]], see ticket #2005
    /** Constructs a table component with static cell contents.
    * The contents is editable.
    *
    * @param  rowData     the table contents, where the first dimension
    *                     indexes rows, and the second dimension indexes columns.
    *
    * @param  columnNames objects that represent the column names. The names are derived
    *                     by calling `toString` on the elements. The size of this sequence
    *                     must correspond with the inner dimension of `rowData`.
    */
    def this(rowData: Array[Array[Any]], columnNames: scala.collection.Seq[?]) =
        this()
        model = new AbstractTableModel:
            override def getColumnName(column: Int): String = columnNames(column).toString

            def getRowCount: Int    = rowData.length
            def getColumnCount: Int = columnNames.length

            def getValueAt(row: Int, col: Int): AnyRef = rowData(row)(col).asInstanceOf[AnyRef]

            override def isCellEditable(row: Int, column: Int) = true

            override def setValueAt(value: Any, row: Int, col: Int): Unit =
                rowData(row)(col) = value
                fireTableCellUpdated(row, col)
    end this

    /** Constructs a table component with a `DefaultTableModel` of the given dimensions. */
    def this(rows: Int, columns: Int) =
        this()
        model = new DefaultTableModel(rows, columns)

    /** Constructs a table component with a given model. */
    def this(model0: TableModel) =
        this()
        model = model0

    protected def scrollablePeer: JTable = peer

    def rowHeight: Int            = peer.getRowHeight
    def rowHeight_=(x: Int): Unit = peer.setRowHeight(x)

    def rowCount: Int = peer.getRowCount

    def model: TableModel = peer.getModel
    def model_=(x: TableModel): Unit =
        peer.setModel(x)
        model.removeTableModelListener(modelListener)
        model.addTableModelListener(modelListener)
    end model_=

    def autoResizeMode: AutoResizeMode.Value            = AutoResizeMode(peer.getAutoResizeMode)
    def autoResizeMode_=(x: AutoResizeMode.Value): Unit = peer.setAutoResizeMode(x.id)

    def showGrid: Boolean               = peer.getShowHorizontalLines && peer.getShowVerticalLines
    def showGrid_=(grid: Boolean): Unit = peer.setShowGrid(grid)

    def gridColor: Color                = peer.getGridColor
    def gridColor_=(color: Color): Unit = peer.setGridColor(color)

    def rowMargin: Int                = peer.getRowMargin
    def rowMargin_=(value: Int): Unit = peer.setRowMargin(value)

    def preferredViewportSize_=(dim: Dimension): Unit = peer.setPreferredScrollableViewportSize(dim)
    // 1.6: def fillsViewportHeight: Boolean = peer.getFillsViewportHeight
    // def fillsViewportHeight_=(b: Boolean) = peer.setFillsViewportHeight(b)

    object selection extends Publisher:
        // TODO: could be a sorted set
        protected abstract class SelectionSet[A](a: => scala.collection.Seq[A]) extends SetWrapper[A]:
            // def -=(n: A): this.type
            // def +=(n: A): this.type

            def contains(n: A): Boolean = a.contains(n)

            override def size: Int = a.length

            def iterator: Iterator[A] = a.iterator
        end SelectionSet

        object rows extends SelectionSet(peer.getSelectedRows):
            override def subtractOne(n: Int): this.type =
                peer.removeRowSelectionInterval(n, n); this
            override def addOne(n: Int): this.type =
                peer.addRowSelectionInterval(n, n); this

            override def clear(): Unit =
                val n = peer.getRowCount
                if n > 0 then peer.removeRowSelectionInterval(0, n - 1)

            def leadIndex: Int   = peer.getSelectionModel.getLeadSelectionIndex
            def anchorIndex: Int = peer.getSelectionModel.getAnchorSelectionIndex
        end rows

        object columns extends SelectionSet(peer.getSelectedColumns):
            override def subtractOne(n: Int): this.type =
                peer.removeColumnSelectionInterval(n, n); this
            override def addOne(n: Int): this.type =
                peer.addColumnSelectionInterval(n, n); this

            override def clear(): Unit =
                val n = peer.getColumnCount
                if n > 0 then peer.removeColumnSelectionInterval(0, n - 1)

            def leadIndex: Int   = peer.getColumnModel.getSelectionModel.getLeadSelectionIndex
            def anchorIndex: Int = peer.getColumnModel.getSelectionModel.getAnchorSelectionIndex
        end columns

        def cells: mutable.Set[(Int, Int)] =
            new SelectionSet[(Int, Int)]((for (r <- selection.rows; c <- selection.columns) yield (r, c)).toSeq):
                outer =>
                override def subtractOne(n: (Int, Int)): this.type =
                    peer.removeRowSelectionInterval(n._1, n._1)
                    peer.removeColumnSelectionInterval(n._2, n._2)
                    this
                end subtractOne

                override def addOne(n: (Int, Int)): this.type =
                    peer.addRowSelectionInterval(n._1, n._1)
                    peer.addColumnSelectionInterval(n._2, n._2)
                    this
                end addOne

                override def clear(): Unit = peer.clearSelection()

                override def size: Int = peer.getSelectedRowCount * peer.getSelectedColumnCount

        /**
     * From the JTable Swing tutorial:
     * You can specify selection by cell in multiple interval selection mode,
     * but the result is a table that does not produce useful selections.
     */
        def intervalMode: IntervalMode.Value            = IntervalMode(peer.getSelectionModel.getSelectionMode)
        def intervalMode_=(m: IntervalMode.Value): Unit = peer.setSelectionMode(m.id)

        def elementMode: ElementMode.Value =
            if peer.getColumnSelectionAllowed && peer.getRowSelectionAllowed then ElementMode.Cell
            else if peer.getColumnSelectionAllowed then ElementMode.Column
            else if peer.getRowSelectionAllowed then ElementMode.Row
            else ElementMode.None

        def elementMode_=(m: ElementMode.Value): Unit =
            m match
                case ElementMode.Cell   => peer.setCellSelectionEnabled(true)
                case ElementMode.Column => peer.setRowSelectionAllowed(false); peer.setColumnSelectionAllowed(true)
                case ElementMode.Row    => peer.setRowSelectionAllowed(true); peer.setColumnSelectionAllowed(false)
                case ElementMode.None   => peer.setRowSelectionAllowed(false); peer.setColumnSelectionAllowed(false)

        peer.getColumnModel.getSelectionModel.addListSelectionListener(new ListSelectionListener:
                def valueChanged(e: javax.swing.event.ListSelectionEvent): Unit =
                    publish(TableColumnsSelected(Table.this, e.getFirstIndex to e.getLastIndex, e.getValueIsAdjusting))
        )
        peer.getSelectionModel.addListSelectionListener(new ListSelectionListener:
                def valueChanged(e: javax.swing.event.ListSelectionEvent): Unit =
                    publish(TableRowsSelected(Table.this, e.getFirstIndex to e.getLastIndex, e.getValueIsAdjusting))
        )
    end selection

    /**
   * Supplies a renderer component for a given cell.
   */
    protected def rendererComponent(isSelected: Boolean, focused: Boolean, row: Int, column: Int): Component =
        new Component:
            override lazy val peer: JComponent =
                val v = apply(row, column).asInstanceOf[AnyRef]
                if v != null then
                    Table.this.peer.getDefaultRenderer(v.getClass).getTableCellRendererComponent(Table.this.peer,
                        v, isSelected, focused, row, column).asInstanceOf[JComponent]
                else
                    Table.this.peer.getDefaultRenderer(classOf[Object]).getTableCellRendererComponent(Table.this.peer,
                        v, isSelected, focused, row, column).asInstanceOf[JComponent]
                end if
            end peer

    // TODO: a public API for setting editors
    protected def editor(row: Int, column: Int): TableCellEditor =
        val v             = apply(row, column).asInstanceOf[AnyRef]
        val clz: Class[?] = if v != null then v.getClass else classOf[Object]
        Table.this.peer.getDefaultEditor(clz)
    end editor

    /** Gets the current value of the given cell.
    * The given cell coordinates are in view coordinates and thus not
    * necessarily the same as for the model.
    *
    * If you have model coordinates, use `model.getValueAt` instead.
    */
    def apply(row: Int, column: Int): Any =
        val mRow = viewToModelRow(row)
        val mCol = viewToModelColumn(column)
        model.getValueAt(mRow, mCol)
    end apply

    def viewToModelRow(idx: Int): Int = peer.convertRowIndexToModel(idx)
    def modelToViewRow(idx: Int): Int = peer.convertRowIndexToView(idx)

    def viewToModelColumn(idx: Int): Int = peer.convertColumnIndexToModel(idx)
    def modelToViewColumn(idx: Int): Int = peer.convertColumnIndexToView(idx)

    def autoCreateRowSorter: Boolean                = peer.getAutoCreateRowSorter
    def autoCreateRowSorter_=(value: Boolean): Unit = peer.setAutoCreateRowSorter(value)

    def updateSelectionOnSort: Boolean                = peer.getUpdateSelectionOnSort
    def updateSelectionOnSort_=(value: Boolean): Unit = peer.setUpdateSelectionOnSort(value)

    /** Programmatically sets the sorted column of the table view. */
    def sort(column: Int, ascending: Boolean = true): Unit =
        val sorter = peer.getRowSorter
        if sorter != null then
            val list = new java.util.ArrayList[RowSorter.SortKey](1)
            list.add(new RowSorter.SortKey(column, if ascending then SortOrder.ASCENDING else SortOrder.DESCENDING))
            sorter.setSortKeys(list)
        end if
    end sort

    /** Changes the value of the given cell.
    * The given cell coordinates are in view coordinates and thus not
    * necessarily the same as for the model.
    *
    * If you have model coordinates, use `model.setValueAt` instead.
    */
    def update(row: Int, column: Int, value: Any): Unit =
        val mRow = viewToModelRow(row)
        val mCol = viewToModelColumn(column)
        model.setValueAt(value, mRow, mCol)
    end update

    /**
   * Visually updates the given cell.
   */
    def updateCell(row: Int, column: Int): Unit = update(row, column, apply(row, column))

    def selectionForeground: Color            = peer.getSelectionForeground
    def selectionForeground_=(c: Color): Unit = peer.setSelectionForeground(c)
    def selectionBackground: Color            = peer.getSelectionBackground
    def selectionBackground_=(c: Color): Unit = peer.setSelectionBackground(c)

    protected val modelListener: TableModelListener = new TableModelListener:
        def tableChanged(e: TableModelEvent): Unit = publish(
            e.getType match
                case TableModelEvent.UPDATE =>
                    if e.getFirstRow == 0 && e.getLastRow == Int.MaxValue && e.getColumn == TableModelEvent.ALL_COLUMNS
                    then
                        TableChanged(Table.this)
                    else if e.getFirstRow == TableModelEvent.HEADER_ROW then
                        TableStructureChanged(Table.this)
                    else
                        TableUpdated(Table.this, e.getFirstRow to e.getLastRow, e.getColumn)
                case TableModelEvent.INSERT =>
                    TableRowsAdded(Table.this, e.getFirstRow to e.getLastRow)
                case TableModelEvent.DELETE =>
                    TableRowsRemoved(Table.this, e.getFirstRow to e.getLastRow)
        )
end Table
