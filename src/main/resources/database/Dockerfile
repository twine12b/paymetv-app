FROM mysql:8.4
LABEL authors="richardrenaud"

# optional environment defaults
#ENV MYSQL_DATABASE=mydb
#ENV ROOT_USER=root
#ENV MYSQL_ROOT_PASSWORD=rootpassword

ENV MYSQL_DATABASE=paymetv_db1
COPY dumps/database.sql /docker-entrypoint-initdb.d/
COPY initialise_db.sql /docker-entrypoint-initdb.d/

EXPOSE 3306

# ensure permissions (optional but safe)
RUN chmod 644 /docker-entrypoint-initdb.d/database.sql
RUN chmod 644 /docker-entrypoint-initdb.d/initialise_db.sql

CMD ["mysqld"]