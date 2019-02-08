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

