package av.semforms

import deductions.runtime.sparql_cache.DataSourceManager
import org.w3.banana.RDF
import java.net.URL
import deductions.runtime.sparql_cache.PopulateRDFCacheTrait

/**
 * @author jmv
 */
trait PopulateDB[Rdf <: RDF, DATASET]
    extends DataSourceManager[Rdf, DATASET]
    with PopulateRDFCacheTrait[Rdf, DATASET] {
  /**
   * replace Same Language Triples in named graph `graphURI`
   *  with the triples coming from given `url`
   */
  def setSpecificFormConfig() = {
    implicit val graph: Rdf#Graph = allNamedGraph
    replaceSameLanguageTriples(new URL(
      "https://raw.githubusercontent.com/assemblee-virtuelle/pair/master/form_labels.ttl"),
      "rdf-i18n")
  }
}