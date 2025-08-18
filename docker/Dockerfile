FROM ubuntu:latest

RUN apt-get update && apt-get upgrade -y && \
    apt-get install -y openjdk-21-jdk && \
    apt-get install bash -y && \
    apt-get install mysql-client -y && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

RUN echo "Europe/Minsk" > /etc/timezone && \
    ln -sf /usr/share/zoneinfo/Europe/Minsk /etc/localtime && \
    dpkg-reconfigure -f noninteractive tzdata

WORKDIR /root/rce

CMD ["/usr/bin/java", "-Xms1g", "-Xmx1g", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/root/tgb/auth.jar"]
