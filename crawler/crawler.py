import requests
import re
import json
import shutil
import os

import argparse

import tqdm

from bs4 import BeautifulSoup

import pandas as pd

from collections import defaultdict


def cleanhtml(raw_html):
    cleanr = re.compile('<.*?>')
    cleantext = re.sub(cleanr, '', raw_html)
    return cleantext.strip()


def main(start, end):
    data = defaultdict(list)

    for idx in tqdm.trange(start, end):
        url = 'http://www.etoretro.ru/pic{}.htm'.format(idx)
        r = requests.get(url, allow_redirects=False)

        # redirecting, there is no object with corresponding id
        # Is everything ok?
        if r.status_code == 200:
            content = r.content.decode(r.encoding)

            # there is no map!
            meta_search = re.search('gMap.singlePhoto =(.*)', content, re.IGNORECASE)
            if meta_search is None:
                continue

            meta = json.loads(meta_search.group(1).strip(' ;'))

            # the object is not on the map!
            if type(meta) is list:
                continue

            soup = BeautifulSoup(content, 'lxml')

            city = cleanhtml(re.search('Старый город:(.*)', content, re.IGNORECASE).group(1))
            title = soup.find(property='og:title').get('content')
            img_src = soup.find(property='og:image').get('content')

            img_r = requests.get(img_src, stream=True)
            img_name = img_src.split('/')[~0]
            img_path = os.path.join('images', img_name)
            with open(img_path, 'wb') as out_file:
                shutil.copyfileobj(img_r.raw, out_file)

            data['title'].append(title)
            data['city'].append(city)
            data['img'].append(img_name)
            data['latitude'].append(meta['latitude'])
            data['longitude'].append(meta['longitude'])

    df = pd.DataFrame(data)
    df.to_csv('meta_data.csv', index=False)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("--start", help="First index for iteration", default=10000, type=int)
    parser.add_argument("--end", help="Last index for iteration", default=100000, type=int)
    args = parser.parse_args()

    main(args.start, args.end)
