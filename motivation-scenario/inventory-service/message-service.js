var amqp = require("amqplib/callback_api");
var os = require("os");

var exports = module.exports = {};

function getVMId() {
  return os.hostname().split("-")[1]
}

function getScalingGroupId(){
  return os.hostname().split("-")[0]
}

exports.sendMessage = function sendMessage() {
  amqp.connect("amqp://rabbitMq", function(err, conn) {
    if(err){
      console.log(err);
    } else {
      conn.createChannel(function(err, ch) {
        var q = "applicationEvents";
        ch.assertQueue(q, { durable: false });
        var data = {
          vmId: getVMId(),
          scalingGroupId: getScalingGroupId(),
          cpu: "0.5"
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
