import express from 'express'
import dotenv from 'dotenv'
import axios from 'axios'
import { z } from 'zod'
import { searchImage } from './search'
import { getReview } from './review'
import { Builder, Browser } from 'selenium-webdriver'
import chrome from 'selenium-webdriver/chrome'

dotenv.config()

if (!process.env.KAKAO_REST_API_KEY) {
  throw new Error('KAKAO_REST_API_KEY is not defined')
}

if (!process.env.NAVER_CLIENT_ID || !process.env.NAVER_CLIENT_SECRET) {
  throw new Error('NAVER_CLIENT is not defined')
}

// headless chrome
const driverOption = new chrome.Options()
driverOption.addArguments('headless')
const driver = new Builder()
  .forBrowser(Browser.CHROME)
  .setChromeOptions(driverOption)
  .build()

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

  const placesWithImage = await Promise.all(
    parsedData.places.map(async (place) => {
      const image = await searchImage(place.name + ' 성균관대역')
      return {
        ...place,
        image
      }
    })
  )

  const dataWithImage = {
    meta: parsedData.meta,
    places: placesWithImage
  }

  res.send(dataWithImage)
})

app.get('/place/:id', async (req, res) => {
  const review = await getReview(req.params.id, driver)

  res.send(review)
})

app.listen(3000, () => {
  console.log('🌈 Server listening on port 3000')
})
