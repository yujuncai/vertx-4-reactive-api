# 使用官方 JDK 基础镜像
FROM eclipse-temurin:21-jdk-alpine
# 设置工作目录
WORKDIR /app
# 复制本地构建好的 jar 包到容器中
COPY target/vertx-4-reactive-rest-api-0.1-SNAPSHOT-fat.jar app.jar

# 运行应用
ENTRYPOINT ["java", "-jar", "app.jar"]