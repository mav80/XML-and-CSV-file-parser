cd Task01

echo "Czyszczenie..."
mvn clean

echo "Kompilacja..."
mvn compile 

echo "Pakowanie..."
mvn package 

cd ..