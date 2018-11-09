const express = require('express');
const router = express.Router();

var http = require("http");
var fs = require("fs");
// make sure the db instance is open before passing into `Grid`

router.get('/', function(req,res){
  console.log("video connected");

  var filename = './1.mp4';
  var file = fs.createReadStream(filename, {flags: 'r'});

  file.pipe(res);
});

module.exports = router ;
