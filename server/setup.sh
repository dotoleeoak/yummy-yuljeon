#!/bin/bash

set -x

git stash
git pull

fuser -k 3000/tcp

pnpm i
pnpm build
node dist/main.js
