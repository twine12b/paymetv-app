# 403 Forbidden Error - Root Cause Analysis and Fix

## Problem Summary
When accessing the Lighttpd container at `http://localhost:8081`, the server returned **HTTP 403 Forbidden** instead of serving the expected content.

## Root Cause Analysis

### Issue #1: Empty Document Root (Primary Issue)
**Problem:** The original Dockerfile created a `/var/www` directory and set it as a VOLUME, but **never copied any content** into it during the build process.

**Evidence:**
```dockerfile
# Original Dockerfile (BROKEN)
WORKDIR /var/www
VOLUME ["/var/www"]
# No COPY command to add content!
```

**Result:** When the container started without a mounted volume, `/var/www` was empty. Lighttpd couldn't find any files to serve.

---

### Issue #2: Missing Custom Configuration
**Problem:** The Dockerfile didn't copy the custom `lighttpd.conf` file, so Alpine's default configuration was used instead.

**Evidence:**
- Custom config sets: `server.document-root = "/var/www"`
- Default Alpine config sets: `var.basedir = "/var/www/localhost"` → document root becomes `/var/www/localhost/htdocs`

**Result:** Even after copying files to `/var/www`, Lighttpd was looking in `/var/www/localhost/htdocs` instead.

---

### Issue #3: Directory Listing Disabled
**Problem:** The custom `lighttpd.conf` has `dir-listing.activate = "disable"`.

**Result:** Even if Lighttpd found the directory, it wouldn't show a directory listing without an index file. Combined with the empty directory, this guaranteed a 403 error.

---

### Issue #4: Missing Permissions Setup
**Problem:** The Dockerfile didn't set proper ownership and permissions on the web content.

**Result:** Potential permission issues if files were copied with incorrect ownership.

---

## The Fix

### Changes Made to `wip3/Dockerfile`

#### 1. **Copy Custom Configuration**

```dockerfile
# Copy custom lighttpd configuration
# This sets the correct document root and other settings
COPY conf/lighttpd.conf /etc/lighttpd/lighttpd.conf
```

**Why:** Ensures Lighttpd uses the correct document root (`/var/www`) and other custom settings.

---

#### 2. **Copy Default Web Content**
```dockerfile
# Copy the html directory content to /var/www
# This provides default content so the container works without mounted volumes
COPY html/ /var/www/
```

**Why:** Provides actual files for Lighttpd to serve. The container now works standalone without requiring mounted volumes.

---

#### 3. **Set Proper Permissions**
```dockerfile
# Set proper permissions on configuration and web content
RUN chown -R lighttpd:lighttpd /var/www && \
    chmod -R 755 /var/www && \
    find /var/www -type f -exec chmod 644 {} \; && \
    chmod 644 /etc/lighttpd/lighttpd.conf
```

**Why:** 
- Ensures the `lighttpd` user (which runs the server) can read all files
- Directories get `755` (rwxr-xr-x) - readable and executable by all
- Files get `644` (rw-r--r--) - readable by all, writable only by owner
- Config file gets `644` for security

---

## Complete Fixed Dockerfile

```dockerfile
FROM alpine:3.14.6

ARG VERSION

LABEL maintainer="zgist" \
        org.label-schema.name="Lighttpd" \
        org.label-schema.version=$VERSION

ENV UID=100 \
    GID=101

# Install lighttpd and create necessary directories
RUN set -ex && \
    apk add --no-cache lighttpd && \
    mkdir -p /var/www/localhost/htdocs && \
    mkdir -p /var/lib/lighttpd/cache/compress && \
    mkdir -p /var/log/lighttpd && \
    mkdir -p /run/lighttpd && \
    chown -R lighttpd:lighttpd /var/www && \
    chown -R lighttpd:lighttpd /var/lib/lighttpd && \
    chown -R lighttpd:lighttpd /var/log/lighttpd && \
    chown -R lighttpd:lighttpd /run/lighttpd && \
    rm -rf /var/cache/apk/*

# Copy custom lighttpd configuration
COPY conf/lighttpd.conf /etc/lighttpd/lighttpd.conf

# Copy the html directory content to /var/www
COPY html/ /var/www/

# Set proper permissions on configuration and web content
RUN chown -R lighttpd:lighttpd /var/www && \
    chmod -R 755 /var/www && \
    find /var/www -type f -exec chmod 644 {} \; && \
    chmod 644 /etc/lighttpd/lighttpd.conf

WORKDIR /var/www

VOLUME ["/var/www"]

EXPOSE 80

CMD ["lighttpd", "-D", "-f", "/etc/lighttpd/lighttpd.conf"]
```

---

## Verification

### Build and Test
```bash
# Build the image
docker build -t test . -f wip3/Dockerfile

# Run the container
docker run -d -p 8081:80 -it test:latest

# Test the response
curl -I http://localhost:8081/
```

### Expected Result
```
HTTP/1.1 200 OK
Content-Type: text/html
...
```

### Actual Result After Fix
✅ **HTTP 200 OK** - Server successfully serves the `index.html` file from `/var/www/`

---

## Key Takeaways

1. **Always provide default content** in web server images, even if you expect volumes to be mounted
2. **Copy custom configurations** explicitly - don't rely on base image defaults
3. **Set proper permissions** for the user running the service
4. **Test without volumes** to ensure the image works standalone
5. **Check logs and inspect containers** when debugging (use `docker exec` to verify file locations)

---

## Docker Best Practices Applied

✅ **Minimal layers** - Combined related RUN commands
✅ **Proper ownership** - Files owned by the service user, not root
✅ **Security** - Correct file permissions (644 for files, 755 for directories)
✅ **Reproducibility** - Image works without external dependencies
✅ **Documentation** - Clear comments explaining each step

## Run the script
✅ **Run the script port 8081** - docker stop $(docker ps -q --filter "publish=8081") 2>/dev/null || true && docker build -t test . -f Dockerfile && docker run -d -p 8081:80 -it test:latest && curl -I http://localhost:8081/ | grep HTTP/1.1