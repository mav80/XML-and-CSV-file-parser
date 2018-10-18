cd Task01

echo Czyszczenie...
call mvn clean

echo Kompilacja...
call mvn compile 

echo Pakowanie...
call mvn package 

cd ..