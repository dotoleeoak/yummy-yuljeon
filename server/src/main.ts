import express from 'express'
import dotenv from 'dotenv'

dotenv.config()

if (!process.env.KAKAO_REST_API_KEY) {
  throw new Error('KAKAO_REST_API_KEY is not defined')
}

const app = express()

app.get('/', (req, res) => {
  res.send('Hello World!')
})

app.listen(3000, () => {
  console.log('Server listening on port 3000')
})
