#!/bin/bash

set -x

git pull

pnpm i
pnpm build

pm2 reload app
