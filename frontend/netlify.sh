#!/bin/bash

echo "Starting Netlify build process"

# Run the standard build
npm run build

# Make sure locales are processed properly
mkdir -p dist/locales
cp -v src/locales/*.json dist/locales/

echo "Build completed with locales copied to dist/locales/" 