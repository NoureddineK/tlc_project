# Rapport Projet TLC
 
Dans le cadre de ce projet nous allons découvrir la solution Platform as a Service (**PaaS**). 

Nous avons choisi **Google AppEngine** pour réaliser ce travail, nous utiliserons également la version d'essai de la plate-forme **Google Cloud Platform**.

Nous avons développé une application **Maven** dédiée au suivi de la condition physique d'une personne pendant qu'il coure. Nous avons utilisé **Google Datastore** pour la persistance des données.

Afin d'interagir avec notre application, nous avons mis en place un service **HTTP REST** pour stocker, supprimer et rechercher un utilisateur qui a la possibilité d’enregistrer ses courses. 

L’application enregistrera régulièrement le nom d'utilisateur, sa position (latitude et longitude), la date et l'heure du parcours (timestamp) et un identifiant unique qui identifiera une course spécifique.

Pour plus de détails sur notre projet et le benchmark de l'application, référez vous au [rapport de notre projet](https://github.com/NoureddineK/tlc_project/blob/master/Rapport_TLC_MERZOUK_KADRI.pdf)


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

