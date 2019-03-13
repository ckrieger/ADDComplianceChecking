var express = require('express')
var bodyParser = require('body-parser')
var app = express()

const port = 8088;
// parse application/json
app.use(bodyParser.json())

app.get('/', function(req, res) {
  res.send('hello from shipping-service')
})

app.listen(port, function() {
  console.log('Shipping Service is listening on port ' + port)
})