object MainObjet {
  def main(args: Array[String]) = {
    import java.time.Year

    val client = new EdgarClient

    client.getQuaterlyIndex(Year of 2016, 3)
      .map(record => record.formType)
      .foldLeft(Map.empty[String, Int])((count, form) => count + (form -> (1 + count.getOrElse (form, 0))))
      .toArray
      .sortBy {case (formType, frequency) => frequency} // sort by only frequency
      .reverse // sort high to low, instead of low to high
      .slice(0, 12) // get top 12 most frequent
      .foreach {case (formType, frequency) => println(frequency + " " + formType)}
  }
}
