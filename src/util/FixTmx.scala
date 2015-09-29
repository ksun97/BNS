package util

import java.io.{File, PrintWriter}
import java.util.Scanner

import main.Main

object FixTmx {
  def main(args: Array[String]) {
    val files = Array(new File("res/seasons/spring.tmx"), new File("res/seasons/summer.tmx"), new File("res/seasons/fall.tmx"), new File("res/seasons/winter.tmx"), new File("res/seasons/shop.tmx"))
    for (file <- files) {
      val scan = new Scanner(file)

      var finLine = ""
      while (scan.hasNextLine) {
        val line = scan.nextLine
        if(line.contains("<objectgroup") && !line.contains("width=\"0\" height=\"0\"")) {
          finLine += line.replace(">", " width=\"0\" height=\"0\">") + "\n"
        } else finLine += line + "\n"
      }
      scan.close()
      val wr = new PrintWriter(file)
      wr.println(finLine.trim)
      wr.close()
    }
    Main.main(args)
  }
}


