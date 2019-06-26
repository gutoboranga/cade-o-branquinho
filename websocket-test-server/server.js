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
    "{ \"latitude\" : -30.07077, \"longitude\" : -51.11807, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07153, \"longitude\" : -51.11792, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07161, \"longitude\" : -51.11791, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07165, \"longitude\" : -51.1179, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07199, \"longitude\" : -51.11798, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07226, \"longitude\" : -51.11794, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07247, \"longitude\" : -51.1179, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07254, \"longitude\" : -51.11789, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0728, \"longitude\" : -51.11801, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07294, \"longitude\" : -51.11813, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07302, \"longitude\" : -51.11821, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07305, \"longitude\" : -51.11824, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07305, \"longitude\" : -51.11824, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07305, \"longitude\" : -51.11824, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07326, \"longitude\" : -51.11844, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07342, \"longitude\" : -51.11858, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07351, \"longitude\" : -51.1186, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07367, \"longitude\" : -51.11862, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07381, \"longitude\" : -51.11862, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07386, \"longitude\" : -51.11863, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07439, \"longitude\" : -51.11906, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07442, \"longitude\" : -51.11907, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07445, \"longitude\" : -51.11907, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07448, \"longitude\" : -51.11906, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0745, \"longitude\" : -51.11904, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07451, \"longitude\" : -51.11903, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07451, \"longitude\" : -51.11903, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07451, \"longitude\" : -51.11903, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0746, \"longitude\" : -51.11896, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07466, \"longitude\" : -51.11891, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07532, \"longitude\" : -51.1177, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07539, \"longitude\" : -51.1173, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07529, \"longitude\" : -51.11678, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0753, \"longitude\" : -51.11664, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07553, \"longitude\" : -51.11588, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07553, \"longitude\" : -51.11588, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07553, \"longitude\" : -51.11587, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07551, \"longitude\" : -51.11586, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07544, \"longitude\" : -51.1158, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0754, \"longitude\" : -51.11569, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07543, \"longitude\" : -51.11555, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07559, \"longitude\" : -51.11544, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07579, \"longitude\" : -51.11546, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07601, \"longitude\" : -51.11546, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07621, \"longitude\" : -51.11544, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07645, \"longitude\" : -51.11541, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07674, \"longitude\" : -51.11528, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07679, \"longitude\" : -51.11525, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07679, \"longitude\" : -51.11525, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0768, \"longitude\" : -51.11525, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07692, \"longitude\" : -51.11515, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0772, \"longitude\" : -51.11489, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07733, \"longitude\" : -51.11455, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07743, \"longitude\" : -51.11415, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0774, \"longitude\" : -51.1138, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07725, \"longitude\" : -51.11363, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07698, \"longitude\" : -51.11353, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07686, \"longitude\" : -51.11352, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07677, \"longitude\" : -51.11362, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07677, \"longitude\" : -51.11362, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07677, \"longitude\" : -51.11362, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07673, \"longitude\" : -51.11366, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07673, \"longitude\" : -51.11366, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07657, \"longitude\" : -51.11359, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07636, \"longitude\" : -51.11352, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07625, \"longitude\" : -51.11344, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07625, \"longitude\" : -51.11335, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07625, \"longitude\" : -51.11335, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07625, \"longitude\" : -51.11344, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07636, \"longitude\" : -51.11352, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07657, \"longitude\" : -51.11359, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07673, \"longitude\" : -51.11366, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07673, \"longitude\" : -51.11366, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07686, \"longitude\" : -51.11352, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07686, \"longitude\" : -51.11352, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07692, \"longitude\" : -51.11352, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07692, \"longitude\" : -51.11352, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07692, \"longitude\" : -51.11352, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07698, \"longitude\" : -51.11353, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07725, \"longitude\" : -51.11363, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07725, \"longitude\" : -51.11363, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07725, \"longitude\" : -51.11363, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0774, \"longitude\" : -51.1138, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07743, \"longitude\" : -51.11415, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07733, \"longitude\" : -51.11455, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0772, \"longitude\" : -51.11489, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07692, \"longitude\" : -51.11515, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07679, \"longitude\" : -51.11525, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07674, \"longitude\" : -51.11528, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07645, \"longitude\" : -51.11541, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07621, \"longitude\" : -51.11544, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07601, \"longitude\" : -51.11546, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07579, \"longitude\" : -51.11546, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07559, \"longitude\" : -51.11544, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07543, \"longitude\" : -51.11555, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0754, \"longitude\" : -51.11569, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07544, \"longitude\" : -51.1158, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07551, \"longitude\" : -51.11586, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07553, \"longitude\" : -51.11587, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07616, \"longitude\" : -51.11604, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07661, \"longitude\" : -51.11608, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07704, \"longitude\" : -51.11602, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07738, \"longitude\" : -51.11592, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07738, \"longitude\" : -51.11592, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07738, \"longitude\" : -51.11592, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07818, \"longitude\" : -51.11567, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07865, \"longitude\" : -51.1155, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07872, \"longitude\" : -51.1155, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07872, \"longitude\" : -51.1155, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07872, \"longitude\" : -51.1155, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07875, \"longitude\" : -51.11551, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07883, \"longitude\" : -51.11555, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0789, \"longitude\" : -51.11561, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07894, \"longitude\" : -51.1157, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07896, \"longitude\" : -51.11579, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07895, \"longitude\" : -51.11592, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07895, \"longitude\" : -51.11592, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0789, \"longitude\" : -51.11621, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0787, \"longitude\" : -51.11756, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07867, \"longitude\" : -51.11786, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07863, \"longitude\" : -51.11811, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07859, \"longitude\" : -51.11834, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07849, \"longitude\" : -51.11861, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07837, \"longitude\" : -51.11879, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07802, \"longitude\" : -51.11936, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0777, \"longitude\" : -51.11989, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07744, \"longitude\" : -51.12041, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07721, \"longitude\" : -51.12096, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07705, \"longitude\" : -51.12159, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07672, \"longitude\" : -51.12297, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07665, \"longitude\" : -51.12326, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0766, \"longitude\" : -51.12384, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0766, \"longitude\" : -51.12384, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0766, \"longitude\" : -51.12384, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07656, \"longitude\" : -51.12432, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07655, \"longitude\" : -51.12464, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07655, \"longitude\" : -51.12464, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07651, \"longitude\" : -51.12466, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07608, \"longitude\" : -51.12467, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07586, \"longitude\" : -51.12468, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07544, \"longitude\" : -51.12469, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07542, \"longitude\" : -51.12469, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07537, \"longitude\" : -51.12469, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07534, \"longitude\" : -51.12468, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07522, \"longitude\" : -51.12468, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07496, \"longitude\" : -51.1246, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07459, \"longitude\" : -51.1244, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07459, \"longitude\" : -51.1244, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07459, \"longitude\" : -51.1244, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07482, \"longitude\" : -51.12388, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07482, \"longitude\" : -51.12388, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07482, \"longitude\" : -51.12388, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07503, \"longitude\" : -51.12343, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07509, \"longitude\" : -51.12327, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07516, \"longitude\" : -51.12311, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07527, \"longitude\" : -51.12289, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07527, \"longitude\" : -51.12289, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07527, \"longitude\" : -51.12289, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07544, \"longitude\" : -51.1225, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07583, \"longitude\" : -51.12163, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07597, \"longitude\" : -51.12135, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07597, \"longitude\" : -51.12135, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07598, \"longitude\" : -51.12132, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07597, \"longitude\" : -51.12128, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07596, \"longitude\" : -51.12125, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07595, \"longitude\" : -51.12123, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07595, \"longitude\" : -51.12123, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07595, \"longitude\" : -51.12123, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07594, \"longitude\" : -51.12123, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07592, \"longitude\" : -51.12121, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07589, \"longitude\" : -51.1212, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07586, \"longitude\" : -51.1212, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07571, \"longitude\" : -51.12122, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0757, \"longitude\" : -51.12122, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07562, \"longitude\" : -51.12124, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07561, \"longitude\" : -51.12125, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07561, \"longitude\" : -51.12125, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07561, \"longitude\" : -51.12125, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07507, \"longitude\" : -51.12138, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07507, \"longitude\" : -51.12138, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07503, \"longitude\" : -51.12145, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07499, \"longitude\" : -51.12151, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07494, \"longitude\" : -51.12155, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07488, \"longitude\" : -51.12161, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07483, \"longitude\" : -51.12172, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07483, \"longitude\" : -51.12172, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07483, \"longitude\" : -51.12172, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0748, \"longitude\" : -51.1218, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07464, \"longitude\" : -51.12214, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07443, \"longitude\" : -51.12257, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07427, \"longitude\" : -51.12289, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07405, \"longitude\" : -51.12331, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0739, \"longitude\" : -51.12357, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0739, \"longitude\" : -51.12357, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07379, \"longitude\" : -51.12367, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07365, \"longitude\" : -51.12373, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07351, \"longitude\" : -51.12374, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07313, \"longitude\" : -51.12362, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.073, \"longitude\" : -51.12354, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0729, \"longitude\" : -51.12337, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07287, \"longitude\" : -51.12331, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07287, \"longitude\" : -51.12331, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07287, \"longitude\" : -51.12331, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07285, \"longitude\" : -51.12326, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0727, \"longitude\" : -51.12294, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07252, \"longitude\" : -51.1227, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07229, \"longitude\" : -51.12253, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07187, \"longitude\" : -51.12232, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07146, \"longitude\" : -51.12209, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07125, \"longitude\" : -51.12195, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07097, \"longitude\" : -51.12173, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07076, \"longitude\" : -51.12155, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07057, \"longitude\" : -51.12135, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07043, \"longitude\" : -51.12128, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07035, \"longitude\" : -51.12124, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07022, \"longitude\" : -51.1212, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06992, \"longitude\" : -51.1212, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06973, \"longitude\" : -51.12129, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06951, \"longitude\" : -51.12151, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06918, \"longitude\" : -51.12205, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06903, \"longitude\" : -51.12222, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06884, \"longitude\" : -51.12236, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06859, \"longitude\" : -51.12245, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06833, \"longitude\" : -51.12249, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06809, \"longitude\" : -51.12246, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06778, \"longitude\" : -51.12233, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06745, \"longitude\" : -51.12207, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0669, \"longitude\" : -51.12164, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06686, \"longitude\" : -51.12159, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06679, \"longitude\" : -51.12147, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06676, \"longitude\" : -51.12134, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06675, \"longitude\" : -51.12113, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06679, \"longitude\" : -51.12088, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.0672, \"longitude\" : -51.11981, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06739, \"longitude\" : -51.11932, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06766, \"longitude\" : -51.11896, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06787, \"longitude\" : -51.11877, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06827, \"longitude\" : -51.11854, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06863, \"longitude\" : -51.11843, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06929, \"longitude\" : -51.11832, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.06985, \"longitude\" : -51.11824, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07072, \"longitude\" : -51.11807, \"bearing\" : 0.0 }",
    "{ \"latitude\" : -30.07072, \"longitude\" : -51.11807, \"bearing\" : 0.0 }"
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
}, 500);
