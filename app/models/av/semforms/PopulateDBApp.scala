package av.semforms

import org.w3.banana.jena.Jena
import com.hp.hpl.jena.query.Dataset
import deductions.runtime.jena.JenaHelpers
import org.w3.banana.jena.JenaModule
import deductions.runtime.jena.RDFStoreLocalJena1Provider
import deductions.runtime.services.DefaultConfiguration

/**
 * @author jmv
 */
object PopulateDBApp extends JenaModule
    with App
    with JenaHelpers
    with RDFStoreLocalJena1Provider
    with PopulateDB[Jena, Dataset]
  with DefaultConfiguration {

	resetCommonVocabularies()
  resetCommonFormSpecifications()
  resetRDFI18NTranslations()
  
  loadCommonVocabularies()
  
  val form_specs_av = githubcontent +
    "/assemblee-virtuelle/pair/master/form_guillaume.ttl"
  val form_specs_foaf = githubcontent +
    "/jmvanel/semantic_forms/master/scala/forms/form_specs/foaf.form.ttl"
  val form_specs_owl = githubcontent +
    "/jmvanel/semantic_forms/master/scala/forms/form_specs/owl.form.ttl"
  storeContentInNamedGraph(form_specs_av)
  storeContentInNamedGraph(form_specs_foaf)
  storeContentInNamedGraph(form_specs_owl)

  loadFromGitHubRDFI18NTranslations()
  implicit val graph: Rdf#Graph =
    rdfStore.r( dataset, { allNamedGraph }) . get
  setSpecificFormConfig
}
