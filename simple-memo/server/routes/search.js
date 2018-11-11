const express = require('express');
const router = express.Router();
var User = require('../models/user');
var Content = require('../models/content');



// content.find({},{"_id":false,
//   "startDate" : false,
//   "roomNum" : false,
//   "endDate" : false,
//   "achievementRate" : false,
//   "userList" : false,
//   "isDone" : false,
//   "__v" : false,
//
// },function (err,data) {
//
//     console.log(data);
//
// });// 모든 content data 에대해서 id 만 출력.


router.post('/getAllContentList', function (err, res, ) {
  Content.collection.distinct("id", function(err, results){
    if(err)  console.log(err);
    else{
      console.log("Content List : " +results);
      res.send(results);
    }
  });
});

router.post('/getAllUserList', function (req, res) {

  var name = req.body.name;

  User.findOne({name : name}), function (err, user) {

    console.log("send search name : " +user.name);
    res.send(user.name);

  };
})




module.exports = router ;

