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

package scala.swing.examples

import java.awt.Insets

import scala.swing.GridBagPanel.*
import scala.swing.*

object GridBagDemo extends SimpleSwingApplication:
    lazy val ui: GridBagPanel = new GridBagPanel:
        val c          = new Constraints
        val shouldFill = true
        if shouldFill then
            c.fill = Fill.Horizontal

        val button1 = new Button("Button 1")

        c.weightx = 0.5

        c.fill = Fill.Horizontal
        c.gridx = 0
        c.gridy = 0
        layout(button1) = c

        val button2 = new Button("Button 2")
        c.fill = Fill.Horizontal
        c.weightx = 0.5
        c.gridx = 1
        c.gridy = 0
        layout(button2) = c

        val button3 = new Button("Button 3")
        c.fill = Fill.Horizontal
        c.weightx = 0.5
        c.gridx = 2
        c.gridy = 0
        layout(button3) = c

        val button4 = new Button("Long-Named Button 4")
        c.fill = Fill.Horizontal
        c.ipady = 40 // make this component tall
        c.weightx = 0.0
        c.gridwidth = 3
        c.gridx = 0
        c.gridy = 1
        layout(button4) = c

        val button5 = new Button("5")
        c.fill = Fill.Horizontal
        c.ipady = 0     // reset to default
        c.weighty = 1.0 // request any extra vertical space
        c.anchor = Anchor.PageEnd
        c.insets = new Insets(10, 0, 0, 0) // top padding
        c.gridx = 1                        // aligned with button 2
        c.gridwidth = 2                    // 2 columns wide
        c.gridy = 2                        // third row
        layout(button5) = c

    def top: Frame = new MainFrame:
        title = "GridBag Demo"
        contents = ui
end GridBagDemo
