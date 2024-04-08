FROM mongo:4.4-bionic

# set locale
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en' LC_ALL='en_US.UTF-8'

RUN apt-get update \
    && apt-get install -y --no-install-recommends tzdata curl ca-certificates fontconfig locales \
    && echo "en_US.UTF-8 UTF-8" >> /etc/locale.gen \
    && locale-gen en_US.UTF-8

RUN apt-get install -y --no-install-recommends software-properties-common wget apt-transport-https gnupg \
    && wget --no-check-certificate -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public | apt-key add - \
    && wget --no-check-certificate -qO - https://nginx.org/keys/nginx_signing.key | apt-key add - \
    && add-apt-repository --yes https://packages.adoptium.net/artifactory/deb/ \
    && echo "deb https://nginx.org/packages/mainline/ubuntu/ bionic nginx" >> /etc/apt/sources.list \
    && echo "deb-src https://nginx.org/packages/mainline/ubuntu/ bionic nginx" >> /etc/apt/sources.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends supervisor temurin-11-jre nginx \
    && rm -rf /var/lib/apt/lists/*

# Create logs storaging
RUN mkdir -p /app/logs

# Nginx config
RUN mkdir -p /etc/nginx/
COPY config/nginx_release.conf /etc/nginx/nginx.conf

# Suporvisord config
RUN mkdir -p /etc/supervisor/conf.d/
COPY config/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# MongoDB Config
# Note. Do not put the mongo init scripts to /docker-entrypoint-initdb.d for automated initialization.
# Currently it causes error when initializing replica set in mongo entrypoint execution.
# Use supervisord instead.
COPY config/mongo/mongo-init.sh /app/mongo/mongo-init.sh
COPY config/mongo/mongo-init-replica-set.js /app/mongo/mongo-init-replica-set.js
COPY config/mongo/mongo-create-user.js /app/mongo/mongo-create-user.js
RUN openssl rand -base64 756 > /app/mongo/keyfile
RUN chown 999 /app/mongo/keyfile && chmod 400 /app/mongo/keyfile

ENV MONGO_INITDB_DATABASE 4-Key-Metrics
ENV MONGO_INITDB_ROOT_USERNAME admin
ENV MONGO_INITDB_ROOT_PASSWORD root

# Copy BACKEND artifact
ENV APP_ENV release
ENV DB_USER 4km
ENV DB_PASSWORD 4000km
ENV AES_KEY "&E)H@MbQeThWmZq4"
ENV AES_IV "D(G+KbPeShVkYp3s"

COPY artifacts/backend/run.sh /app/metrik-service.sh
COPY artifacts/backend/metrik-backend-*.jar /app/metrik-backend.jar

# Copy FRONTEND artifact
COPY artifacts/frontend/dist /usr/share/nginx/html

RUN find /app -name "*.sh" -exec chmod +x {} \;

EXPOSE 80 9000 27017

ENTRYPOINT ["supervisord"]
