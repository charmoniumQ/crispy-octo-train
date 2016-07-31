// TODO: this
// class CIK(code: Int) extends BigInt {
//   // all CIKs I have seen so far are less than 7 digits
//   // Int can 9-digit numbers, adjust the datatype if this is not enough
//   def this(codeStr: String) = this(codeStr.toInt)
// }


class Record {
  
}

class EdgarClient {

  // TODO: parallelize this
  import java.net.URI
  def getAll(uris: Iterable[URI]): Iterable[String] = {
    import org.apache.commons.net.ftp.FTPClient
    import org.apache.commons.net.ftp.FTPReply
    import org.apache.commons.io.IOUtils
    import java.nio.charset.Charset

    // https://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTPClient.html
    val ftpClient = new FTPClient
    try {
      ftpClient.connect("ftp.sec.gov")
      if (! (FTPReply isPositiveCompletion ftpClient.getReplyCode)) {
        throw new java.io.IOException("Unable to connect")
      }
      ftpClient.login("anonymous", "anonymous")
      ftpClient.enterLocalPassiveMode

      val output = uris.map(uri => {
        val stream = ftpClient retrieveFileStream uri.getPath
        if (stream == null) {
          throw new java.io.IOException("Edgar file not found")
        } else {
          IOUtils toString (stream, Charset forName "windows-1252")
        }
      })

      ftpClient.logout

      output
    } finally {
      if (ftpClient.isConnected) {
        ftpClient.disconnect
      }
    }
  }

  import scala.xml.Elem
  import java.time.YearMonth
  def getMonthlyXBRL(date: YearMonth): Elem = {
    import scala.xml.XML

    val year = date.getYear
    val month = date.getMonth.getValue
    val path = f"/edgar/monthly/xbrlrss-$year-$month%02d.xml"

    val documentString = getAll(Seq(new URI(path))).head
    // TODO: stream document to XML loader

    XML loadString (documentString)
  }

  import java.time.LocalDate
  class IndexRecord(
    val CIK: Int,
    val companyName: String,
    val formType: String,
    val dateSubmitted: LocalDate,
    val path: URI) { }

  def parseIndexFile(document: String): Array[IndexRecord] = {
    val boundary = "--------------------------------------------------------------------------------\n"
    val documentBody = document.substring(document.indexOf(boundary) + boundary.length)
    // TODO: parallelize this
    documentBody
      .split("\n")
      .map (line => line.split("\\|"))
      .map (lineArray =>
        new IndexRecord(
          lineArray(0).toInt,
          lineArray(1),
          lineArray(2),
          LocalDate.parse(lineArray(3)),
          new URI(lineArray(4))
        )
      )
  }

  // TODO: download the compressed version of this
  import java.time.Year
  def getQuaterlyIndex(year: Year, quarter: Int): Array[IndexRecord] = {
    import org.apache.commons.io.IOUtils

    val yearVal = year.getValue()
    val path = f"/edgar/full-index/$yearVal/QTR$quarter/master.idx"
    // TODO: implement this as streaming data
    val document = getAll(Seq(new URI(path))).head
    parseIndexFile(document)
  }

}
