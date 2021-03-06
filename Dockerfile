FROM openjdk:8

ENV FIREBASE_CREDENTIALS /app/firebase.json

WORKDIR /app

ADD ./src/main/resources/firebase.json /app/firebase.json
ADD build/libs/challenge-0.0.1-SNAPSHOT.jar challenge.jar

CMD java -jar -Xms128m -Xmx256m challenge.jar
