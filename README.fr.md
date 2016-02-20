
# Charger des données CSV

Lancer sous SBT:

    runMain deductions.runtime.jena.CSVImporterApp /home/jmv/Documents/AV-PAIRs-Acteurs-Individus.csv urn:AV-PAIRs-Acteurs-Individus/ av.import.ttl

Le fichier Turtle av.import.ttl contient par exmple:

```turtle
@prefix av: <http://www.assemblee-virtuelle.org/ontologies/v1.owl#> .
<any:ROW> a av:Person .
<any:ROW> av:likesOrganization <http://ldp.virtual-assembly.org/resource/AV> .
```

