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

import scala.collection.{Iterator, mutable}

/**
 * Default partial implementation for buffer adapters.
 */
abstract class BufferWrapper[A] extends mutable.Buffer[A]:
    type MoreElem[+B] = IterableOnce[B]

    override def clear(): Unit = for _ <- 0 until length do remove(0)

    override def update(n: Int, a: A): Unit =
        remove(n)
        insert(n, a)

    override def iterator: Iterator[A] = Iterator.range(0, length).map(apply)

    override def prepend(elem: A): this.type =
        insert(0, elem); this

    override def insertAll(idx: Int, elems: MoreElem[A]): Unit =
        var i = idx
        for el <- elems.iterator do
            insert(i, el)
            i += 1
    end insertAll

    override def remove(idx: Int, count: Int): Unit =
        require(count >= 0)
        var n = 0
        while n < count do
            remove(idx + n)
            n += 1
    end remove

    override def patchInPlace(from: Int, patch: MoreElem[A], replaced: Int): this.type =
        if replaced > 0 then
            remove(from, replaced)
        insertAll(from, patch)
        this
    end patchInPlace
end BufferWrapper
