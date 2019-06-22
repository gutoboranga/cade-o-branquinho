const express = require('express');
const expressWebSocket = require('express-ws');
const websocketStream = require('websocket-stream/stream');
const app = express();
 
var providerWS = null
var consumerWS = {}
var lastConsumerId = 0
 
// extend express app with app.ws()
expressWebSocket(app, null, {
    // ws options here
    perMessageDeflate: false,
});

app.get('/', function(req, res) {
    res.sendFile(__dirname + '/index.html');
});

// to connect
app.ws('/websocket', function(ws, req) {
    let id = makeConsumerId()
    
    consumerWS[id] = ws
    console.log("> Consumer " + id + " joined.\tTOTAL CONSUMERS: " + Object.keys(consumerWS).length);
    
    ws.onclose = function (e) {
        delete consumerWS[id];
        console.log("> Consumer " + id + " left.\tTOTAL CONSUMERS: " + Object.keys(consumerWS).length);
    }
    
});

app.ws('/websocket-provider', function(ws, req) {
    console.log(">>> Provider connected");
    providerWS = ws
    
    providerWS.onmessage = function (e) {
        broadcast(e.data);
    };
    
    providerWS.onclose = function (e) {
        console.log(">>> Provider disconnected");
        providerWS = null
    };
});
 
app.listen(3000);
console.log("Running on port 3000");

function makeConsumerId() {
    lastConsumerId += 1;
    return lastConsumerId
}

function broadcast(message) {
    for (var key in consumerWS) {
        consumerWS[key].send(message)
    }
}
