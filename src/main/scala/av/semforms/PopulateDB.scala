package av.semforms

import deductions.runtime.sparql_cache.DataSourceManager
import org.w3.banana.RDF
import java.net.URL

/**
 * @author jmv
 */
trait PopulateDB[Rdf <: RDF, DATASET] extends DataSourceManager[Rdf, DATASET] {
    def replaceSameLanguageTriples(url: String, grapURI: String) = {
      
    }
}