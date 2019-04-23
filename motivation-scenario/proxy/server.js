var http = require("http"),
  httpProxy = require("http-proxy");
var sender = require("./message-service");
var url = require('url');
var watch = require('node-watch');
const readline = require('readline');
const fs = require('fs');

var proxy = httpProxy.createProxyServer({toProxy: true});
const shippinServiceUri = 'http://motivation-scenario_shipping-service:8088';
let lastReadLineByFile = new Map();

proxy.on("error", function(err, req, res) {
  res.writeHead(500, {
    "Content-Type": "text/plain"
  });
  console.log("error");
  sender.sendMessage(req.headers.origin, "failed")
  res.end("Something went wrong. And we are reporting a custom error message.");
});



proxy.on("proxyRes", function(proxyRes, req, res) {
  sender.sendMessage(req.headers.origin, "success")
});

var server = http.createServer(function(req, res, options) {
  console.log("request from " + req.headers);
  proxy.web(req, res, { target: shippinServiceUri });
});

console.log("listening on port 5050");
server.listen(5050);

watch('./loggingvolume', function(evt, filename) {
  if (evt == 'update') {
    let line_no = 0;
    let rl = readline.createInterface({
      input: fs.createReadStream(filename)
    });
    // init file in map if not exist already
    if (!lastReadLineByFile.has(filename)){
      lastReadLineByFile.set(filename, 0)
    }

    rl.on('line', function(line) {
        line_no++;
        if(line_no > lastReadLineByFile.get(filename)) {
          var data = line.split(" ")[3]
          console.log(data);
          sender.sendVmEventMessage(JSON.parse(data));
        }
    });

    rl.on('close', function(line) {
      lastReadLineByFile.set(filename, line_no);
      console.log(lastReadLineByFile);
    });
  }
 
  if (evt == 'remove') {
    // on delete
  }
 
});

