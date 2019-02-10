# Rapport Projet TLC
 
Dans le cadre de ce projet nous allons découvrir la solution Platform as a Service (**PaaS**). 

Nous avons choisi **Google AppEngine** pour réaliser ce travail, nous utiliserons également la version d'essai de la plate-forme **Google Cloud Platform**.

Nous avons développé une application **Maven** dédiée au suivi de la condition physique d'une personne pendant qu'il coure. Nous avons utilisé **Google Datastore** pour la persistance des données.

Afin d'interagir avec notre application, nous avons mis en place un service **HTTP REST** pour stocker, supprimer et rechercher un utilisateur qui a la possibilité d’enregistrer ses courses. 

L’application enregistrera régulièrement le nom d'utilisateur, sa position (latitude et longitude), la date et l'heure du parcours (timestamp) et un identifiant unique qui identifiera une course spécifique.

1. Bulk Add: Chaque enregistrement créé sera directement envoyé au serveur. L’utilisateur pourra également enregistrer plusieurs courses à la fois.
Exemple: 
Requete POST `https://infinite-bruin-224415.appspot.com/api/run`
Avec comme Body :
- Exemple 1: une entité : `[{"id":1,"lat":4,"lon":2,"user":"user1","timestamp":3}]`
- Exemple 2: trois entités : `[
{"id":1,"lat":4,"lon":2,"user":"user1","timestamp":3},
{"id":2,"lat":4,"lon":2,"user":"user2","timestamp":3},
{"id":3,"lat":4,"lon":2,"user":"user3","timestamp":3}
        ]`
2. Search records: Les utilisateurs peuvent accéder aux informations des courses en sélectionnant n'importe quelle combinaison (Id, Timestamp, Location).
Exemple: 
* Requete GET `https://infinite-bruin-224415.appspot.com/api/run?id=10`
* Requete GET `https://infinite-bruin-224415.appspot.com/api/run?timestamp=1,1543775728`
* Requete GET `https://infinite-bruin-224415.appspot.com/api/run?id=10&timestamp=1,1543775728`

2. Bulk Delete: L’utilisateur peut supprimer plusieurs enregistrements en utilisant les 
identifiants des courses.
Exemple: 
* Requete DELETE `https://infinite-bruin-224415.appspot.com/api/run?list=10`
* Requete DELETE `https://infinite-bruin-224415.appspot.com/api/run?list=10,11,13`


Afin de bien gérer nos données et les accès à la base sur le DataStore, nous avons défini des index, comme nous pouvons le voir sur l’exemple ci-après qui indexe l’Id et le TimeStamp d’un Record: 
`<datastore-index kind="record" ancestor="false">
	<property name="id" direction="asc" />
	<property name="timestamp" direction="asc" />
</datastore-index>`

## *********************************************************************************************
This is a working skeleton. However, it only returns dummy values and you must replace them by interacting with Google Datastore.
The only java file you need to update is `src/main/java/tlc/tracking/RunResource.java`. You will find `@FIXME` comments where you should add code.
Still, you are encouraged to read the whole project files.

## Google AppEngine related files

  * `/src/main/webapp/WEB-INF/appengine-web.xml` - you must edit the application to replace `tlcgae2` by your application id
  * `/src/main/webapp/WEB-INF/datastore-indexes.xml` - you must put your indexes here

## Running locally

```
mvn appengine:devserver
```

And go to http://127.0.0.1:8080

## Deploying to Google Cloud

```
mvn appengine::update
```

