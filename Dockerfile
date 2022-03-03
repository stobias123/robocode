FROM openjdk:8-jre-alpine

COPY ./build/robocode-*-setup.jar ./
RUN java -jar robocode-*-setup.jar || true


WORKDIR /root/robocode
ENTRYPOINT ["./robocode.sh"]
CMD ["-battle", "/root/robocode/battles/demo.battle", "-nosound", "-nodisplay"]
