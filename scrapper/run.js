var XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest;
var getElement = require('get-element');
var DOMParser = require('xmldom').DOMParser;

let URL = "https://www.ufrgs.br/bibiph/categoria/onibus-circular/"

function getAlerts(callback) {
  var httpRequest = new XMLHttpRequest();
  
  httpRequest.onreadystatechange = function() {
    if (httpRequest.readyState == 4) {
      
      var DomParser = require('dom-parser');
      var parser = new DomParser();
      
      var dom = parser.parseFromString(httpRequest.responseText);
     
      alerts = scrape(dom)
      callback(alerts)
    }
  };

  httpRequest.open('GET', URL, true);
  httpRequest.send();
}

function scrape(dom) {
    alerts = []

    try {
        let items = dom.getElementsByClassName('kad_blog_item')
        
        for (var i = 0; i < items.length; i++) {
            
            let postContent = items[i].getElementsByClassName('postcontent')[0]
            
            // get text
            let header = postContent.getElementsByTagName('header')[0]
            let a = header.getElementsByTagName('a')[0]
            let text = a.getElementsByTagName('h5')[0].innerHTML.trim()
            
            console.log(text);
        
            // get date
            let footer = postContent.getElementsByTagName('footer')[0]
            let date = footer.getElementsByClassName('postdate')[0].innerHTML.trim()
        
            // create alert
            let alert = {}
            alert.text = text;
            alert.date = date;
        
            // add to list
            alerts.push(JSON.stringify(alert))
        }
    } catch (e) {
        console.log("Error parsing the data");
        console.log(e);
    } finally {
        console.log("Finally: will post result, I guess");
        return alerts;
    }
}

getAlerts(function(alerts) {
    console.log(alerts);
    
    // const fs = require('fs');
    // fs.writeFile("test.txt", "[" + alerts + "]", function(err) {
    //     if(err) {
    //         return console.log(err);
    //     }
    //
    //     console.log("The file was saved!");
    // });
})
