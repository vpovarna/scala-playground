package hackerRank

object InputScan {

  def main(args: Array[String]): Unit = {
    val result = scala.io.StdIn.readLine().take(2).map(_.toInt).sum
    println(result)
  }

}
