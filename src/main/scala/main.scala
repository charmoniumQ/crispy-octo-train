import java.time.YearMonth

object MainObjet {
  def main(args: Array[String]) = {
    println ("Started running")
    val x = new EdgarClient
    println ((x getMonthlyXBRL (YearMonth of (2016, 7))) \ "channel" \ "item")
  }
}
