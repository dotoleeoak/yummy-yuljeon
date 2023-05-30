#!/bin/bash

set -x

git pull

fuser -k 3000/tcp

pnpm i
pnpm build

pm2 reload app
