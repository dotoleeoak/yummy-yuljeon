import os
import sys
from selenium import webdriver


def review(id: str):
    driver = (
        webdriver.Chrome("~/Downloads/chromedriver_mac_arm64")
        if os.name == "posix"
        else webdriver.PhantomJS("~/phantomjs/bin/phantomjs")
    )
    driver.get("https://place.map.kakao.com/" + id)

    driver.implicitly_wait(10)

    opening_time = driver.find_element_by_xpath(
        '//*[@id="mArticle"]/div[1]/div[2]/div[2]/div/div[2]/div/ul[1]/li[2]'
    )

    print(opening_time.text)

    driver.quit()


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python review.py <review_id>")
        sys.exit(1)

    review(sys.argv[1])
