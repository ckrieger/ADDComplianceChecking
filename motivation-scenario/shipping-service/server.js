var express = require('express')
var bodyParser = require('body-parser')
var app = express()
var os = require("os");
var loggingService = require('./logging-service')

const port = 8088;
// parse application/json
app.use(bodyParser.json())

app.get('/', function(req, res) {
  res.send('hello from shipping-service with hostname ' + os.hostname())
})

// start sending messages to queue
setInterval(loggingService.logContainerInfo, 1500);

app.listen(port, function() {
  console.log('Shipping Service is listening on port ' + port)
})