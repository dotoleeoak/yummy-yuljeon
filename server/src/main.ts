import express from 'express'
import dotenv from 'dotenv'
import axios from 'axios'
import { z } from 'zod'
import { searchImage } from './search'

dotenv.config()

if (!process.env.KAKAO_REST_API_KEY) {
  throw new Error('KAKAO_REST_API_KEY is not defined')
}

const app = express()

app.get('/', (req, res) => {
  res.send('Hello World!')
})

app.get('/place', async (req, res) => {
  const radius = req.query.distance || '1000'
  const x = req.query.x || '126.974579152201'
  const y = req.query.y || '37.2940144502368'
  const page = req.query.page || '1'
  const size = req.query.size || '8'

  const category_group_code = 'FD6' // 음식점

  const { data } = await axios.get(
    'https://dapi.kakao.com/v2/local/search/category.json',
    {
      headers: {
        Authorization: `KakaoAK ${process.env.KAKAO_REST_API_KEY}`
      },
      params: {
        category_group_code,
        radius,
        x,
        y,
        page,
        size
      }
    }
  )

  const schema = z
    .object({
      meta: z.object({
        total_count: z.number(),
        is_end: z.boolean()
      }),
      documents: z.array(
        z.object({
          id: z.string(),
          place_name: z.string(),
          road_address_name: z.string(),
          phone: z.string(),
          distance: z.string(),
          x: z.string(),
          y: z.string()
        })
      )
    })
    .transform((data) => ({
      meta: data.meta,
      places: data.documents.map((place) => ({
        id: place.id,
        name: place.place_name,
        address: place.road_address_name,
        phone: place.phone,
        distance: place.distance,
        x: place.x,
        y: place.y
      }))
    }))

  const parsedData = schema.parse(data)

  const dataWithImage = await Promise.all(
    parsedData.places.map(async (place) => {
      const image = await searchImage(place.name)
      return {
        ...place,
        image
      }
    })
  )

  res.send(dataWithImage)
})

app.listen(3000, () => {
  console.log('🌈 Server listening on port 3000')
})
