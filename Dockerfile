# Dockerfile
FROM postgres:16-alpine

# 環境変数（デフォルトDB、ユーザー、パスワード）
ENV POSTGRES_DB=app \
    POSTGRES_USER=app \
    POSTGRES_PASSWORD=secret

# 初期化SQL（任意）を配置
COPY ./initdb/ /docker-entrypoint-initdb.d/

EXPOSE 15432
