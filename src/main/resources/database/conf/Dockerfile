FROM mysql:8.4
LABEL authors="richardrenaud"

#RUN apt-get update && apt-get install curl rsync -y && apt-get -y clean
#ENV MYSQL_ROOT_PASSWORD=password
ENV MYSQL_DATABASE=paymetv_db1
#ENV MYSQL_USER=paymetv_user
#ENV MYSQL_PASSWORD=paymetv_pass
COPY dumps/database.sql /docker-entrypoint-initdb.d/
RUN chmod 644 /docker-entrypoint-initdb.d/database.sql-old

EXPOSE 3306

CMD ["mysqld"]


#ENTRYPOINT ["top", "-b"]