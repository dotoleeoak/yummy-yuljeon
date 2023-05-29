import { By, until, type ThenableWebDriver } from 'selenium-webdriver'

export const getReview = async (placeId: string, driver: ThenableWebDriver) => {
  await driver.get('https://place.map.kakao.com/' + placeId)
  await driver.wait(
    until.elementLocated(By.className('list_evaluation')),
    10000
  )
  const reviewWrapper = await driver.findElement(
    By.className('evaluation_review')
  )
  // const moreButton = await reviewWrapper.findElement(By.className('link_more'))
  // if (moreButton) {
  //   await moreButton.click()
  //   await driver.manage().setTimeouts({ implicit: 3000 })
  // }
  const review = await reviewWrapper.findElements(By.className('txt_comment'))
  const reviewList = await Promise.all(
    review.map(async (review) => {
      // const name = await review.findElement({ css: '.txt_name' })
      // const rating = await review.findElement({ css: '.grade_star' })
      // const content = await review.findElement({ css: '.txt_comment' })
      // const date = await review.findElement({ css: '.time_write' })
      // const image = await review.findElement({ css: '.link_photo' })
      // const nameText = await name.getText()
      // const ratingText = await rating.getAttribute('class')
      // const contentText = await content.getText()
      // const dateText = await date.getText()
      // const imageText = await image.getAttribute('href')
      let content = await review.getText()
      content = content.replace('더보기', '')
      return {
        content
      }
    })
  )
  const openHour = await driver.findElement(By.className('openhour_wrap'))
  const openHourText = await openHour.getText()
  return {
    reviews: reviewList.filter((review) => review.content !== ''),
    openHour: openHourText
  }
}
