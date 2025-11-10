#!/bin/bash
# 为GitHub Actions创建debug keystore

KEYSTORE_FILE="app/debug.keystore"

if [ ! -f "$KEYSTORE_FILE" ]; then
    echo "Creating debug keystore..."
    keytool -genkey -v \
        -keystore "$KEYSTORE_FILE" \
        -storepass android \
        -alias androiddebugkey \
        -keypass android \
        -keyalg RSA \
        -keysize 2048 \
        -validity 10000 \
        -dname "CN=Android Debug,O=Android,C=US"
    echo "Debug keystore created successfully!"
else
    echo "Debug keystore already exists"
fi
