var amqp = require("amqplib/callback_api");
var os = require("os");
const log = require('simple-node-logger').createSimpleFileLogger(`loggingvolume/${os.hostname()}.log`);

var exports = module.exports = {};

function getVMId() {
  return os.hostname().split("-")[1]
}

function getScalingGroupId(){
  return os.hostname().split("-")[0]
}

exports.logContainerInfo = function logContainerInfo() {
  var data = {
    vmId: getVMId(),
    scalingGroupId: getScalingGroupId(),
    cpu: "0.5"
  };
  log.info(data);
}
