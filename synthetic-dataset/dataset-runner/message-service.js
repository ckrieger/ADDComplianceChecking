var amqp = require("amqplib/callback_api");
var exports = module.exports = {};

exports.sendMessageToRabbitMQ = function sendMessageToRabbitMQ(data) {
  amqp.connect("amqp://localhost", function(err, conn) {
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