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



//jwt 토큰
router.get('/getContentList', function(err, res){
  var userEmail = req.body.email;
  User.findOne({email: userEmail}, function(err, user){
    console.log("user's Content List: " +user.contentList);

    res.send(user.contentList);
  });
});

/*
//컨텐츠의 id를 unique하게 뽑아냄
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
*/

//content id(or name)로 content list중에 해당 애를 뽑아서 content정보를 줘야함
//jwt토큰 사용 -> post로 변경
//만약 user의 정보가 필요 없이, content id로만 구분이 가능하면 user에서 find를 할 필요가 없음!!!!!
router.get('./enterContent/:contentID', function(req, res){
  var contentId = req.body.contentId;
  User.findOne({email : req.body.email, "contentList.contentId" : req.body.contentId}, function (err, user) {
    //content db를 접근, id+name(id로만 구분하면 id로)로 해당 컨텐츠 정보를 찾기.
    //그 후 전송
    Content.findOne({id: "contentList.contentId"})
    //contentList 몇번째를 가져올지 결정해야 함
    //res.send(contentInfo);

  });
})

//jwt 토큰 이용, user의 email을 비교해서 contentlist 던저주거나. --> 없애는 방향
//만약 client에서 토큰 접근이 불가능할 경우에는, email/nickname을 통해 판단한다.
router.get('./enterUser/:userID', function(req, res){
  var userEmail = req.body.email
  User.findOne({email : userEmail}, function(err, user){

    res.send(user.contentList);
  })
})

module.exports = router ;

