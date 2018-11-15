const express = require('express');
const router = express.Router();
var Content = require('../models/content')
var User = require('../models/user');

router.get('/contentJoin/:contentName', function (req,res) {
  Content.find({contentName : req.params.contentName, isDone: 2}, function(err, contentList) {
    var contentCount = Object.keys(contentList).length;
    var startDate = new Array();
    var endDate = new Array();
    var contentIdArray = new Array();

    for(var i = 0; i < contentCount; i++){
      startDate[i] = contentList[i].startDate;
      endDate[i] = contentList[i].endDate;
      contentIdArray[i] = contentList[i].contentId;
    }

    res.send({startDate, endDate, contentId});
  });
});

/*
//jwt토큰으로 사용자 파악,(decode encode)
router.get('/contentJoin/:contentID',  function (req,res) {
  //User.findOne({email: req.body.email}, function(err, user){
  Content.findOne({id : req.body.id, name : req.body.name}, function(err, content){


    //이건 어떻게 해야 할까??
    //요청이 오면 해당하는 컨텐츠에 대해 기간을 보내준다???
    // 다시 얘기 .
    res.send(content.startDate, content.endDate);


  });
})
*/

//컨텐츠 아이디or네임으로 유저의 해당되는 컨텐츠 리스트를 알아야 함(새로운 컨텐츠 리스트를 추가해야 됨, 푸시만 하면)
router.get('/contentJoinComplete/:contentId/:jwtToken',  function (req,res) {
  console.log("contentJoinComplete jwt토큰 "+ req.body.jwtToken);
  var decoded = jwt.decode(req.body.token,req.app.get("jwtTokenSecret"));
  console.log("contentJoinComplete jwt토큰 디코딩 "+ decoded.userCheck);
  var userEmail = decoded.userCheck;
  var contentId = req.params.contentId;
  Content.findOne({contentId: contentId}, function(err, content){
    User.findOne({email: userEmail,}, function(err, user){
      //user의 content List에 해당 content 정보들 추가
      user.contentList.contentId = content.contentId;
      user.contentList.contentName = content.contentName;
      user.contentList.joinState = 0;

      var year = content.startDate.getFullYear();
      var month = content.startDate.getMonth() + 1;
      var day = content.startDate.getDate();
      // 일이 한자리 수인 경우 앞에 0을 붙여주기 위해
      if ((day+"").length < 2) {
        day = "0" + day;
      }
      var date = year+ "-" + month + "-" + day;
      user.contentList.authenticationDate = date;
      
      //content의 user List에 해당 user 추가
      var userIndex = content.userList.length;
      content.userList[userIndex] = user.email;
    });
  });
});

module.exports = router ;
