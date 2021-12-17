package hackerRank

import scala.annotation.tailrec

object ListExercises extends App {
  
  /**
  * Easy problems
  */

  // get element at a given index from a list
  def getNElement(arr: List[Int], n: Int): Int = {

    @tailrec
    def findElementTailRec(remainingArr: List[Int], currentIndex: Int): Int = {
      if (currentIndex == n) remainingArr.head
      else findElementTailRec(remainingArr.tail, currentIndex + 1)
    }

    findElementTailRec(arr, 0)
  }

  /**
  * Size of a list in scala using tailrec
  */

  def listSize(l: List[Int]): Int = {

    @tailrec
    def listSizeTailRec(rList: List[Int], size: Int): Int ={
        if (rList.isEmpty) size
        else listSizeTailRec(rList.tail, size + 1)
    }

    listSizeTailRec(l, 0)
  }

  // Testing
  // println(listSize(List(1,2,3,4,5)))

  /*
  * Reverse list into a new list.
  */ 
  def f4(arr:List[Int]):List[Int] = {
    
    def reverse(tmpList: List[Int], accList: List[Int]): List[Int] = {
      if (tmpList.size  == 0) accList
      else reverse(tmpList.tail, tmpList.head :: accList)
    }

    reverse(arr, List.empty)
  }

}
