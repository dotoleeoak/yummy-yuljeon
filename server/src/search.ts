import axios from 'axios'
import { z } from 'zod'

export const searchImage = async (query: string) => {
  const { data } = await axios.get(
    'https://openapi.naver.com/v1/search/image',
    {
      headers: {
        'X-Naver-Client-Id': process.env.NAVER_CLIENT_ID,
        'X-Naver-Client-Secret': process.env.NAVER_CLIENT_SECRET
      },
      params: {
        query
      }
    }
  )

  const schema = z.object({
    items: z.array(
      z.object({
        link: z.string(),
        thumbnail: z.string(),
        sizeheight: z.string(),
        sizewidth: z.string()
      })
    )
  })

  const parsed = schema.parse(data)

  return parsed.items[0].link
}
