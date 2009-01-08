
                 Sprout

              ------o------

Sprout is a simple base from which you can 
build any CMS in Java.

You will need Mysql 4.0 or later and Java 1.4 or later 
added to the path to run Sprout.

You need Ant added to the path to build Sprout.

Run the server with run.bat or run.sh depending 
on your OS and build sprout with ant:

  > run.bat | run.sh &
  
  Edit the database username and password in 
  the Sprout.java source and at the bottom of 
  the build.xml now if you want to use 
  something other than "root"/"".
  
  > ant

Then simply create a database like this:

  > CREATE DATABASE sprout DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;

And pipe the create.sql to it like this:

  > mysql sprout < create.sql

Finally browse to: http://localhost:9000

To view the database structure, double click lib/logic.jar and
open res/sprout.xml, you should not need to modify the database
but you can easily do so in logic and save the sprout.xml. The 
changes will be executed when you run the ant script.

Now just edit the Type.java types to reflect the model your project 
requires and write the HTML to manipulate this model.

Happy Hacking!

Version:

  B - RSS Feed
      Cache Leak
  C - Comments
  D - Connection Leak
  E - Columnizing
  F - Flash Video
  G - Button CSS Fix

Todo:

  - Delete images
  - Pagination
  - Cache Timeout