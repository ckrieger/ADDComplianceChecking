var express = require('express')
var bodyParser = require('body-parser')
var app = express()
var os = require("os");
var sender = require('./message-service')

const hostname = os.hostname();
const port = 5000;
// parse application/json
app.use(bodyParser.json())

app.get('/', function(req, res) {
  res.send('hello from inventory-service with hostname: ' + hostname)
})

// start sending messages to queue
setInterval(sender.sendMessage, 1500);

app.listen(port, function() {
  console.log('Inventory Service is listening on port ' + port)
})

