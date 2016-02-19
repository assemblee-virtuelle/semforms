package views

import deductions.runtime.utils.I18NMessages
import scala.xml.NodeSeq
import deductions.runtime.views.ToolsPage
import scala.xml.NodeSeq.seqToNodeSeq
import deductions.runtime.html.EnterButtons
import controllers._
import deductions.runtime.services.Configuration
import scala.xml.Text

trait MainXmlWithHead extends deductions.runtime.html.MainXml
with Configuration {

  /** TODO really need to override ?
   *  question is: do we need here more than the generic semantic_forms app ? */
  override def head(title: String = "")(implicit lang: String = "en"): NodeSeq = {
    /** TODO host bootstrap locally <<<<< */
    val bootstrap = "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1"
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
  <title>{ val default = message("Welcome") ; if( title != "") title +" - "+default else default }</title>
	<link rel="shortcut icon" type="image/png" href="/assets/images/favicon.png"></link>
	<link rel="stylesheet" href={ routes.Assets.at("stylesheets/main.css").url }></link>
  <link rel="shortcut icon" type="image/png" href={ routes.Assets.at("images/favicon.ico").url }/>

	<script src={ routes.Assets.at("javascripts/jquery-1.11.2.min.js").url } type="text/javascript"></script>

  <!-- bootstrap -->
  <link rel="stylesheet" href={ routes.Assets.at("stylesheets/bootstrap.min.css").url } />
  <link rel="stylesheet" href={ routes.Assets.at("stylesheets/bootstrap-theme.min.css").url } />
  <script src={ routes.Assets.at("javascripts/bootstrap.min.js").url } type="text/javascript"></script>

	<script src={ routes.Assets.at("javascripts/wikipedia.js").url } type="text/javascript"></script>
	<script src={ routes.Assets.at("javascripts/formInteractions.js").url } type="text/javascript"></script>

  <!-- RDF Viewer-->
  <link rel="stylesheet" href={ routes.Assets.at("stylesheets/rdfviewer.css").url } />
  <script src={ routes.Assets.at("javascripts/rdf.js").url } type="text/javascript"></script>

	<style type="text/css">
		.resize {{ resize: both; width: 100%; height: 100%; }}
		.overflow {{ overflow: auto; width: 100%; height: 100%; }}
	</style>
</head>
  }

override def mainPageHeader(implicit lang: String) =
	<div class="container">
		<div class="row">
			<h3>Bienvenue à l'Assemblée Virtuelle</h3>
		</div>
		<div class="row"><h3>Bienvenue à l'Assemblée Virtuelle</h3></div>
    <bold>
			<a href="https://github.com/jmvanel/semantic_forms/wiki/Manuel-utilisateur#avertissement-sur-lusage">
           Avertissement !!! A LIRE
			</a>
		</bold>

		<div class="row">
			<div class="col-md-12">
				<form role="form" action="/wordsearch">
					<div class="form-group">
						<label class="col-md-2 control-label" for="q" title="Search URI whose value (object triple) match given regular expression">Rechercher un PAIR</label>
						<div class="col-md-6">
							<input class="form-control" type="text" name="q"
placeholder="Entrer terme de recherche (en fait une expression régulière) qui correspond aux valeurs (objets des triplets)"/>
						</div>
						<div class="col-md-4">
							<input class="btn btn-primary" type="submit" value="Rechercher" un="" pair=""/>
						</div>
						<input type="submit" style="display:none"/>
					</div>
				</form>
			</div>
		</div>

		<div class="row">
			<div class="col-md-12">
				<h4 title="PAIR = Projet, Acteur, Idée, Resource">Créez un PAIR</h4>
				<div class="flex-form"> { Seq(
					creationFormAV(	"Person", "Personne" ),
					creationFormAV( "Organization",	"Organisation" ),
					creationFormAV( "Project", "Projet" ),
					creationFormAV( "Idea",	"Idée" ),
					creationFormAV(	"Resource", "Ressource" )
        )} </div>

				<div>Dans une autre ontologie</div>
				<form role="form" action="/create">
					<div class="col-md-6">
						<!--input class="form-control" type="text" name='uri' placeholder="Coller ou taper l&#x27;URI d&#x27;une classe dans une ontologie."></input-->
						<select class="form-control" type="text" name="uri" list="class_uris">
                { suggestedClassesForCreation }
						</select>
					</div>
					<div class="col-md-4">
						<input class="btn btn-primary" type="submit" value="Créer"/>
					</div>
					<input type="submit" style="display:none"/>
				</form>
			</div>
		</div>
	</div>

  private def creationFormAV(classe: String, label: String): NodeSeq =
    <form role="form" action="/create">
      <input type="hidden" name="uri" id="uri" value={ prefixAVontology + classe }/>
      <input type="submit" name="create" id="create" value={ label }/>
    </form>
}
