var http = require("http"),
  httpProxy = require("http-proxy");
var sender = require("./message-service");
var url = require('url');

var proxy = httpProxy.createProxyServer({toProxy: true});
const shippinServiceUri = 'http://motivation-scenario_shipping-service:8088';
//const shippinServiceUri = 'http://localhost:8088';
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
