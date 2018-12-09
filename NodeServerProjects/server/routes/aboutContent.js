const express = require('express');
const router = express.Router();
var jwt = require('jwt-simple'); // jwt token 사용
var Content = require('../models/content')
var User = require('../models/user');
var mkdirp = require('mkdirp'); // directory 만드는것

router.get('/contentJoin/:contentName', function (req,res) {
  console.log("contentJoin Start");
  var contentName = req.params.contentName;

  var todayDate = new Date();
  var todayMonth = todayDate.getMonth() + 1;
  var todayDay = todayDate.getDate();
  var todayYear = todayDate.getFullYear();

  var today = new Date(todayYear, todayMonth-1, todayDay);

 // Content.find({name : contentName, isDone: 2}, function(err, contentList) {
  Content.find({ $or: [ { name: contentName, isDone: 2 }, {name: contentName, isDone: 0, startDate: today}]}, function(err,contentList){
//   Content.find({name: contentName, isDone: 0, startDate: today}, function(err,contentList){
    console.log("today : " +today);
    console.log("contentLsit : "+contentList);
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

    var todayDate = new Date();
    var todayMonth = todayDate.getMonth() + 1;
    var todayDay = todayDate.getDate();
    var todayYear = todayDate.getFullYear();

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

          console.log("contentId: "+ contentId + "content Name: " + contentName);
          console.log("date: " + date);

          //user의 content List에 해당 content 정보들 추가
          if(year == todayYear && month == todayMonth && day == todayDay){
            User.findOneAndUpdate({email: userEmail}, {$push:{contentList: [{contentId: contentId, contentName: contentName, isUploaded: "0",
                  authenticationDate: date, joinState: 1, money: 100000, reward: 0, rewardCheck: false}]}},function(err, doc){
              if(err){
                console.log("contentJoinComplete  User findOneAndUpdate err :"+err);
              }
              console.log("join user update done");
            });
          }
          else if(contentName === "NoSmoking"){
            User.findOneAndUpdate({email: userEmail}, {$push:{contentList: [{contentId: contentId, contentName: contentName, isUploaded: "0",
                  authenticationDate: date, joinState: 0, money: 100000, reward: 0, rewardCheck: false}]}},function(err, doc){
              if(err){
                console.log("contentJoinComplete  User findOneAndUpdate err :"+err);
              }
              console.log("join user update done");
            });
          }
          else{
            User.findOneAndUpdate({email: userEmail}, {$push:{contentList: [{contentId: contentId, contentName: contentName, isUploaded: "0",
                  authenticationDate: date, joinState: 0}]}},function(err, doc){
              if(err){
                console.log("contentJoinComplete  User findOneAndUpdate err :"+err);
              }
              console.log("join user update done");
            });
          }

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
        if(content.achievementRate != null){
          res.send({success: true,  rate: content.achievementRate});
        }
        else{
          console.log("content 시작 전")
          res.send({rate: 0});
        }
      }
    });
  });
});

router.get('/getContentRule/:contentName/:startMonth/:startDay/:startYear',  function (req,res) {
  var contentName = req.params.contentName;
  var startYear = req.params.startYear;
  var startMonth = req.params.startMonth;
  var startDay = req.params.startDay;
  var startDate = new Date(startYear, (startMonth-1), startDay);
  console.log("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
  console.log("rule start : "+ startDate);
  console.log("rule start : "+ startYear);
  console.log("rule start : "+ startMonth);
  console.log("rule start : "+ startDay);
  Content.findOne({name: contentName, startDate: startDate}, function(err, content){
    console.log(content);
    if(err) {
      console.log(" err : "+err);
      res.send({success: false});
    }
    else if(content != null){
      if(content.description != null){
        res.send({success: true,  description: content.description});
      }
      else{
        res.send({success: false});
      }
    }
  });
});

//컨텐츠 참여 상태와 시작일, 끝일을 전달한다.
router.get("/isParticipated/:jwtToken/:contentName", function(req,res) {
  console.log("isParticipated Start");
  var decoded = jwt.decode(req.params.jwtToken,req.app.get("jwtTokenSecret"));
  // console.log("isParticipated jwt토큰 디코딩 "+ decoded.userCheck);
  var userEmail = decoded.userCheck;

  var contentName = req.params.contentName;

  var joinState;
  var startDate;
  var endDate;
  User.findOne({ email : userEmail , "contentList.contentName": contentName}, function(err, user) {
    if(err){
      console.log("isparticipated err : "+err);
      res.send({success: false});
    }
    else{
      if(user == null) res.send({joinState: 3, startDate: {year: -1, month: -1, day: -1},  endDate: {year: -1, month: -1, day: -1}});
      else  {
        var contentIndex;
        var joinContentCount = user.contentList.length;
        var contentId;

        for (var i = 0; i < joinContentCount; i++) {
          if (user.contentList[i].contentName === contentName) {
            contentIndex = i;
            break;
          }
        }
        joinState = user.contentList[contentIndex].joinState;
        contentId = user.contentList[contentIndex].contentId;

        Content.findOne({name: contentName, id: contentId}, function(err, content){
          startDate = content.startDate;
          endDate = content.endDate;

          res.send({joinState: joinState, startDate: {year: startDate.getFullYear(), month: startDate.getMonth() + 1, day: startDate.getDate()},
            endDate: {year: endDate.getFullYear(), month: endDate.getMonth() + 1, day: endDate.getDate()}});
        });
      }
    }
  });
});

router.get('/getCalendarInfo/:jwtToken/:contentName', function (req,res) {
  console.log("getCalendarInfo Start");
  console.log("req.params: "+req.params);
  console.log("name "+ req.params.contentName);
  // console.log("calendar jwt토큰 "+ req.params.jwtToken);
  var decoded = jwt.decode(req.params.jwtToken,req.app.get("jwtTokenSecret"));
  // console.log("calendar jwt토큰 디코딩 "+ decoded.userCheck);
  var userEmail = decoded.userCheck;

  var contentName = req.params.contentName;
  console.log("contentName: " + contentName);
  User.findOne({ email : userEmail , "contentList.contentName": contentName}, function(err, user) {
    // console.log(user);
    if(err) console.log(err);
    else {
      if(user == null){
        var array = new Array();
        res.send(array);
      }
      else{
        var joinContentCount = user.contentList.length;
        var contentIndex;
        for (var i = 0; i < joinContentCount; i++) {
          if (user.contentList[i].contentName === contentName) {
            contentIndex = i;
            break;
          }
        }
        res.send(user.contentList[contentIndex].calendar);
      }
    }
  });
});

module.exports = router ;
