## Agregar epajava al proyecto
mvn deploy:deploy-file -Durl=file:///lib/ -Dfile=lib/epajava.jar -DgroupId=com.epanet -DartifactId=epajava -Dversion=1.0 -Dpackaging=jar

Compile with mvn jfx:jar to create a jar
Compile with mvn jfx:native to create a native

