var express = require('express')
var bodyParser = require('body-parser')
var app = express()
var os = require("os");
var sender = require('./message-service')
const request = require('request-promise-native')

const hostname = os.hostname();
const port = 5000;

let failureCount = 0;

//const shippinServiceUri = 'http://motivation-scenario_shipping-service:8088';
const shippinServiceUri = 'http://localhost:5050';

// parse application/json
app.use(bodyParser.json())

app.post('/sendRequest', function(req, res) {
  sendRequestToShippingService(res);
})

function sendRequestToShippingService(response){
  var options = {
      method: 'GET',
      uri: shippinServiceUri,
      json: true 
  };
   
  request(options)
      .then(function (parsedBody) {
          console.log('Resopnse of Shipping-Service: ' + parsedBody);
          failureCount = 0;
          response.json({response: "Request Success", failureCount: failureCount })
      })
      .catch(function (err) {
        failureCount ++;
          response.json({response: "Request Failed", failureCount: failureCount })
          console.log(err);
      });
}

// start sending messages to queue
 setInterval(sender.sendMessage, 1500);

//serve static file (index.html, images, css)
app.use(express.static(__dirname + '/views'));

app.listen(port, function() {
  console.log('Inventory Service is listening on port ' + port)
})

