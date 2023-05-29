#!/bin/bash

set -ex

fuser -k 3000/tcp

git reset --hard
git pull

pnpm i
pnpm build
node dist/main.js
