import { By, until, type ThenableWebDriver } from 'selenium-webdriver'

export const getReview = async (placeId: string, driver: ThenableWebDriver) => {
  await driver.get('https://place.map.kakao.com/' + placeId)
  await driver.wait(until.elementLocated(By.className('head_mapdetail')), 10000)
  driver.executeScript(
    'document.querySelector(".head_mapdetail").innerHTML = ""'
  )
  const reviewWrapper = await driver.findElement(
    By.className('evaluation_review')
  )
  try {
    const moreButton = await reviewWrapper.findElement(
      By.className('link_more')
    )
    for (let i = 0; i < 10; i++) {
      await moreButton.click()
      await new Promise((resolve) => setTimeout(resolve, 100))
      if ((await moreButton.getText()).includes('접기')) {
        break
      }
    }
  } catch (e) {}
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
  const reviews = reviewList.filter((review) => review.content !== '')
  try {
    const openHour = await driver.findElement(By.className('openhour_wrap'))
    const openHourText = await openHour.getText()
    return {
      reviews,
      openHour: openHourText.replace('\n더보기', '')
    }
  } catch (e) {
    return {
      reviews,
      openHour: ''
    }
  }
}
