const express = require('express');
const router = express.Router();
var jwt = require('jwt-simple'); // jwt token 사용
var Content = require('../models/content')
var User = require('../models/user');
var mkdirp = require('mkdirp'); // directory 만드는것


router.get('/contentJoin/:contentName', function (req,res) {
  console.log("contentJoin Start");
  var contentName = req.params.contentName;

  Content.find({name : contentName, isDone: 2}, function(err, contentList) {
    var contentCount = Object.keys(contentList).length;
    var startDate = new Array();

    //var endDate = new Array();
    //var contentIdArray = new Array();

    for(var i = 0; i < contentCount; i++){
      var date = new Object();
      date.year = contentList[i].startDate.getFullYear();
      date.month = contentList[i].startDate.getMonth() + 1;
      date.day = contentList[i].startDate.getDate();
      date.realdate = contentList[i].startDate;
      startDate[i] = date;
      //endDate[i] = contentList[i].endDate;
      //contentIdArray[i] = contentList[i].contentId;
    }
    console.log(startDate);
    res.send({startDate: startDate});
  });
});

//컨텐츠 아이디or네임으로 유저의 해당되는 컨텐츠 리스트를 알아야 함(새로운 컨텐츠 리스트를 추가해야 됨, 푸시만 하면)
router.post('/contentJoinComplete',  function (req,res) {
  console.log("contentJoinComplete Start")
  // console.log("contentJoinComplete jwt토큰 "+ req.body.token);
  var decoded = jwt.decode(req.body.token,req.app.get("jwtTokenSecret"));
  // console.log("contentJoinComplete jwt토큰 디코딩 "+ decoded.userCheck);
  var userEmail = decoded.userCheck;
  var contentName = req.body.contentName;
  /**/

  Content.find({name: contentName}, function(err, contentList){
    // console.log(contentList);
    var year = req.body.year;
    var month = req.body.month;
    var day = req.body.day;
    var contentId;

    for(var i = 0; i < Object.keys(contentList).length; i++){
      console.log(year +"-"+ month +"-"+ day + "and real Date " + contentList[i].startDate.getFullYear() + "-"+ contentList[i].startDate.getMonth() + "-" + contentList[i].startDate.getDate());
      if(contentList[i].startDate.getFullYear() == year && contentList[i].startDate.getMonth() + 1 == month && contentList[i].startDate.getDate() == day){
        User.findOne({email: userEmail}, function(err, user){
          var contentYear = contentList[i].startDate.getFullYear();
          var contentMonth = contentList[i].startDate.getMonth() + 1;
          var contentDay = contentList[i].startDate.getDate();
          // 일이 한자리 수인 경우 앞에 0을 붙여주기 위해
          if ((contentMonth+"").length < 2) {
            contentMonth = "0" + contentMonth;
          }
          if ((contentDay+"").length < 2) {
            contentDay = "0" + contentDay;
          }
          var date = contentYear+ "-" + contentMonth + "-" + contentDay;
          var contentId = contentList[i].id;
          var contentName = contentList[i].name;

          // if(contentName === "NoSmoking"){
          //   Content.findOne({name: contentName, id: contentId},function(err, content){
          //     if(err){
          //       console.log(err);
          //     }
          //
          //   });
          // }

          console.log("contentId: "+ contentId + "content Name: " + contentName);
          console.log("date: " + date);

          //user의 content List에 해당 content 정보들 추가
          User.findOneAndUpdate({email: userEmail}, {$push:{contentList: [{contentId: contentId, contentName: contentName, isUploaded: "0",
                authenticationDate: date, joinState: 0}]}},function(err, doc){
            if(err){
              console.log("contentJoinComplete  User findOneAndUpdate err :"+err);
            }
            console.log("join user update done");
          });

          var userName = user.name;

          //content의 user List에 해당 user 추가
          Content.findOneAndUpdate({name: contentName, id: contentId}, {$push:{userList : [{name: userName, email: userEmail, result: 2},]}}, function(err,doc){
            if(err){
              console.log("contentJoinComplete Content findOneAndUpdate err :"+err);
            }
            console.log("join content update done");
            mkdirp('./server/user/'+userEmail+'/video/'+contentName, function (err) {
              if(err) console.log("create dir content err : "+err);
              else console.log("create dir : "+contentName);
            }); //server폴더 아래 /user/useremail/video 폴더가 생김.
          });
        });

        break;
      }
    }
    res.send({success: true});
  })
});

//달성률과 설명을 보내준다.
router.get('/getAchievementRate/:jwtToken/:contentName',  function (req,res) {
  console.log("send achievementRate and Description");
  var decoded = jwt.decode(req.params.jwtToken,req.app.get("jwtTokenSecret"));
  // console.log("achievementRate jwt토큰 디코딩 "+ decoded.userCheck);
  var userEmail = decoded.userCheck;
  var contentName = req.params.contentName;

  console.log(userEmail + "  " +contentName);
  var contentId;

  User.findOne({ email : userEmail }, function(err, user) {
    if(user.contentList.length == 0){
      console.log("send -1");
      res.send({rate: -1});
    }
    else{
      console.log("fuck else");
      var contentListCount = user.contentList.length;
      var contentListIndex = -1;
      for (var i = 0; i < contentListCount; i++) {
        if (user.contentList[i].contentName === contentName) {
          contentListIndex = i;
          break;
        }
      }
      if(contentListIndex == -1){
        console.log("send -1 2nd");
        res.send({rate: -1});
      }
      else{
        console.log("else ");
        contentId = user.contentList[contentListIndex].contentId;
        console.log("contentId = "+ contentId);
      }
    }
    Content.findOne({name: contentName, id: contentId}, function(err, content){
      if(err) {
        console.log("getAchievementRate err : "+err);
        res.send({rate: -1});
      }
      else if(content != null){
        if(content.description != null && content.achievementRate != null){
          console.log(content.description + " and "  + content.achievementRate);
          res.send({success: true, description: content.description, rate: content.achievementRate});
        }
        else{
          console.log("content 시작 전")
          res.send({rate: 0});
        }
      }
    });
  });
});

module.exports = router ;
