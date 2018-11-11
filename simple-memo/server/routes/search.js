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




router.get('/getContentList', function(err, res){
  var userEmail = req.body.email;
  User.findOne({email: userEmail}, function(err, user){
    console.log("user's Content List: " +user.contentList);

    res.send(user.contentList);
  });
});

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

  User.findOne({name : name}, function (err, user) {

    console.log("send search name : " +user.name);
    res.send(user.name);

  });
})

router.get('./enterContent/:contentID', function(req, res){
  User.findOne({email : req.body.email, "contentList.contentId" : req.body.contentId}, function (err, user {
    
    //contentList 몇번째를 가져올지 결정해야 함
    console.log("content : " +user.contentList);
    res.send(user.contentList);

  });
})

router.get('./enterUser/:userID', function(req, res){
  User.findOne({email : req.body.email}, function(err, user){

    //return 값이 이게 맞나?
    res.send(user);
  })
})

module.exports = router ;

