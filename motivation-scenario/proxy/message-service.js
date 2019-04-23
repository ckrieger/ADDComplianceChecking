var amqp = require("amqplib/callback_api");
var os = require("os");

var exports = module.exports = {};

exports.sendMessage = function sendMessage(serviceId, statusCode) {
  amqp.connect("amqp://rabbitMq", function(err, conn) {
    if(err){
      console.log(err);
    } else {
      conn.createChannel(function(err, ch) {
        var q = "applicationEvents";
        ch.assertQueue(q, { durable: false });
        var data = {
          serviceId: serviceId,
          statusCode: statusCode
        };
        ch.sendToQueue(q, Buffer.from(JSON.stringify(data)));
        console.log(" [x] Sent %s", JSON.stringify(data));
      });

      setTimeout(function() {
        conn.close();
      }, 500);
    }
  });
}

exports.sendVmEventMessage = function sendVmEventMessage(data) {
  amqp.connect("amqp://rabbitMq", function(err, conn) {
    if(err){
      console.log(err);
    } else {
      conn.createChannel(function(err, ch) {
        var q = "applicationEvents";
        ch.assertQueue(q, { durable: false });
        ch.sendToQueue(q, Buffer.from(JSON.stringify(data)));
        console.log(" [x] Sent %s", JSON.stringify(data));
      });

      setTimeout(function() {
        conn.close();
      }, 500);
    }
  });
}