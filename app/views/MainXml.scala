package views

import deductions.runtime.utils.I18NMessages
import scala.xml.NodeSeq
import deductions.runtime.views.ToolsPage
import scala.xml.NodeSeq.seqToNodeSeq
import deductions.runtime.html.EnterButtons
import controllers._

trait MainXml extends ToolsPage with EnterButtons {

  /** main Page with a single content (typically a form) */
  def mainPage(content: NodeSeq, userInfo: NodeSeq, lang: String = "en") = {
    <html>
      { head(lang) }
      <body>
        {
          Seq(
            userInfo,
            mainPageHeader(lang),
            content,
            linkToToolsPage)
        }
      </body>
    </html>
  }

  def linkToToolsPage = <p>
                          ---<br/>
                          <a href="/tools">Tools</a>
                        </p>

  def head(implicit lang: String = "en"): NodeSeq = {
    val bootstrap = "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1"
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
	<title>Bienvenue à l'Assemblée Virtuelle</title>
	<link rel="shortcut icon" type="image/png" href="/assets/images/favicon.png"></link>
	<link rel="stylesheet" href={ routes.Assets.at("stylesheets/main.css").url }></link>

	<script src={ routes.Assets.at("javascripts/jquery-1.11.2.min.js").url } type="text/javascript"></script>
	<!-- bootstrap -->
      <!-- bootstrap -->
      <link rel="stylesheet" href={ bootstrap + "/css/bootstrap.min.css"}/>
      <link rel="stylesheet" href={ bootstrap + "/css/bootstrap-theme.min.css"}/>
      <script src={ bootstrap + "/js/bootstrap.min.js"}></script>

	<!--link rel="stylesheet" href={ routes.Assets.at("stylesheets/bootstrap.min.css").url }></link>
	<link rel="stylesheet" href={ routes.Assets.at("bootstrap-theme.min.css").url }></link>
	<script src={ routes.Assets.at("javascripts/bootstrap.min.js").url }></script
  -->

	<script src={ routes.Assets.at("javascripts/wikipedia.js").url } type="text/javascript"></script>
	<script src={ routes.Assets.at("javascripts/formInteractions.js").url } type="text/javascript"></script>

	<style type="text/css">
		.resize {{ resize: both; width: 100%; height: 100%; }}
		.overflow {{ overflow: auto; width: 100%; height: 100%; }}
	</style>
</head>
  }

def mainPageHeader(lang: String) =
	<div class="container">
		<div class="row"><h3>Bienvenue à l'Assemblée Virtuelle</h3></div>

		<div class="row">
			<div class="col-md-12">
				<form role="form" action="http://localhost:9000/wordsearch">
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
				<h3>Choisissez un type de concept :</h3>
				<h4>Dans le modèle PAIR</h4>
				<div class="flex-form">
					<form role="form" action="http://localhost:9000/create">
						<input type="hidden" name="uri" id="uri" value="http://www.assemblee-virtuelle.org/ontologies/v1.owl#Person"/>
						<input type="submit" name="create" id="create" value="Personne"/>
					</form>
					<form role="form" action="http://localhost:9000/create">
						<input type="hidden" name="uri" id="uri" value="http://www.assemblee-virtuelle.org/ontologies/v1.owl#Organization"/>
						<input type="submit" name="create" id="create" value="Organisation"/>
					</form>
					<form role="form" action="http://localhost:9000/create">
						<input type="hidden" name="uri" id="uri" value="http://www.assemblee-virtuelle.org/ontologies/v1.owl#Project"/>
						<input type="submit" name="create" id="create" value="Projet"/>
					</form>
					<form role="form" action="http://localhost:9000/create">
						<input type="hidden" name="uri" id="uri" value="http://www.assemblee-virtuelle.org/ontologies/v1.owl#Idea"/>
						<input type="submit" name="create" id="create" value="Idée"/>
					</form>
					<form role="form" action="http://localhost:9000/create">
						<input type="hidden" name="uri" id="uri" value="http://www.assemblee-virtuelle.org/ontologies/v1.owl#Resource"/>
						<input type="submit" name="create" id="create" value="Ressource"/>
					</form>
					//
					<form role="form" action="http://localhost:9000/create">
						<input type="hidden" name="uri" id="uri" value="http://www.assemblee-virtuelle.org/ontologies/v1.owl#Event"/>
						<input type="submit" name="create" id="create" value="Evènement"/>
					</form>
					<form role="form" action="http://localhost:9000/create">
						<input type="hidden" name="uri" id="uri" value="http://www.assemblee-virtuelle.org/ontologies/v1.owl#Task"/>
						<input type="submit" name="create" id="create" value="Tâche"/>
					</form>
				</div>

				<h4>Dans une autre ontologie</h4>
				<form role="form" action="http://localhost:9000/create">
					<div class="col-md-6">
						<!--input class="form-control" type="text" name='uri' placeholder="Coller ou taper l&#x27;URI d&#x27;une classe dans une ontologie."></input-->
						<select class="form-control" type="text" name="uri" list="class_uris">
							<option label="foaf:Person" selected="selected"> http://xmlns.com/foaf/0.1/Person </option>
							<option label="doap:Project"> http://usefulinc.com/ns/doap#Project </option>
							<option label="foaf:Organization"> http://xmlns.com/foaf/0.1/Organization </option>
							<!-- http://www.w3.org/2002/12/cal/ical#Vevent" // "cal:Vevent" -->
							<option label="owl:Class"> http://www.w3.org/2002/07/owl#Class </option>
							<option label="owl:DatatypeProperty"> http://www.w3.org/2002/07/owl#DatatypeProperty </option>
							<option label="owl:ObjectProperty"> http://www.w3.org/2002/07/owl#ObjectProperty </option>
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
			
}