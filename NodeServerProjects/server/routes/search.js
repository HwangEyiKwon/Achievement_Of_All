const express = require('express');
const router = express.Router();
var User = require('../models/user');
var Content = require('../models/content');

router.get('/getSearchUserData', function (req,res) {
  console.log("getSearchUserData Start");
  var searchData = new Array();
  User.find(function(err, info){
    console.log("search user data" + info);

    var searchData = {
      users: [],
    }

    for(var i in info){
      searchData.users.push(info[i].name);
    }
    res.send(searchData);
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

module.exports = router ;

