var sender = require("./message-service");
var watch = require('node-watch');
const readline = require('readline');
const fs = require('fs');

let lastReadLineByFile = new Map();
let fileList = ["circuit-breaker-violation"];

fileList.forEach(filename => readAndSendLog(filename));
  
function readAndSendLog(filename) {
    let line_no = 0;
    let rl = readline.createInterface({
      input: fs.createReadStream(`../${filename}.log`)
    });
    // init file in map if not exist already
    if (!lastReadLineByFile.has(filename)){
      lastReadLineByFile.set(filename, 0)
    }

    rl.on('line', function(line) {
        line_no++;
        if(line_no > lastReadLineByFile.get(filename)) {
          var data = line.split(" ")[3]
          sendMessage(JSON.parse(data));
        }
    });

    rl.on('close', function(line) {
      lastReadLineByFile.set(filename, line_no);
      console.log(lastReadLineByFile);
    });
  }
 
  async function sendMessage(data) {
    await sleep(1000)
    sender.sendMessageToRabbitMQ(data);
  }

  function sleep(ms){
    return new Promise(resolve=>{
        setTimeout(resolve,ms)
    })
}
 

