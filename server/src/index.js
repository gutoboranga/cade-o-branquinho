import 'dotenv/config';
import cors from 'cors';
import express from 'express';

var scrapper = require('../scrapper/run.js');

const app = express();

function parse_msg(msg) {
  /* Given a (unparsed) message object from scrapper module, 
      returns it as a JSON with the extra key 'status', 
        indicating whether the message text is positive, negative or undefined
  */ 

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
  return obj;
}

function receive_alerts(alerts, cb) {
  let statuses = [];
  for (let i=0;  i < alerts.length; i++) {
    statuses.push(parse_msg(alerts[i]));
  }
  cb(statuses);
}

app.get('/', (req, res) => {
  res.send({});
});

app.get('/status', (req, res) => {
  scrapper.getAlerts(function(alerts) { 
    receive_alerts(alerts, function(statuses) { 
      res.send({
        "past_statuses": statuses,
        "current_status": statuses[0]['status']
      });
    });
  });
})

app.listen(process.env.PORT, () =>
  console.log(`Server listening on port ${process.env.PORT}!`),
);