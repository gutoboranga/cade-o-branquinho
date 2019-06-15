import 'dotenv/config';
import cors from 'cors';
import express from 'express';

var scrapper = require('../scrapper/run.js');

const app = express();

app.get('/', (req, res) => {
  res.send({});
});

app.get('/status', (req, res) => {
  res.send({
    'status': true ? 'on' : 'off'
  })
})

app.listen(process.env.PORT, () =>
  console.log(`Server listening on port ${process.env.PORT}!`),
);