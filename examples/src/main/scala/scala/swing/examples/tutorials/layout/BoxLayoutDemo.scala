/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package scala.swing.examples.tutorials.layout

import scala.swing.*

/**
 * Tutorials: How to Use BoxLayout
 * [[http://docs.oracle.com/javase/tutorial/uiswing/layout/box.html]]
 * 
 * Source code reference:
 * [[http://docs.oracle.com/javase/tutorial/uiswing/examples/layout/BoxLayoutDemoProject/src/layout/BoxLayoutDemo.java]]
 *
 * BoxLayoutDemo.scala requires no other files.
 */
class BoxLayoutDemo extends BoxPanel(Orientation.Vertical):
    val button1 = new Button("Button 1"):
        xLayoutAlignment = 0.5
    val button2 = new Button("Button 2"):
        xLayoutAlignment = 0.5
    val button3 = new Button("Button 3"):
        xLayoutAlignment = 0.5
    val button4 = new Button("Long-Named Button 4"):
        xLayoutAlignment = 0.5
    val button5 = new Button("5"):
        xLayoutAlignment = 0.5
    contents += button1
    contents += button2
    contents += button3
    contents += button4
    contents += button5
end BoxLayoutDemo

object BoxLayoutDemo extends SimpleSwingApplication:
    // Create and set up the window.
    lazy val top = new MainFrame:
        title = "BoxLayoutDemo"
        contents = new BoxLayoutDemo()
end BoxLayoutDemo
