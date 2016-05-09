
# Charger des données CSV

Lancer sous SBT:

    runMain deductions.runtime.jena.CSVImporterApp AV-PAIRs-Acteurs-Individus.csv urn:AV-PAIRs-Acteurs-Individus/ av.import.ttl
    runMain deductions.runtime.jena.CSVImporterApp AV-PAIRs-Acteurs-Collectifs.csv urn:AV-PAIRs-Acteurs-Collectifs/ ~/src/semforms/av.import-organisation.ttl

Le fichier Turtle av.import.ttl contient par exmple:

```turtle
@prefix av: <http://www.assemblee-virtuelle.org/ontologies/v1.owl#> .
<any:ROW> a av:Person .
<any:ROW> av:likesOrganization <http://ldp.virtual-assembly.org/resource/AV> .
```

