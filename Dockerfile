# ベースイメージ: JDK + Gradle 入り
FROM gradle:8.11-jdk17 AS build

WORKDIR /app

# 先に Gradle 定義だけコピーして依存をキャッシュ
COPY ./build.gradle.kts ./settings.gradle.kts ./gradle.properties ./
RUN gradle --no-daemon dependencies || true

# ソースをコピー
COPY ./src ./src

# fat jar を作成（shadowJar）
RUN gradle --no-daemon clean shadowJar

# 2. 実行用ステージ（JRE のみ）
FROM eclipse-temurin:21-jre

WORKDIR /app

# fat jar をコピー（*-all.jar を1本にリネーム）
COPY --from=build /app/build/libs/*-all.jar /app/app.jar

COPY ./serviceAccountKey.json /app/serviceAccountKey.json

ENV PORT=8080
EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]
