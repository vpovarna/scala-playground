package hackerRank

import cats.instances.finiteDuration

object Exercises {

  def f(args: Array[String]) = {
    val v: Int = args(0).toInt
    (0 to v - 1).foreach(x => println("Hello World"))
  }

  def f1(num: Int, arr: List[Int]) = {
    arr.flatMap(nr => (0 to num - 1).map(_ => nr))
  }

  def f2(delim: Int, arr: List[Int]): List[Int] = {
    arr.filter(_ < delim)
  }

  def f3(arr: List[Int]): List[Int] = {
    val size: Int = arr.length
    val t: List[Int] = (0 to size - 1).filter(i => i % 2 == 0).map(arr(_)).toList
    t  
  }

  // Reverse List
  def f4(arr:List[Int]):List[Int] = {
    
    def reverse(tmpList: List[Int], accList: List[Int]): List[Int] = {
      if (tmpList.size  == 0) accList
      else reverse(tmpList.tail, tmpList.head :: accList)
    }

    reverse(arr, List.empty)
  }

  // Sum of odd elements
  def sumOdd(arr:List[Int]):Int = {
    arr.filter(elem => elem % 2 != 0).reduce((i, j) => i+j)
  }

  def main(args: Array[String]): Unit = {}


}
