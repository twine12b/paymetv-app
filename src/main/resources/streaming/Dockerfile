FROM alpine:3.14.10

ARG VERSION

LABEL maintainer="zgist" \
        org.label-schema.name="Lighttpd" \
        org.label-schema.version=$VERSION

ENV UID=100 \
    GID=101

# Install lighttpd and create necessary directories
RUN set -ex && \
    apk add --no-cache lighttpd && \
    # Create required directories for lighttpd \
    mkdir -p /var/www/localhost/htdocs && \
    mkdir -p /var/lib/lighttpd/cache/compress && \
    mkdir -p /var/log/lighttpd && \
    mkdir -p /run/lighttpd && \
    # Set proper ownership for lighttpd user (already exists in Alpine) \
    chown -R lighttpd:lighttpd /var/www && \
    chown -R lighttpd:lighttpd /var/lib/lighttpd && \
    chown -R lighttpd:lighttpd /var/log/lighttpd && \
    chown -R lighttpd:lighttpd /run/lighttpd && \
    # Clean up
    rm -rf /var/cache/apk/*

RUN apk --no-cache add php7 \
    php7-cgi


COPY conf/lighttpd.conf /etc/lighttpd/lighttpd.conf

COPY html/ /var/www/

# Set proper permissions on configuration and web content
RUN chown -R lighttpd:lighttpd /var/www && \
    chmod -R 755 /var/www && \
    find /var/www -type f -exec chmod 644 {} \; && \
    chmod 644 /etc/lighttpd/lighttpd.conf

WORKDIR /var/www

VOLUME ["/var/www"]

EXPOSE 80

# Health check to ensure lighttpd is responding
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8081/ || exit 1

# Run lighttpd in foreground mode
CMD ["lighttpd", "-D", "-f", "/etc/lighttpd/lighttpd.conf"]
