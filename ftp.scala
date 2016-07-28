def edgar_get(path: String) = {
	import org.apache.commons.net.ftp.FTPClient
	import org.apache.commons.net.ftp.FTPReply
	import org.apache.commons.io.IOUtils
	
	val ftpClient = new FTPClient
	ftpClient connect "ftp.sec.gov"
	// FTPReply.isPositiveCompletion(ftpClient.getReplyCode)
	ftpClient.login("anonymous", "anonymous")
	ftpClient.enterLocalPassiveMode

	// TODO: error handling if file doesn't exist
	val output = IOUtils.toString(ftpClient retrieveFileStream path, "UTF-8")

	ftpClient.logout
	// TODO: execute this in 'finally' block  of try
	ftpClient.disconnect

	output
}

// https://michid.wordpress.com/2009/02/23/function_mem/
class Memoize1[-T, +R](f: T => R) extends (T => R) {
	import scala.collection.mutable
	private[this] val vals = mutable.Map.empty[T, R]
 
	def apply(x: T): R = {
		if (vals.contains(x)){ 
			vals(x)
		} else {
			val y = f(x)
			vals += ((x, y))
			y
		}
	}
}
object Memoize1 {
	def apply[T, R](f: T => R) = new Memoize1(f)
}

// TODO: use joda dates here
def get_monthly_xbrl_(year: Int, month: Int) = {
	import scala.xml.XML
	// TODO: catch case where date is outside of range for recorded data
	XML.loadString(edgar_get(f"/edgar/monthly/xbrlrss-$year-$month%02d.xml"))
}
//val get_monthly_xbrl = Memoize1(get_monthly_xbrl_)

get_monthly_xbrl_(2016, 7) \ "channel" \ "item"
// http://alvinalexander.com/scala/xml-parsing-xpath-extract-xml-tag-attributes
