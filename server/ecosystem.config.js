module.exports = {
  apps: [
    {
      name: 'app',
      script: 'node dist/main.js'
    }
  ],
  max_memory_restart: '500M'
  // deploy: {
  //   production: {
  //     user: 'ubuntu',
  //     host: 'api.dotoleeoak.me',
  //     repo: 'git@github.com:dotoleeoak/yummy-yuljeon.git',
  //     path: '/home/ubuntu/yummy-yuljeon/server',
  //     'post-deploy': 'pnpm install && pnpm build && fuser -k 3000/tcp'
  //   }
  // }
}
