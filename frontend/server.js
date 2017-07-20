'use strict';

const fs = require('fs');
const express = require('express');
const bs = require('browser-sync').create();
const browserify = require('browserify');

const app = express();
const b = browserify();

const targets = [
  "app/index.html",
  "app/assets/stylesheets/app.css",
  "src/**/*.js"
];
const proxiedPort = 9011;
const basePort = 9020;

b.add('src/app.js');

app.use(express.static('app'));
app.listen(basePort);

bs.init({
  proxy: `localhost:${basePort}`,
  port: proxiedPort,
  files: targets
});

bs.watch(targets).on('change', () => {
  b.bundle().pipe(fs.createWriteStream('app/assets/javascripts/app.js'));
  bs.reload();
});
