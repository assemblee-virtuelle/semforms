# semforms
semantic CRUD applications for assemblee-virtuelle,
a specialization of [semantic\_forms Play! generic Application](https://github.com/jmvanel/semantic_forms/tree/master/scala/forms_play)

Most practical things are here:

[forms\_play/README.md](https://github.com/jmvanel/semantic_forms/blob/master/scala/forms_play/README.md)

#Database Administration

To dump the database in N-Triples format:

    sbt "runMain tdb.tdbdump --loc=TDB" > dump.nq

To re-load the database from N-Triples format (possibly delete the TDB directory before) :

    sbt "runMain tdb.tdbloader --loc=TDB dump.nq "

To delete and reload ontologies (vocabularies) and form specifications:

    sbt "runMain av.semforms.PopulateDBApp"

# Installation

The installation process is similar to the forms_play one, so please check this project README located at the URL above if you have any questions. 

## Specific errors
If, when executing the `eclipse with-source=true` command, you get an "Unresolved dependency path error" like the following:

```
Note: Unresolved dependencies path:
[warn]      deductions:semantic_forms_2.11:1.0-SNAPSHOT (/workspace/semantic_forms/scala/semforms/build.sbt#L12-13)
[warn]        +- virtual-assembly:semantic_forms_av_2.11:1.0-SNAPSHOT
```

Please execute the following commands to fix that:

```
cd /semantic_forms_directory/scala/forms
sbt publishLocal
```

Then if you come back to the `/scala/semforms` directory and relaunch the `eclipse with-source=true` command on SBT, everything should be ok.
