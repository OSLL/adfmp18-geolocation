import pandas as pd

from bottle import run, post, request, response, get, route
import base64

import json


data = pd.read_csv('images_meta.csv')


@route('/',method = 'GET')
def process():
    meta = data.sample(1)

    with open(meta['fname'], 'rb') as img:
        image_read = img.read()
        base64_bytes = base64.b64encode(image_read)
        base64_string = base64_bytes.decode('utf-8')

        return json.dumps({'image_base64': base64_string, 'name': data['name'], 'geo':{'lat':data['lat'], 'lon':data['lon']}})        

if __name__ == '__main__':
    run(host='localhost', port=8080, debug=True)