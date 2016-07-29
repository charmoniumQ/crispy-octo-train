class EdgarClient {

  def getAll_(paths: Iterable[String]): Iterable[String] = {
    import org.apache.commons.net.ftp.FTPClient
    import org.apache.commons.net.ftp.FTPReply
    import org.apache.commons.io.IOUtils

    // https://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTPClient.html
    val ftpClient = new FTPClient
    try {
      ftpClient connect "ftp.sec.gov"
      if (! (FTPReply isPositiveCompletion ftpClient.getReplyCode)) {
        throw new java.io.IOException("Unable to connect")
      }
      ftpClient login ("anonymous", "anonymous")
      ftpClient.enterLocalPassiveMode

      val output = paths map { path =>
        val stream = ftpClient retrieveFileStream path
        if (stream == null) {
          throw new java.io.IOException("Edgar file not found")
        }
        IOUtils toString (stream, "UTF-8")
      }

      ftpClient.logout

      output
    } finally {
      if (ftpClient.isConnected) {
        ftpClient.disconnect
      }
    }
  }

  import scala.collection.mutable
  private val cache = mutable.Map.empty[String, String]
  def getAll(paths: Iterable[String]): Iterable[String] = {
    val newPaths = paths filterNot cache.contains
    
    (newPaths zip getAll_(newPaths)) foreach { pair =>
      cache put (pair._1, pair._2)
    }

    // now that *all* of the paths are in the cache
    paths map cache
  }

  import scala.xml.Elem
  import java.time.YearMonth
  def getMonthlyXBRL(date: YearMonth): Elem = {
    import scala.xml.XML
    val year = date.getYear
    val month = date.getMonth.getValue

    val document = getAll(Seq(f"/edgar/monthly/xbrlrss-$year-$month%02d.xml")).head
    XML loadString document
  }
}
// http://alvinalexander.com/scala/xml-parsing-xpath-extract-xml-tag-attributes
