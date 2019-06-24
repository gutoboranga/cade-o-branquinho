const express = require('express');
const expressWebSocket = require('express-ws');
const websocketStream = require('websocket-stream/stream');
const app = express();
 
var providerWS = null
var consumerWS = {}
var lastConsumerId = 0

// temporario
var currentDataSent = 0
let data = [
    "{ \"latitude\" : -29.75895163, \"longitude\" : -50.01396854, \"direction\": 180.00162210799738, \"test\" : -0.10194063580456714, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -29.75895163, \"longitude\" : -50.01396854, \"direction\": 180.00162210799738, \"test\" : -0.10194063580456714, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -29.7589551, \"longitude\" : -50.01395817, \"direction\": 179.99949135162817, \"test\" : -0.10143198743273274, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -29.7589551, \"longitude\" : -50.01395817, \"direction\": 179.99949135162817, \"test\" : -0.10143198743273274, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -29.7589563, \"longitude\" : -50.01394795, \"direction\": 179.99949870726704, \"test\" : -0.10093069469976967, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -29.7589563, \"longitude\" : -50.01394795, \"direction\": 179.99949870726704, \"test\" : -0.10093069469976967, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -29.75896409, \"longitude\" : -50.0139383, \"direction\": 179.9995266623012, \"test\" : -0.1004573570009768, \"bearing\" : 302.0 }",
    "{ \"latitude\" : -29.75896434, \"longitude\" : -50.01391815, \"direction\": 179.99901162546044, \"test\" : -0.099468982461417, \"bearing\" : 9.1 }",
    "{ \"latitude\" : -29.75896171, \"longitude\" : -50.01392709, \"direction\": 180.00043851366885, \"test\" : -0.09990749613027106, \"bearing\" : 298.5 }",
    "{ \"latitude\" : -29.75896471, \"longitude\" : -50.01392046, \"direction\": 179.99967479339207, \"test\" : -0.09958228952234549, \"bearing\" : 304.1 }",
    "{ \"latitude\" : -29.75896325, \"longitude\" : -50.01392679, \"direction\": 180.00031049171577, \"test\" : -0.09989278123811118, \"bearing\" : 283.7 }",
    "{ \"latitude\" : -29.75895847, \"longitude\" : -50.01393462, \"direction\": 180.0003840659625, \"test\" : -0.10027684720060392, \"bearing\" : 299.8 }",
    "{ \"latitude\" : -29.75895267, \"longitude\" : -50.01394144, \"direction\": 180.00033452200347, \"test\" : -0.10061136920407421, \"bearing\" : 299.3 }",
    "{ \"latitude\" : -29.75894965, \"longitude\" : -50.01394987, \"direction\": 180.00041348982157, \"test\" : -0.1010248590256424, \"bearing\" : 297.6 }",
    "{ \"latitude\" : -29.75894778, \"longitude\" : -50.01395883, \"direction\": 180.0004394844907, \"test\" : -0.1014643435163407, \"bearing\" : 290.0 }",
    "{ \"latitude\" : -29.75895095, \"longitude\" : -50.01396732, \"direction\": 180.00041643168782, \"test\" : -0.10188077520416527, \"bearing\" : 265.7 }",
    "{ \"latitude\" : -29.75895734, \"longitude\" : -50.01397225, \"direction\": 180.0002418167702, \"test\" : -0.10212259197436424, \"bearing\" : 240.8 }",
    "{ \"latitude\" : -29.75896637, \"longitude\" : -50.01397521, \"direction\": 180.00014518999185, \"test\" : -0.10226778196621922, \"bearing\" : 227.6 }",
    "{ \"latitude\" : -29.758978, \"longitude\" : -50.0139762, \"direction\": 180.00004856098485, \"test\" : -0.10231634295107028, \"bearing\" : 217.0 }",
    "{ \"latitude\" : -29.75898975, \"longitude\" : -50.0139789, \"direction\": 180.00013244156185, \"test\" : -0.10244878451291584, \"bearing\" : 216.7 }",
    "{ \"latitude\" : -29.75900351, \"longitude\" : -50.01398065, \"direction\": 180.00008584353617, \"test\" : -0.10253462804908509, \"bearing\" : 200.6 }",
    "{ \"latitude\" : -29.75901687, \"longitude\" : -50.01398228, \"direction\": 180.00007995888055, \"test\" : -0.10261458692963288, \"bearing\" : 206.1 }",
    "{ \"latitude\" : -29.75902455, \"longitude\" : -50.01398419, \"direction\": 180.0000936957293, \"test\" : -0.10270828265893783, \"bearing\" : 200.4 }",
    "{ \"latitude\" : -29.75903177, \"longitude\" : -50.01398385, \"direction\": 179.99998332097772, \"test\" : -0.10269160363665719, \"bearing\" : 198.4 }",
    "{ \"latitude\" : -29.75903665, \"longitude\" : -50.01397923, \"direction\": 179.9997733593137, \"test\" : -0.10246496295036422, \"bearing\" : 178.4 }",
    "{ \"latitude\" : -29.75904307, \"longitude\" : -50.013973, \"direction\": 179.99969437564997, \"test\" : -0.10215933860033033, \"bearing\" : 187.2 }",
    "{ \"latitude\" : -29.7590511, \"longitude\" : -50.01396834, \"direction\": 179.99977139225464, \"test\" : -0.10193073085497417, \"bearing\" : 194.9 }",
    "{ \"latitude\" : -29.7590604, \"longitude\" : -50.01396515, \"direction\": 179.99984350450728, \"test\" : -0.1017742353622566, \"bearing\" : 199.8 }",
    "{ \"latitude\" : -29.75907151, \"longitude\" : -50.01395695, \"direction\": 179.99959771649657, \"test\" : -0.1013719518588232, \"bearing\" : 181.7 }",
    "{ \"latitude\" : -29.75908167, \"longitude\" : -50.01394917, \"direction\": 179.99961831468536, \"test\" : -0.10099026654418708, \"bearing\" : 168.7 }",
    "{ \"latitude\" : -29.75909047, \"longitude\" : -50.01394391, \"direction\": 179.99974194144627, \"test\" : -0.10073220799046112, \"bearing\" : 161.8 }",
    "{ \"latitude\" : -29.75910007, \"longitude\" : -50.01393904, \"direction\": 179.99976107149016, \"test\" : -0.10049327948061659, \"bearing\" : 174.2 }",
    "{ \"latitude\" : -29.75911, \"longitude\" : -50.01393608, \"direction\": 179.99985477626166, \"test\" : -0.10034805574227335, \"bearing\" : 181.9 }",
    "{ \"latitude\" : -29.75911998, \"longitude\" : -50.0139352, \"direction\": 179.9999568246772, \"test\" : -0.10030488041948615, \"bearing\" : 186.9 }",
    "{ \"latitude\" : -29.75913003, \"longitude\" : -50.01393584, \"direction\": 180.00003140074543, \"test\" : -0.100336281164914, \"bearing\" : 193.5 }",
    "{ \"latitude\" : -29.7591386, \"longitude\" : -50.01393757, \"direction\": 180.00008488141796, \"test\" : -0.10042116258287592, \"bearing\" : 198.0 }",
    "{ \"latitude\" : -29.75914806, \"longitude\" : -50.01393906, \"direction\": 180.000073107033, \"test\" : -0.10049426961586505, \"bearing\" : 199.3 }",
    "{ \"latitude\" : -29.75915774, \"longitude\" : -50.01394137, \"direction\": 180.00011334219573, \"test\" : -0.10060761181159705, \"bearing\" : 192.6 }",
    "{ \"latitude\" : -29.75916609, \"longitude\" : -50.0139413, \"direction\": 179.99999656533797, \"test\" : -0.10060417714956316, \"bearing\" : 186.8 }",
    "{ \"latitude\" : -29.75917552, \"longitude\" : -50.01394207, \"direction\": 180.00003778182977, \"test\" : -0.10064195897933814, \"bearing\" : 188.1 }",
    "{ \"latitude\" : -29.75918618, \"longitude\" : -50.01394627, \"direction\": 180.00020608607767, \"test\" : -0.10084804505700617, \"bearing\" : 194.6 }",
    "{ \"latitude\" : -29.75919758, \"longitude\" : -50.01395074, \"direction\": 180.00021933840185, \"test\" : -0.10106738345885447, \"bearing\" : 199.2 }",
    "{ \"latitude\" : -29.75920418, \"longitude\" : -50.01395528, \"direction\": 180.0002227764489, \"test\" : -0.10129015990776224, \"bearing\" : 199.5 }",
    "{ \"latitude\" : -29.75921122, \"longitude\" : -50.01396015, \"direction\": 180.00023897210082, \"test\" : -0.10152913200857938, \"bearing\" : 198.5 }",
    "{ \"latitude\" : -29.75922006, \"longitude\" : -50.01396559, \"direction\": 180.00026694559907, \"test\" : -0.10179607760764497, \"bearing\" : 197.3 }",
    "{ \"latitude\" : -29.75922796, \"longitude\" : -50.01397363, \"direction\": 180.00039453524195, \"test\" : -0.10219061284959707, \"bearing\" : 215.8 }",
    "{ \"latitude\" : -29.75923274, \"longitude\" : -50.01398449, \"direction\": 180.00053292243288, \"test\" : -0.1027235352824789, \"bearing\" : 239.7 }",
    "{ \"latitude\" : -29.75923733, \"longitude\" : -50.01399912, \"direction\": 180.00071792950777, \"test\" : -0.10344146479025085, \"bearing\" : 254.1 }",
    "{ \"latitude\" : -29.75924036, \"longitude\" : -50.01401437, \"direction\": 180.00074835899656, \"test\" : -0.10418982378681108, \"bearing\" : 251.2 }",
    "{ \"latitude\" : -29.75924329, \"longitude\" : -50.01402656, \"direction\": 180.00059819936254, \"test\" : -0.10478802314935365, \"bearing\" : 244.1 }",
    "{ \"latitude\" : -29.75924661, \"longitude\" : -50.01403817, \"direction\": 180.00056973994558, \"test\" : -0.10535776309492917, \"bearing\" : 247.6 }",
    "{ \"latitude\" : -29.75925135, \"longitude\" : -50.01404956, \"direction\": 180.00055894752506, \"test\" : -0.10591671061999364, \"bearing\" : 240.4 }",
    "{ \"latitude\" : -29.75925161, \"longitude\" : -50.01406292, \"direction\": 180.00065562494092, \"test\" : -0.10657233556091228, \"bearing\" : 251.2 }",
    "{ \"latitude\" : -29.75925076, \"longitude\" : -50.01407577, \"direction\": 180.00063059701512, \"test\" : -0.10720293257602975, \"bearing\" : 258.7 }",
    "{ \"latitude\" : -29.7592471, \"longitude\" : -50.01408961, \"direction\": 180.00067917742203, \"test\" : -0.1078821099980587, \"bearing\" : 276.8 }",
    "{ \"latitude\" : -29.75924218, \"longitude\" : -50.01410424, \"direction\": 180.00071794046454, \"test\" : -0.10860005046259857, \"bearing\" : 281.1 }",
    "{ \"latitude\" : -29.75922498, \"longitude\" : -50.01412294, \"direction\": 180.0009176513868, \"test\" : -0.10951770184939846, \"bearing\" : 322.1 }",
    "{ \"latitude\" : -29.75919912, \"longitude\" : -50.01413932, \"direction\": 180.0008037753848, \"test\" : -0.11032147723420849, \"bearing\" : 336.9 }",
    "{ \"latitude\" : -29.75917334, \"longitude\" : -50.01415133, \"direction\": 180.00058931243785, \"test\" : -0.11091078967206158, \"bearing\" : 342.0 }",
    "{ \"latitude\" : -29.75915064, \"longitude\" : -50.01416336, \"direction\": 180.000590270642, \"test\" : -0.11150106031405471, \"bearing\" : 333.8 }",
    "{ \"latitude\" : -29.75913295, \"longitude\" : -50.01417906, \"direction\": 180.00077031977506, \"test\" : -0.11227138008911197, \"bearing\" : 322.2 }",
    "{ \"latitude\" : -29.75911702, \"longitude\" : -50.0141947, \"direction\": 180.00076735498558, \"test\" : -0.11303873507469575, \"bearing\" : 324.1 }"
]

 
// extend express app with app.ws()
expressWebSocket(app, null, {
    // ws options here
    perMessageDeflate: false,
});

app.set('port', (process.env.PORT || 5000));
app.listen(app.get('port'), function() {
  var port = app.get('port').toString();
  console.log("Running on port " + port);
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
 
function makeConsumerId() {
    lastConsumerId += 1;
    return lastConsumerId
}

function broadcast(message) {
    for (var key in consumerWS) {
        consumerWS[key].send(message)
    }
}

setInterval(function() {
    broadcast(data[currentDataSent]);
    currentDataSent += 1;
    if (currentDataSent >= data.length) {
        currentDataSent = 0
    }
}, 200);

