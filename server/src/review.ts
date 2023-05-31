import { By, until, type ThenableWebDriver, WebElement } from 'selenium-webdriver'

export const getReview = async (placeId: string, driver: ThenableWebDriver) => {
  await driver.get('https://place.map.kakao.com/' + placeId)
  await driver.wait(until.elementLocated(By.className('head_mapdetail')), 10000)
  driver.executeScript('document.querySelector(".head_mapdetail").innerHTML = ""')

  let reviewWrapper: WebElement
  let reviews: { content: string }[] = []
  let openHour = ''

  try {
    reviewWrapper = await driver.findElement(By.className('evaluation_review'))
  } catch (e) {
    return { reviews, openHour }
  }

  try {
    const moreButton = await reviewWrapper.findElement(By.className('link_more'))
    for (let i = 0; i < 10; i++) {
      await moreButton.click()
      await new Promise((resolve) => setTimeout(resolve, 100))
      if ((await moreButton.getText()).includes('접기')) {
        break
      }
    }
  } catch (e) {}

  const reviewElements = await reviewWrapper.findElements(By.className('txt_comment'))

  reviews = await Promise.all(
    reviewElements.map(async (element) => {
      let content = await element.getText()
      content = content.replace('\n더보기', '')
      return { content }
    })
  )

  reviews = reviews.filter((review) => review.content !== '')

  try {
    const openHourElement = await driver.findElement(By.className('openhour_wrap'))
    openHour = await openHourElement.getText()
    openHour = openHour.replace('\n더보기', '')
  } catch (e) {}

  return { reviews, openHour }
}
