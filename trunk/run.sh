java -Dhost=sprout.rupy.se -Dmail=mail1.comhem.se -classpath lib/activation.jar:lib/http.jar:lib/json.jar:lib/mail.jar:lib/util.jar:lib/mysql.jar se.rupy.http.Daemon -port 9000 -delay 60 -timeout 3600 -threads 100 -live -pass secret