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

import scala.collection.mutable
import scala.ref.{Reference, WeakReference}
import scala.swing.event.Event

/** <p>
 *    Notifies registered reactions when an event is published. Publishers are
 *    also reactors and listen to themselves per default as a convenience.
 *  </p>
 *  <p>
 *    In order to reduce memory leaks, reactions are weakly referenced by default,
 *    unless they implement <code>Reactions.StronglyReferenced</code>. That way,
 *    the lifetime of reactions are more easily bound to the registering object,
 *    which are reactors in common client code and hold strong references to their
 *    reactions. As a result, reactors can be garbage collected even though they
 *    still have reactions registered at some publisher, but not vice versa
 *    since reactors (strongly) reference publishers they are interested in.
 *  </p>
 */
trait Publisher extends Reactor:
    import Reactions.*

    protected val listeners: RefSet[Reaction] = new RefSet[Reaction]:
        protected val underlying: mutable.Set[Reference[Reaction]] =
            new mutable.HashSet[Reference[Reaction]]

        protected def Ref(a: Reaction): Ref[Reaction] = a match
            case a: StronglyReferenced => new StrongReference[Reaction](a) with super.Ref[Reaction]
            case _                     => new WeakReference[Reaction](a, referenceQueue) with super.Ref[Reaction]

    private[swing] def subscribe(listener: Reaction): Unit   = listeners += listener
    private[swing] def unsubscribe(listener: Reaction): Unit = listeners -= listener

    /**
   * Notify all registered reactions.
   */
    def publish(e: Event): Unit = for l <- listeners do if l.isDefinedAt(e) then l(e)

    listenTo(this)
end Publisher

/**
 * A publisher that subscribes itself to an underlying event source not before the first
 * reaction is installed. Can unsubscribe itself when the last reaction is uninstalled.
 */
private[swing] trait LazyPublisher extends Publisher:
    import Reactions.*

    protected def onFirstSubscribe(): Unit
    protected def onLastUnsubscribe(): Unit

    override def subscribe(listener: Reaction): Unit =
        if listeners.size == 1 then onFirstSubscribe()
        super.subscribe(listener)

    override def unsubscribe(listener: Reaction): Unit =
        super.unsubscribe(listener)
        if listeners.size == 1 then onLastUnsubscribe()
end LazyPublisher

import scala.ref.*

private[swing] trait SingleRefCollection[A <: AnyRef] extends Iterable[A]:
    self =>

    trait Ref[+B <: AnyRef] extends Reference[B]:
        override def hashCode(): Int = get match
            case Some(x) => x.##
            case _       => 0
        override def equals(that: Any): Boolean = that match
            case that: ReferenceWrapper[?] =>
                val v1 = this.get
                val v2 = that.get
                v1 == v2
            case _ => false
    end Ref

    // type Ref <: Reference[A] // TODO: could use higher kinded types, but currently crashes
    protected[this] def Ref(a: A): Ref[A]
    protected[this] val referenceQueue = new ReferenceQueue[A]

    protected val underlying: Iterable[Reference[A]]

    def purgeReferences(): Unit =
        var ref = referenceQueue.poll
        while ref.isDefined do
            removeReference(ref.get)
            ref = referenceQueue.poll
    end purgeReferences

    protected[this] def removeReference(ref: Reference[A]): Unit

    def iterator: Iterator[A] = new Iterator[A]:
        private val elems          = self.underlying.iterator
        private var hd: A          = _
        private var ahead: Boolean = false
        private def skip(): Unit =
            while !ahead && elems.hasNext do
                // make sure we have a reference to the next element,
                // otherwise it might be garbage collected
                val next = elems.next().get
                ahead = next.isDefined
                if ahead then hd = next.get
        def hasNext: Boolean =
            skip(); ahead
        def next(): A =
            if hasNext then
                ahead = false; hd
            else throw new NoSuchElementException("next on empty iterator")
end SingleRefCollection

private[swing] class StrongReference[+T <: AnyRef](value: T) extends Reference[T]:
    private[this] var ref: Option[T] = Some(value)
    def isValid: Boolean             = ref.isDefined
    def apply(): T                   = ref.get
    def get: Option[T]               = ref
    override def toString: String    = get.map(_.toString).getOrElse("<deleted>")
    def clear(): Unit                = ref = None
    def enqueue(): Boolean           = false
    def isEnqueued: Boolean          = false
end StrongReference

private[swing] abstract class RefSet[A <: AnyRef] extends SetWrapper[A] with SingleRefCollection[A]:
    self =>
    protected val underlying: mutable.Set[Reference[A]]

    override def addOne(el: A): this.type =
        purgeReferences(); underlying += Ref(el); this
    override def subtractOne(el: A): this.type =
        underlying -= Ref(el); purgeReferences(); this

    override def clear(): Unit =
        underlying.clear(); purgeReferences()

    def contains(el: A): Boolean =
        purgeReferences(); underlying.contains(Ref(el))

    override def size: Int =
        purgeReferences(); underlying.size

    protected[this] def removeReference(ref: Reference[A]): Unit = underlying -= ref
end RefSet
