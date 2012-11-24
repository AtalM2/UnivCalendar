Testing Framework for Funambol Caldav 1.0

author: rpolli@babel.it

Testing the connector
-----------------------------
funambol-caldav uses funambol connector testing 
framework to certify its behavior. This folder 
contains the framework


Running the test
-----------------------------
0- build the connector so that in CONNECTOR/target/ there're required jars

1- configure the syncsource in ./config/caldav-vcalendar.xml

2- configure the console logging and use 
# source bin/myprofile.env 

3- configure funambol datasources ./config/com/funambol/server/db/db.xml and eventually run
     funambol database (mysql, hypersonic)

4- move or remove away the .svn files from data/basic/x-vcalendar/ because
     fctf try to use them as item

5- run the "basic" test
# bin/fctf -s config/caldav-vcalendar.xml -u user -p pass -t basic

6- check the output

