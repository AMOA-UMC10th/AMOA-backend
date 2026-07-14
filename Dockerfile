FROM eclipse-temurin:21-jre

# 일반 사용자 생성
RUN addgroup --system spring && adduser --system --ingroup spring spring

WORKDIR /app

COPY app.jar app.jar

# 파일 소유권 변경
RUN chown -R spring:spring /app

# 일반 사용자로 실행
USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]