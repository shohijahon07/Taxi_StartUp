#!/bin/bash

echo "Building frontend..."
cd fronted
npm install
npm run build

echo "Copying build files to backend resources..."
rm -rf ../backend/src/main/resources/static
mkdir -p ../backend/src/main/resources/static
cp -r dist/* ../backend/src/main/resources/static/

echo "Starting backend..."
cd ../backend
./mvnw spring-boot:run
