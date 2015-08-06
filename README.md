# semforms
semantic CRUD applications for assemblee-virtuelle,
a specialization of 
https://github.com/jmvanel/semantic_forms/tree/master/scala/forms_play

Most practical things are here:

[forms\_play/README.md](https://github.com/jmvanel/semantic_forms/blob/master/scala/forms_play/README.md)

#Database Administration

To dump the database in N-Triples format:

    sbt "runMain tdb.tdbdump --loc=TDB" > dump.nq

To re-load the database from N-Triples format (possibly delete the TDB directory before) :

    sbt "runMain tdb.tdbloader --loc=TDB dump.nq "
