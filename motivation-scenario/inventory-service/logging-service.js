var amqp = require("amqplib/callback_api");
var os = require("os");
var osUtils = require("os-utils");
const log = require('simple-node-logger').createSimpleFileLogger(`loggingvolume/${os.hostname()}.log`);

var exports = module.exports = {};

function getVMId() {
  return os.hostname().split("-")[1]
}

function getScalingGroupId(){
  return os.hostname().split("-")[0]
}

exports.logContainerInfo = function logContainerInfo() {
  osUtils.cpuUsage(function(value){
    var data = {
      vmId: getVMId(),
      scalingGroupId: getScalingGroupId(),
      cpu: value
    };
    log.info(data);
  });
}

exports.logHttpResponseStatus = function logHttpResponseStatus(statusCode) {
  var data = {
    serviceId: os.hostname(),
    statusCode: statusCode
  };
  log.info(data);
}
