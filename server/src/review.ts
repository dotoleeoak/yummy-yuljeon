import { By, until, type ThenableWebDriver } from 'selenium-webdriver'

export const getReview = async (placeId: string, driver: ThenableWebDriver) => {
  await driver.get('https://place.map.kakao.com/' + placeId)
  await new Promise((resolve) => setTimeout(resolve, 200))
  const reviewWrapper = await driver.findElement(
    By.className('evaluation_review')
  )
  const moreButton = await reviewWrapper.findElement(By.className('link_more'))
  for (let i = 0; i < 10; i++) {
    await moreButton.click()
    await new Promise((resolve) => setTimeout(resolve, 200))
    if ((await moreButton.getText()).includes('접기')) {
      break
    }
  }
  const review = await reviewWrapper.findElements(By.className('txt_comment'))
  const reviewList = await Promise.all(
    review.map(async (review) => {
      let content = await review.getText()
      content = content.replace('\n더보기', '')
      return {
        content
      }
    })
  )
  const openHour = await driver.findElement(By.className('openhour_wrap'))
  const openHourText = await openHour.getText()
  return {
    reviews: reviewList.filter((review) => review.content !== ''),
    openHour: openHourText.replace('\n더보기', '')
  }
}
