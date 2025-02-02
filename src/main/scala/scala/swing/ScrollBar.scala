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

import javax.swing.JScrollBar

object ScrollBar:
    def wrap(c: JScrollBar): ScrollBar =
        val w = UIElement.cachedWrapper[ScrollBar](c)
        if w != null then w
        else
            new ScrollBar:
                override lazy val peer: JScrollBar = c
        end if
    end wrap
end ScrollBar

class ScrollBar extends Component with Orientable with Adjustable.Wrapper:
    override lazy val peer: JScrollBar = new JScrollBar with SuperMixin

    def valueIsAdjusting: Boolean            = peer.getValueIsAdjusting
    def valueIsAdjusting_=(b: Boolean): Unit = peer.setValueIsAdjusting(b)

    def orientation: Orientation.Value            = Orientation(peer.getOrientation())
    def orientation_=(o: Orientation.Value): Unit = peer.setOrientation(o.id)

    // TODO: can we find a better interface?
    // def setValues(value: Int = this.value, visible: Int = visibleAmount,
    //             min: Int = minimum, max: Int = maximum) =
    //  peer.setValues(value, visible, min, max)

// Not currently needed, requires wrapper for BoundedRangeModel
//
//    	    def model = peer.getModel
//    	    def model_=(m : BoundedRangeModel) = peer.setModel(m)
end ScrollBar
