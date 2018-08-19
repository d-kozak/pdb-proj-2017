# Desktop app connecting to Oracle Spatial database

This repository contains code of our project for [Advanced Database Systems](http://www.fit.vutbr.cz/study/course-l.php.en?id=8791 "Course page") that we made in winter 2017. It is a JavaFX desktop application. Part of it is written in javascript in order to use the [Leaflet](https://leafletjs.com/) library for visualizing maps. This part is located in a WebView JavaFX component. 

Unfortunately this app needs to access a proprietary Oracle spatial database, it won't work properly without it.

## Prequisities

* Java 8
* JavaFX
* Maven

## Compile and run

```
mvn clean install
java -jar target/fit-pdb17-epsilon-1.0-SNAPSHOT.jar
```

## Authors
* [David Kozak](https://github.com/d-kozak) dkozak94@gmail.com
* [Pavel Plaskon](https://github.com/enkelli) 
* [Jan Velecky](https://github.com/VVelda)
