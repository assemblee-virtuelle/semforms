package av.semforms

import org.w3.banana.jena.Jena
import com.hp.hpl.jena.query.Dataset
import deductions.runtime.jena.JenaHelpers
import org.w3.banana.jena.JenaModule
import deductions.runtime.jena.RDFStoreLocalJena1Provider

/**
 * @author jmv
 */
object PopulateDBApp extends JenaModule
    with App
    with JenaHelpers
    with RDFStoreLocalJena1Provider
    with PopulateDB[Jena, Dataset] {
  
	resetCommonVocabularies()
  resetCommonFormSpecifications()
  resetRDFI18NTranslations()
  
  loadCommonVocabularies()
  loadCommonFormSpecifications()
  loadFromGitHubRDFI18NTranslations()
  setSpecificFormConfig()
}