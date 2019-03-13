var express = require('express')
var bodyParser = require('body-parser')
var app = express()

const port = 5000;
// parse application/json
app.use(bodyParser.json())

app.get('/', function(req, res) {
  res.send('hello from inventory-service')
})

app.listen(port, function() {
  console.log('Inventory Service is listening on port ' + port)
})