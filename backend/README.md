# Backend für die Schau-Hin App

Folgende Endpunkte sind vorhanden. Die curl Aufrufe dienen als Beispiele für die lokale Umgebung:

GET /
Liefert alle Einträge
    curl localhost:3000/
POST /
Fügt einen Eintrag hinzu
    curl -X POST -H 'Content-Type: application/json' -d '{"name": "Max Mustermann"}'
GET /:id
Gibt den Eintrag mit einer bestimmten ID zurück
    curl localhost:3000/abcd
DELETE /:id
Löscht den Eintrag mit einer bestimmten ID, falls vorhanden. Gibt immer 204 zurück, selbst wenn der Eintrag nicht
vorhanden ist
    curl -X DELETE localhost:3000/abcd

## Prerequisites

Um das Backend zu benutzen muss einmal leinigen mindestens in der Version 2.0.0 installiert sein:
[leiningen]: https://github.com/technomancy/leiningen

Außerdem muss lokal eine DynamoDB Instanz laufen. Eine Anleitung dazu gibs hier:
[dynamodb]: http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Tools.DynamoDBLocal.html
Beim ersten mal nach der Installation muss noch die Tabelle in DynamoDB angelegt werden, das geht am einfachsten,
indem in der REPL die Funktion create-table ausgeführt wird.

## Running

To start a web server for the application, run:

    lein ring server-headless

## License

Copyright © 2016 FIXME
