package controllers

import java.net.URLDecoder
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.xml.Elem
import scala.xml.NodeSeq
import deductions.runtime.jena.ApplicationFacadeJena
import deductions.runtime.views.ToolsPage
import play.api.mvc.Accepting
import play.api.mvc.Action
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.mvc.Controller
import play.api.mvc.Request
import views.MainXmlWithHead
import deductions.runtime.services.CORS
import deductions.runtime.services.DefaultConfiguration

/** main controller */
object Application extends Controller
    with ApplicationFacadeJena
    with LanguageManagement
    with Secured
    with MainXmlWithHead
    with CORS
    with DefaultConfiguration {
  
  def index() =
        withUser {
    implicit userid =>
    implicit request => {
      val lang = chooseLanguageObject(request).language
      val userInfo = displayUser(userid, "", "", lang)
      Ok( "<!DOCTYPE html>\n" + mainPage(<p>...</p>, userInfo, lang))
            .as("text/html; charset=utf-8")
    }
  }

  def displayURI(uri: String, blanknode: String = "", Edit: String = "") = {
    Action { implicit request =>
      println("displayURI: " + request)
      println("displayURI: " + Edit)
      val lang = chooseLanguage(request)
      val title = labelForURITransaction(uri, lang)
      outputMainPage(
        htmlForm(uri, blanknode, editable = Edit != "",
        lang), lang, title=title )
    }
  }

  def form(uri: String, blankNode: String = "", Edit: String = "") = {
    Action { implicit request =>
      println("form: " + request + " displayURI: " + Edit)
      val lang = chooseLanguage(request)
      Ok(htmlFormElemJustFields(uri: String, hrefDisplayPrefix, blankNode,
        editable = Edit != "", lang))
    }
  }
    
  /** generate a Main Page wrapping given XHTML content */
  private def outputMainPage( content: NodeSeq,
      lang: String, userInfo: NodeSeq = <div/>, title: String = "" )
  (implicit request: Request[_]) = {
      Ok( "<!DOCTYPE html>\n" +
        mainPage( content, userInfo, lang, title )
      ).withHeaders("Access-Control-Allow-Origin" -> "*") // for dbpedia lookup
      .as("text/html; charset=utf-8")
  }

  def wordsearchAction(q: String = "") = Action.async {
    implicit request =>
    val lang = chooseLanguageObject(request).language
    val fut: Future[Elem] = wordsearch(q, lang)
    fut.map( r => outputMainPage( r, lang ) )
  }

  /** pasted from above */
  def showNamedGraphsAction() = Action.async {
    implicit request =>
    val lang = chooseLanguageObject(request).language
    val fut = showNamedGraphs(lang)
    val rr = fut.map( r => outputMainPage( r, lang ) )
    rr
  }

  def showTriplesInGraphAction( uri: String) = {
        Action.async { implicit request =>
          val lang = chooseLanguageObject(request).language
          val fut = Future.successful( showTriplesInGraph( uri, lang) )
          val rr = fut.map( r => outputMainPage( r, lang ) )
          rr
  }
  }
  
  /////////////////////////////////
  
  def edit(uri: String) =
    withUser {
    implicit userid =>
    implicit request =>
      val lang = chooseLanguageObject(request).language
      val pageURI = uri
      val pageLabel = labelForURI(uri, lang)
      val userInfo = displayUser(userid, pageURI, pageLabel, lang)
      println( s"userInfo $userInfo" )
       val content = htmlForm(
        uri, editable = true,
        lang = chooseLanguage(request))
      Ok( "<!DOCTYPE html>\n" + mainPage( content, userInfo, lang))
            .as("text/html; charset=utf-8").
        withHeaders("Access-Control-Allow-Origin" -> "*") // TODO dbpedia only
  }

  def saveAction() = {
    Action { implicit request =>
      val lang = chooseLanguage(request)
      outputMainPage(save(request), lang)
    }
  }

  def save(request: Request[_]): NodeSeq = {
      val body = request.body
      body match {
        case form: AnyContentAsFormUrlEncoded =>
          val lang = chooseLanguage(request)
          val map = form.data
          println("Global.save: " + body.getClass + ", map " + map)
          try {
            saveForm(map, lang )
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
              lang = lang )
            case _ => <p>Save: not normal: { uriOption }</p>
          }
        case _ => <p>Save: not normal: { getClass() }</p>
      }
  }

  def createAction() = {
    Action { implicit request =>
      println("create: " + request)
      val uri = getFirstNonEmptyInMap(request.queryString, "uri")
      val formSpecURI = getFirstNonEmptyInMap(request.queryString, "formspec")
      println("create: " + uri)
      println("formSpecURI: " + formSpecURI)
      val lang = chooseLanguage(request)
      outputMainPage(
        create(uri, chooseLanguage(request),
          formSpecURI), lang )
    }
  }

//  def download(url: String): Action[_] = {
//    Action { Ok(downloadAsString(url)).as("text/turtle; charset=utf-8") }
//  }

  /** cf https://www.playframework.com/documentation/2.3.x/ScalaStream */
  def downloadAction(url: String) = {
    Action {
      Ok.chunked(download(url)).as("text/turtle; charset=utf-8")
      .withHeaders("Access-Control-Allow-Origin" -> "*")
//        Ok.stream(download(url) >>> Enumerator.eof).as("text/turtle; charset=utf-8")
    }
  }

  def getFirstNonEmptyInMap(map: Map[String, Seq[String]],
                            uri: String): String = {
    val uriArgs = map.getOrElse(uri, Seq())
    uriArgs.find { uri => uri != "" }.getOrElse("")
  }

  def sparql(query: String) = {
    Action { implicit request =>
      println("sparql: " + request)
      println("sparql: " + query)
      val lang = chooseLanguage(request)
      outputMainPage(sparqlConstructQuery(query, lang), lang)
    }
  }

  def select(query: String) = {
    Action { implicit request =>
      println("sparql: " + request)
      println("sparql: " + query)
      val lang = chooseLanguage(request)
      outputMainPage(
        sparqlSelectQuery(query, lang), lang)
    }
  }

  def backlinksAction(q: String = "") = Action.async {
	  implicit request =>
	  val fut: Future[Elem] = backlinks(q)
    val extendedSearchLink = <p>
                               <a href={ "/esearch?q=" + q }>
                                 Extended Search for &lt;{ q }
                                 &gt;
                               </a>
                             </p>
    fut.map { res =>
    val lang = chooseLanguage(request)
    outputMainPage(
        NodeSeq fromSeq Seq(extendedSearchLink, res), lang)
    }
  }

  def extSearch(q: String = "") = Action.async {
	  implicit request =>
	  val lang = chooseLanguage(request)
    val fut = esearch(q)
    fut.map(r =>
    outputMainPage(r, lang))
  }

  def ldp(uri: String) = {
    Action { implicit request =>
      println("LDP GET: request " + request)
      val acceptedTypes = request.acceptedTypes // contentType
      val acceptsTurtle = Accepting("text/turtle")
      val turtle = acceptsTurtle.mimeType
      val accepts = Accepting(acceptedTypes.headOption.getOrElse(turtle).toString())
      val r = ldpGET(uri, accepts.mimeType)
      println("LDP: GET: result " + r)
      val contentType = accepts.mimeType + "; charset=utf-8"
      println( s"contentType $contentType" )
      Ok(r).as(contentType)
      .withHeaders("Access-Control-Allow-Origin" -> "*")
    }
  }

  /** TODO: this is blocking code !!! */
  def ldpPOSTAction(uri: String) = {
    Action { implicit request =>
      println("LDP: " + request)
      val slug = request.headers.get("Slug")
      val link = request.headers.get("Link")
      val contentType = request.contentType
      val content = {
        val asText = request.body.asText
        if (asText != None) asText
        else {
          val raw = request.body.asRaw.get
          println(s"""LDP: raw: "$raw" size ${raw.size}""")
          raw.asBytes(raw.size.toInt).map {
            arr => new String(arr, "UTF-8")
          }
        }
      }
      println(s"LDP: content: $content")
      val serviceCalled =
        ldpPOST(uri, link, contentType, slug, content).getOrElse("default")
      Ok(serviceCalled).as("text/plain; charset=utf-8")
      .withHeaders("Access-Control-Allow-Origin" -> "*")
    }
  }

  def lookupService(search: String) = {
    Action { implicit request =>
      println("Lookup: " + request)
      Ok(lookup(search)).as("text/json-ld; charset=utf-8")
    }
  }

  def httpOptions(path: String) = {
	  Action { implicit request =>
      println("OPTIONS: " + request)
      Ok("OPTIONS: " + request)
        .as("text/html; charset=utf-8")
        .withHeaders(corsHeaders.toList:_*)
    }
  }

  def toolsPage = {
    Action { implicit request =>
      Ok(new ToolsPage with DefaultConfiguration {}.getPage)
        .as("text/html; charset=utf-8")
    }
  }

  def makeHistoryUserActionsAction(userURI: String) =
    Action { implicit request =>
      val lang = chooseLanguage(request)
      outputMainPage(makeHistoryUserActions(userURI, lang), lang)
  }

}
