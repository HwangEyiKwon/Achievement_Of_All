const express = require('express');
const router = express.Router();
var User = require('../models/user');
var Content = require('../models/content');
var passport = require('passport');
var jwt = require('jwt-simple'); // jwt token 사용
const passportConfig = require('../../config/passport');

router.get('/getSearchUserData', function (req,res) {
  console.log("getSearchUserData Start");
  User.find(function(err, userList){
    var searchData = new Array();

    for(var i = 0; i < Object.keys(userList).length; i++){
      searchData.push({email: userList[i].email, name: userList[i].name});
    }
    res.send({users: searchData});
  });
});

router.get('/getSearchContentData', function (err, res ) {
  console.log("getSearchContentData Start ");
  Content.collection.distinct("name", function(err, results){
    if(err)  console.log(err);
    else{
      console.log("Content List : " +results);
      res.send({contents: results});
    }
  });
});

// -----------------------------------------------------
module.exports = router ;

