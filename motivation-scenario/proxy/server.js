var http = require("http"),
  httpProxy = require("http-proxy");

  
var proxy = httpProxy.createProxyServer({});


proxy.on("error", function(err, req, res) {
  res.writeHead(500, {
    "Content-Type": "text/plain"
  });
  console.log("error");
  res.end("Something went wrong. And we are reporting a custom error message.");
});

proxy.on("proxyRes", function(proxyRes, req, res) {
  console.log(
    "RAW Response from the target",
    JSON.stringify(proxyRes.headers, true, 2)
  );
});

var server = http.createServer(function(req, res) {
  console.log("request from " + req.headers.host);
  proxy.web(req, res, { target: "http://localhost:8088" });
});

console.log("listening on port 5050");
server.listen(5050);
