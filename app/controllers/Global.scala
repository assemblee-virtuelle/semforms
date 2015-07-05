import java.net.URLDecoder
import java.net.URLEncoder
import scala.xml.Elem
import org.apache.log4j.Logger
import deductions.runtime.html.TableView
import deductions.runtime.jena.RDFStoreObject
import deductions.runtime.services.BrowsableGraph
import deductions.runtime.services.FormSaver
import deductions.runtime.services.StringSearchSPARQL
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.mvc.Controller
import play.api.mvc.Request
import deductions.runtime.html.CreationForm
import scala.concurrent.Future
import play.api.libs.iteratee.Enumerator
import org.w3.banana.io.RDFWriter
import org.w3.banana.jena.Jena
import org.w3.banana.io.Turtle
import deductions.runtime.uri_classify.SemanticURIGuesser
import org.w3.banana.RDFOpsModule
import scala.util.Try
import org.w3.banana.TurtleWriterModule
import play.api.libs.iteratee.Enumeratee
import play.api.libs.iteratee.Iteratee
import deductions.runtime.html.TableViewModule
import org.apache.log4j.Logger
import deductions.runtime.abstract_syntax.InstanceLabelsInference2
import com.hp.hpl.jena.query.Dataset
import org.w3.banana.RDF
import deductions.runtime.sparql_cache.RDFCacheAlgo
import deductions.runtime.dataset.RDFStoreLocalProvider
import deductions.runtime.jena.JenaHelpers
import deductions.runtime.jena.RDFStoreLocalJena1Provider
import deductions.runtime.html.CreationFormAlgo
import org.w3.banana.jena.JenaModule
import org.w3.banana.SparqlOpsModule
import scala.util.Success
import scala.util.Failure
import deductions.runtime.services.ReverseLinksSearchSPARQL
import deductions.runtime.services.ExtendedSearchSPARQL
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.i18n._

/** NOTE: was obliged to rename global to global1
 *  because of Scala compiler bug:
 *  https://issues.scala-lang.org/browse/SI-9346 */
package global1 {

  /** NOTE: important that JenaModule is first; otherwise ops may be null */
  object Global extends JenaModule
  with AbstractApplication[Jena, Dataset]
  with JenaHelpers
  with RDFStoreLocalJena1Provider
    
  trait AbstractApplication[Rdf <: RDF, DATASET] extends Controller
      with RDFOpsModule
      with SparqlOpsModule 
      with RDFCacheAlgo[Rdf, DATASET]
      with TurtleWriterModule
      with TableViewModule[Rdf, DATASET]
      with StringSearchSPARQL[Rdf, DATASET]
      with ReverseLinksSearchSPARQL[Rdf, DATASET]
      with ExtendedSearchSPARQL[Rdf, DATASET]
      with InstanceLabelsInference2[Rdf] 
      with RDFStoreLocalProvider[Rdf, DATASET]
  with BrowsableGraph[Rdf, DATASET]
  with FormSaver[Rdf, DATASET]
  with CreationFormAlgo[Rdf, DATASET]
  with controllers.LanguageManagement
{
    
    Logger.getRootLogger().info(s"in Global")
    
    var form: Elem = <p>initial value</p>
    lazy val tableView = this
    lazy val search = this
    
    lazy val dl = this
    lazy val fs = this
    lazy val cf = this

    // TODO use inverse Play's URI API
    val hrefDisplayPrefix = "/display?displayuri="
    val hrefDownloadPrefix = "/download?url="
    val hrefEditPrefix ="/edit?url="


    /** TODO move some formatting to views or separate function */
    def htmlForm(uri0: String, blankNode: String = "",
      editable: Boolean = false,
      lang: String = "en"): Elem = {
      Logger.getRootLogger().info(s"""Global.htmlForm uri $uri0 blankNode "$blankNode" lang=$lang """)
      val uri = uri0.trim()

      <div class="container">
        <div class="container">
          <div class="row">
            <h3>
              { Messages("Properties_for")(Lang(lang)) }  
              <b>
                <a href={ hrefEditPrefix + URLEncoder.encode(uri, "utf-8") } title="edit this URI">
                { labelForURI(uri) }</a>
                , URI :
                <a href={ hrefDisplayPrefix + URLEncoder.encode(uri, "utf-8") } title="display this URI">{uri}</a>
              </b>
            </h3>
          </div>
          <div class="row">
            <div class="col-md-6">
              <a href={ uri } title="Download from original URI">Download from original URI</a>
            </div>
            <div class="col-md-6">
              <a href={ hrefDownloadPrefix + URLEncoder.encode(uri, "utf-8") }
                 title="Download Turtle from database (augmented by users' edits)">Triples</a>
            </div>
          </div>
        </div>
        {
          if (uri != null && uri != "")
            try {
              tableView.htmlFormElem(uri, hrefDisplayPrefix, blankNode, editable = editable,
                lang = lang)
            } catch {
              case e: Exception => // e.g. org.apache.jena.riot.RiotException
                <p style="color:red">
                  {
                    e.getLocalizedMessage() + " " + printTrace(e)
                  }<br/>
                  Cause:{ if (e.getCause() != null) e.getCause().getLocalizedMessage() }
                </p>
            }
          else
            <div class="row">Enter an URI</div>
        }
      </div>
    }

    def labelForURI(uri: String): String = {
      rdfStore.r(dataset, {
        implicit val graph: Rdf#Graph = allNamedGraph;
        instanceLabel(ops.URI(uri))
      }).getOrElse(uri)
    }
    
//    def displayURI2(uriSubject: String) //  : Enumerator[scala.xml.Elem] 
//    = {
//      import ops._
//      val graphFuture = RDFStoreObject.allNamedGraphsFuture
//      import scala.concurrent.ExecutionContext.Implicits.global
//
//      type URIPair = (Rdf#Node, SemanticURIGuesser.SemanticURIType)
//      val semanticURItypesFuture = tableView.getSemanticURItypes(uriSubject)
//      // TODO get rid of mutable, but did not found out with yield
//      val elems: Future[Iterator[Elem]] = semanticURItypesFuture map {
//        semanticURItypes =>
//          {
//            semanticURItypes.
//              filter { p => isURI(p._1) }.
//              map {
//                semanticURItype =>
//                  val uri = semanticURItype._1
//                  val semanticType = semanticURItype._2
//                  <p>
//                    <div>{ uri }</div>
//                    <div>{ semanticType }</div>
//                  </p>
//              }
//          }
//      }
//      //    def makeEnumerator[E, A]( f: Future[Iterator[A]] ) : Enumerator[A] = new Enumerator[A] {
//      //      def apply[A]( i : Iteratee[A, Iterator[A]]): Future[Iteratee[A, Iterator[A]]]
//      //      = {
//      //        Future(i) // ?????
//      //      }
//      //    }
//      //    val enum = makeEnumerator(elems) // [ , ]
//      elems
//    }

    def printTrace(e: Exception): String = {
      var s = ""
      for (i <- e.getStackTrace()) { s = s + " " + i }
      s
    }

    def wordsearchFuture(q: String = ""): Future[Elem] = {
      val fut = 
        searchString(q, hrefDisplayPrefix)
      wrapSearchResults(fut, q)
    }

    def downloadAsString(url: String): String = {
      println("download url " + url)
      val res = dl.focusOnURI(url)
      println("download result " + res)
      res
    }

    def download(url: String): Enumerator[Array[Byte]] = {
      // cf https://www.playframework.com/documentation/2.3.x/ScalaStream
      // and http://greweb.me/2012/11/play-framework-enumerator-outputstream/
      Enumerator.outputStream { os =>
//        val dl = new BrowsableGraph[Rdf, DATASET]{}
        val graph = search_only(url)
        graph.map { graph =>
          /* non blocking */
          val writer: RDFWriter[Rdf, Try, Turtle] = turtleWriter
          val ret = writer.write(graph.asInstanceOf[Rdf#Graph], os, base = url)
          os.close()
        }
      }
    }

    def edit(url: String): Elem = {
      htmlForm(url, editable = true)
    }

    def save(request: Request[_]): Elem = {
      val body = request.body
      body match {
        case form: AnyContentAsFormUrlEncoded =>
          val map = form.data
          println("Global.save: " + body.getClass + ", map " + map)
          try {
            fs.saveTriples(map)
          } catch {
            case t: Throwable => println("Exception in saveTriples: " + t)
            // TODO show Exception to user
          }
          val uriOption = map.getOrElse("uri", Seq()).headOption
          println("Global.save: uriOption " + uriOption)
          uriOption match {
            case Some(url1) => htmlForm(
              URLDecoder.decode(url1, "utf-8"),
              editable = false,
              lang = chooseLanguage(request) )
            case _ => <p>Save: not normal: { uriOption }</p>
          }
        case _ => <p>Save: not normal: { form.getClass() }</p>
      }
    }

    def createElem2(uri0: String, lang: String = "en"): Elem = {
      Logger.getRootLogger().info("Global.htmlForm uri " + uri0)
      val uri = uri0.trim()

      <div class="container">
        <h3>{ Messages("Creating_instance")(Lang(lang)) }  
        <strong title={uri}>{ labelForURI(uri) }</strong></h3>
        { cf.create(uri, lang).get }
      </div>
    }

    def sparql(query: String, lang: String = "en"): Elem = {
      Logger.getRootLogger().info("Global.sparql query  " + query)
      <p>
        SPARQL query:<br/>{ query }
        <br/>
<pre>
        { try {
dl.sparqlConstructQuery(query)
        } catch {
          case t: Throwable => t.printStackTrace() // TODO: handle error
        }
          /* TODO Future !!!!!!!!!!!!!!!!!!! */
        }
</pre>
      </p>
    }

    def select(query: String, lang: String = "en"): Elem = {
    		Logger.getRootLogger().info("Global.sparql query  " + query)
    		<p>
    		SPARQL query:<pre>{ query }</pre>
    		<br></br>
        <script type="text/css">
table {{
 border-collapse:collapse;
 width:90%;
 }}
th, td {{
 border:1px solid black;
 width:20%;
 }}
td {{
 text-align:center;
 }}
caption {{
 font-weight:bold
 }}
    		</script>
    		<table>
    		{
    			val rowsTry = dl.sparqlSelectQuery(query)
    					rowsTry match {
    					case Success(rows) =>
    					val printedRows = for (row <- rows) yield {
    						<tr>
    						{ for (cell <- row) yield <td> {cell} </td> }
                </tr>
              }
    					printedRows
    					case Failure(e) => e.toString()
    			}
    		}
    		</table>
    		</p>
    }
    
    def backlinksFuture(q: String = ""): Future[Elem] = {
      val fut = 
        backlinks(q, hrefDisplayPrefix)
      wrapSearchResults(fut, q)
    }
  
  def wrapSearchResults( fut: Future[Elem], q: String ): Future[Elem] =
		  fut.map { v =>
        <p> Searched for "{ q }" :<br/>
          { v }
        </p>
  }
  
  def esearchFuture(q:String = ""): Future[Elem] = {
		 val fut = 
       extendedSearch(q)
		 wrapSearchResults( fut, q )
  }
    
//    def isURI(node: Rdf#Node) = ops.foldNode(node)(identity, x => None, x => None) != None

  }

}