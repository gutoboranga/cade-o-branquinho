import 'dotenv/config';
import cors from 'cors';
import express from 'express';

var scrapper = require('../scrapper/run.js');

const app = express();

function receive_alerts(alerts) {
  let statuses = [];
  for (let i=0;  i < alerts.length; i++) {
    let msg = alerts[i];

    let has_not = msg.toLowerCase().search("não") >= 0;
    let has_circula = msg.toLowerCase().search("circula") >= 0;
    let has_manutencao = msg.toLowerCase().search("manutenção") >= 0;
    let has_normalmente = msg.toLowerCase().search("normalmente") >= 0;

    let off = (has_manutencao) || (has_not && has_circula);
    let on = (has_circula && (has_normalmente || !has_not));
    if (on && off) {
      console.log("We have a on-and-off problem");
    }

    let obj = JSON.parse(msg);
    if (on) {
      obj["status"] = "on";
    }
    else if (off) {
      obj["status"] = "off";
    }
    else {
      obj["status"] = "undefined";
    }
    statuses.push(obj);
  }
  console.log(statuses);
}

app.get('/', (req, res) => {
  res.send({});
});

app.get('/status', (req, res) => {
  scrapper.getAlerts(receive_alerts); // how to get the results?

  res.send({
    'status': true ? 'on' : 'off'
  })
})

app.listen(process.env.PORT, () =>
  console.log(`Server listening on port ${process.env.PORT}!`),
);