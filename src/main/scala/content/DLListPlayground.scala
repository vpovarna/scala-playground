package content

import java.util.NoSuchElementException

object DLListPlayground {

  // Complexity
  // O(1) -> accessing
  // O(n) -> everything else

  // Single Linked List impl
  trait MyList[+T] {
    def head: T
    def tail: MyList[T]
    def prepend[S >: T](element: S): MyList[S]
  }

  case object Empty extends MyList[Nothing] {
    // In order to return nothing, you need to throw an exception.
    override def head: Nothing = throw new NoSuchElementException("head of an empty list")
    override def tail: MyList[Nothing] = throw new NoSuchElementException("tail of an empty list")
    override def prepend[S >: Nothing](element: S): MyList[S] = Cons(element, this)
  }

  case class Cons[+T](override val head: T, override val tail: MyList[T]) extends MyList[T] {
    override def prepend[S >: T](element: S): Cons[S] = Cons(element, this)
  }

  // Double linked list
  trait DLList[+T] {
    // pointer
    def value: T
    def prev: DLList[T]
    def next: DLList[T]
    def append[S >: T](element: S): DLList[S]
    def prepend[S >: T](element: S): DLList[S]

    def updatePrev[S >: T](newPrev: => DLList[S]): DLList[S]
    def updateNext[S >: T](newNext: => DLList[S]): DLList[S]
  }

  case object DLEmpty extends DLList[Nothing] {
    override def value: Nothing = throw new NoSuchElementException("No pointer location in an empty double linked list")

    override def prev: DLList[Nothing] = throw new NoSuchElementException("No previous element in an empty double linked list")

    override def next: DLList[Nothing] = throw new NoSuchElementException("No next element in an empty double linked list")

    override def append[S >: Nothing](element: S): DLList[S] = new DLCons(element, DLEmpty, DLEmpty)

    override def prepend[S >: Nothing](element: S): DLList[S] = new DLCons(element, DLEmpty, DLEmpty)

    override def updateNext[S >: Nothing](newNext: => DLList[S]): DLList[S] = this

    override def updatePrev[S >: Nothing](newPrev: => DLList[S]): DLList[S] = this
  }

  class DLCons[+T](override val value: T, p: => DLList[T], n: => DLList[T]) extends DLList[T] {
    // Call by need
    override lazy val prev: DLList[T] = p
    override lazy val next: DLList[T] = n

    override def updatePrev[S >: T](newPrev: => DLList[S]): DLList[S] = {
      lazy val result: DLList[S] = new DLCons[S](value, newPrev, n.updatePrev(result))
      result
    }

    override def updateNext[S >: T](newNext: => DLList[S]): DLList[S] = {
      lazy val result: DLList[S] = new DLCons[S](value, p.updateNext(result), newNext)
      result
    }

    override def append[S >: T](element: S): DLList[S] = {
      lazy val result: DLList[S] = new DLCons[S](value, p.updateNext(result), n.append(element).updatePrev(result))
      result
    }

    override def prepend[S >: T](element: S): DLList[S] = {
      lazy val result: DLList[S] = new DLCons[S](value, p.prepend(element).updateNext(result), n.updatePrev(result))
      result
    }
  }

  def main(args: Array[String]): Unit = {
    val list = DLEmpty.prepend(1).append(2).prepend(3).append(4)
    println(list.value) // 1
    println(list.next.value) // 2
    println(list.next.prev == list) // true
    println(list.prev.value) // 3
    println(list.prev.next == list) // true
    println(list.next.next.value) // 4

  }

}
