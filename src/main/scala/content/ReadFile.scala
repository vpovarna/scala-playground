package content

import java.io.File
import java.util.Scanner
import org.apache.commons.io.FileUtils
import scala.io.Source

object ReadFile extends App {

  val filePath = "src/main/resources/TestFile.txt"

  // Version 1 -> the Java way

  val file = new File(filePath)
  val scanner = new Scanner(file)

  // while (scanner.hasNextLine) {
  //   val line = scanner.nextLine()
  //   println(line)
  // }

  // Version 2 -> using apache commons.io

  val fileContents = FileUtils.readLines(file)
  // fileContents.forEach(println)

  // Version 3 -> Scala way
  val scalaFileContents: Iterator[String] =
    Source.fromFile(file).getLines() // Iterator is not fully loaded in memory
  // scalaFileContents.foreach(println)

  // Version 4 -> Scala way like a boss

  def open(path: String) = new File(path)
  
  implicit class RichFile(file: File) {
    def read() = Source.fromFile(file).getLines() 
  }

  val readLikeABoss = open(path = filePath).read()
  readLikeABoss.foreach(println)

}
