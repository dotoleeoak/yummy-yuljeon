#!/bin/bash

set -ex

git reset --hard
git pull

pnpm i
pnpm build
node dist/main.js
