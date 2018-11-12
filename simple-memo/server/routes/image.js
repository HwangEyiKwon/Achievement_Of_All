const express = require('express');
const router = express.Router();

var User = require('../models/user');

var fs = require("fs");

router.get('/', function(req, res){
  var filename = './userImage.jpg';
  var file = fs.createReadStream(filename, {flags: 'r'});

  file.pipe(res);
})
