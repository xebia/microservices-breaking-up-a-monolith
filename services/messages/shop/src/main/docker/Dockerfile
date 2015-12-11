FROM java:openjdk-8-jre

COPY JARFILENAME /

USER nobody

CMD [ "java", "-Djava.security.egd=file:/dev/urandom", "-jar", "JARFILENAME" ]
