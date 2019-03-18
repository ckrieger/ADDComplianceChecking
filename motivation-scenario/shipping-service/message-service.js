var amqp = require("amqplib/callback_api");
var os = require("os");

var exports = module.exports = {};

exports.sendMessage = function sendMessage() {
  amqp.connect("amqp://rabbitMq", function(err, conn) {
    if(err){
      console.log(err);
    } else {
      conn.createChannel(function(err, ch) {
        var q = "hello";
        ch.assertQueue(q, { durable: false });
        var data = {
          vmid: os.hostname(),
          cpu: "0.5"
        };
        ch.sendToQueue(q, Buffer.from(JSON.stringify(data)));
        console.log(" [x] Sent %s", JSON.stringify(data));
      });
      setTimeout(function() {
        conn.close();
        //process.exit(0);
      }, 500);
    }
  });
}

// export.startInterval = function startInterval() {
  
// }