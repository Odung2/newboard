FROM mysql:8.0
ENV MYSQL_ROOT_PASSWORD=newboard
ENV MYSQL_DATABASE=newboard
ENV MYSQL_USER=mingle
ENV MYSQL_PASSWORD=mingle
ENV MYSQL_CHARSET=utf8mb4
ENV MYSQL_COLLATION=utf8mb4_unicode_ci
COPY my.cnf /etc/mysql/conf.d/my.cnf
