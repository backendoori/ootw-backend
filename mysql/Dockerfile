FROM mysql:8
ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=ootw
ENV MYSQL_ROOT_USER=root
COPY ./ /docker-entrypoint-initdb.d/
