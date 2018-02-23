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

from multiprocessing import Pool


def cleanhtml(raw_html):
    cleanr = re.compile('<.*?>')
    cleantext = re.sub(cleanr, '', raw_html)
    return cleantext.strip()


def load_page(idx):
    res = dict()

    url = 'http://www.etoretro.ru/pic{}.htm'.format(idx)
    r = requests.get(url, allow_redirects=False)

    # redirecting, there is no object with corresponding id
    # Is everything ok?
    if r.status_code == 200:
        content = r.content.decode(r.encoding)

        # there is no map!
        meta_search = re.search('gMap.singlePhoto =(.*)', content, re.IGNORECASE)
        if meta_search is None:
            return res

        meta = json.loads(meta_search.group(1).strip(' ;'))

        # the object is not on the map!
        if type(meta) is list:
            return res

        soup = BeautifulSoup(content, 'lxml')

        city = cleanhtml(re.search('Старый город:(.*)', content, re.IGNORECASE).group(1))
        title = soup.find(property='og:title').get('content')
        img_src = soup.find(property='og:image').get('content')

        img_r = requests.get(img_src, stream=True)
        img_name = img_src.split('/')[~0]
        img_path = os.path.join('images', img_name)
        with open(img_path, 'wb') as out_file:
            shutil.copyfileobj(img_r.raw, out_file)

        res['title'] = title
        res['city'] = city
        res['img'] = img_name
        res['latitude'] = meta['latitude']
        res['longitude'] = meta['longitude']
        res['year'] = meta['year']
        res['id'] = idx

    return res


def get_loaded_idx(meta_fname):
    if os.path.isfile(meta_fname):
        df = pd.read_csv(meta_fname)
        return set(df['id'])

    return set()


def write_to_csv(data, meta_fname):
    df = pd.DataFrame(data)

    if os.path.isfile(meta_fname):
        old = pd.read_csv(meta_fname)
        df = pd.concat([old, df])

    df.to_csv(meta_fname, index=False)


def main(start, end, meta_fname):
    preloaded = get_loaded_idx(meta_fname)
    all_idx = set(range(start, end)) - preloaded

    data = defaultdict(list)

    with Pool(processes=10) as p:
        with tqdm.tqdm(total=len(all_idx)) as pbar:
            for i, val in tqdm.tqdm(enumerate(p.imap_unordered(load_page, all_idx))):
                if len(val) > 0:
                    data['title'].append(val['title'])
                    data['city'].append(val['city'])
                    data['img'].append(val['img'])
                    data['latitude'].append(val['latitude'])
                    data['longitude'].append(val['longitude'])
                    data['year'].append(val['year'])
                    data['id'].append(val['id'])

                pbar.update()

    write_to_csv(data, meta_fname)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("--start", help="First index for iteration", default=10000, type=int)
    parser.add_argument("--end", help="Last index for iteration", default=100000, type=int)
    parser.add_argument("--meta_fname", help="Name of the file with metadata", default='metadata.csv', type=str)
    args = parser.parse_args()

    main(args.start, args.end, args.meta_fname)
