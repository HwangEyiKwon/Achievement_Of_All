const express = require('express');
const router = express.Router();

var Content = require('../models/content');
var User = require('../models/user');
var jwt = require('jwt-simple'); // jwt token 사용
var fs = require("fs");
var multiparty = require('multiparty');
var fcmMessage = require('../../server.js')

//사용자의 video 받아서 서버에 저장하는 코드
router.post('/sendVideo', function(req, res, next){
  var decoded = jwt.decode(req.headers.jwt_token, req.app.get("jwtTokenSecret"));
  var userEmail = decoded.userCheck;
  var contentName = req.headers.content_name;//건희가 보내는 거에서 header에 들어있는 내용 꺼내옴
  var filenamePath;
  var index;
  var contentId;

  Content.findOne({name : contentName, "userList.email" : userEmail}, function(err, content){
    contentId = content.id;
    var joinUserCount = content.userList.length;
    var form = new multiparty.Form();
    var year;
    var month;
    var day;
    // get field name & value
    form.on('field',function(name,value){
    });
    // file upload handling
    form.on('part',function(part){
      var filename;
      var size;
      if (part.filename) {
        filename = part.filename;
        filename = filename.split('_');
        filename = filename[0];
        year = filename.substr(0,4);
        month = filename.substr(4,2);
        day = filename.substr(6,2);
        filenamePath = year+'-'+month+'-'+day;
        filename = year+'-'+month+'-'+day+'.mp4'; // 파일 이름이 year-month-day.mp4 로 나옴.
        size = part.byteCount;
      }else{
        part.resume();
      }
      var writeStream = fs.createWriteStream('./server/user/'+userEmail+'/video/'+contentName+'/'+filename);
      writeStream.filename = filename;
      part.pipe(writeStream);
      part.on('data',function(chunk){
      });
      part.on('end',function(){
        writeStream.end();
      });
    });
    // all uploads are completed
    form.on('close',function(){
      if(content == null){
        res.send({success : false});
      }
      else  {
        var userListIndex;
        for (var i = 0; i < joinUserCount; i++) {
          if (content.userList[i].email === userEmail) {
            userListIndex = i;
            break;
          }
        }
        content.userList[userListIndex].newVideo.path = filenamePath;
        content.userList[userListIndex].newVideo.authen = 2;
        content.userList[userListIndex].newVideo.authorizePeople = [];
        content.save(function(err, savedDocument) {
          if (err) console.log("save err : "+err);
        });

        User.findOneAndUpdate({email: userEmail, "contentList.contentName": contentName}, {$push:{"contentList.$.videoPath": [{path : filenamePath, authen: 2}]}},function(err, user){
          if(err){
            console.log("User findOneAndUpdate err : "+err);
          }
        });
        User.findOneAndUpdate({email: userEmail, "contentList.contentName": contentName}, {$push:{"contentList.$.calendar": [{year : year, month: month, day: day, authen: 2}]}},function(err, doc){
          if(err){
            console.log("User findOneAndUpdate err : "+err);
          }
        });

        User.findOne({email: userEmail}, function(err, user){
          if(user.contentList.length != 0) {
            var contentListCount = user.contentList.length;
            var contentListIndex;
            for (var i = 0; i < contentListCount; i++) {
              if (user.contentList[i].contentName === contentName) {
                contentListIndex = i;
                break;
              }
            }
            var threeDaysAfter = new Date(Date.now());
            threeDaysAfter.setDate(threeDaysAfter.getDate() + 3);
            var month = threeDaysAfter.getMonth() + 1;
            var day = threeDaysAfter.getDate();
            var year = threeDaysAfter.getFullYear();
            // 일이 한자리 수인 경우 앞에 0을 붙여주기 위해
            if ((day+"").length < 2) {
              day = "0" + day;
            }
            var date = year+ "-" + month + "-" + day;

            var threeDaysAfterDate = new Date(year,(month-1),day);
            if(content.endDate < threeDaysAfterDate){
              user.contentList[contentListIndex].authenticationDate = date;
            }
            user.contentList[contentListIndex].isUploaded = 1;
            user.save(function(err, savedDocument) {
              if (err) console.log("save err : "+err);
            });
          }
        });
        User.find({"contentList.contentName": contentName, "contentList.contentId": contentId}, function(err, userList){
          for(var i = 0; i < Object.keys(userList).length; i++){
            if(userList[i].email != userEmail){
              var contentListCount = userList[i].contentList.length;
              var contentListIndex;
              for (var j = 0; j < contentListCount; j++) {
                if (userList[i].contentList[j].contentName === contentName) {
                  contentListIndex = j;
                  break;
	              }
              }
              if(userList[i].pushToken != ""){
                var emptyArray = new Array();
                var todayDate = new Date();
                var todayMonth = todayDate.getMonth() + 1;
                var todayDay = todayDate.getDate();
                var todayYear = todayDate.getFullYear();
                var currentHour = todayDate.getHours();
                var currentMinute = todayDate.getMinutes();
                var titleNewVideo = "새영상";
                var sendTime = new Date(todayYear, todayMonth - 1, todayDay, currentHour, currentMinute, todayDate.getSeconds()+5);
                fcmMessage.sendPushMessage2(userList[i], contentListIndex, sendTime, titleNewVideo, contentName, emptyArray, emptyArray);
              }
            }
          }
        });

        res.send({success : true});
      }
    });
    // track progress
    form.on('progress',function(byteRead,byteExpected){
    });
    form.parse(req);
  });
});

router.post('/checkVideo', function(req,res){

  var jwtToken = req.body.token; // 내꺼 토큰
  var contentName = req.body.contentName; // 컨텐츠 이름
  var otherEmail = req.body.email; // 상대방 메일
  var authenInfo = req.body.authenInfo; // 인증
  var checkReason = req.body.checkReason; // 실패 체크한 이유
  var failAuthenUserArray = new Array();
  var checkReasonArray = new Array();
  var decoded = jwt.decode(jwtToken, req.app.get("jwtTokenSecret"));
  var userEmail = decoded.userCheck; // 내 이메일
  var contentId;
  var authenDay;

  function uf(callback) {
    return new Promise(function (resolve,reject) {
      User.findOne({email: otherEmail}, function(err, otherUser){
        if(otherUser.contentList.length != 0){
          var contentListCount = otherUser.contentList.length;
          var contentListIndex;
          for (var i = 0; i < contentListCount; i++) {
            if (otherUser.contentList[i].contentName === contentName) {
              contentListIndex = i;
              break;
            }
          }
          contentId = otherUser.contentList[contentListIndex].contentId;
          authenDay = otherUser.contentList[contentListIndex].authenticationDate;
          resolve(otherUser);
        }
      })
    });
  }



  uf().then(function (otherUser) {

    Content.findOneAndUpdate({name: contentName, id: contentId, "userList.email": otherEmail},
      {$addToSet:{"userList.$.newVideo.authorizePeople": { $each: [{email : userEmail, authenInfo: authenInfo, checkReason: checkReason}]}}},function(err, content){
        if(err){
          console.log("User findOneAndUpdate err : "+err);
        }
        Content.findOne({name: contentName, id: contentId}, function(err, content){
          var userListCount = content.userList.length;
          var userListIndex;
          var contentListCount = otherUser.contentList.length;
          var contentListIndex;
          var authorizeUserCount;
          var successCount = 0;
          var voteRate;
          var videoIndex = 0;
          var calendarIndex = 0;

          for (var i = 0; i < userListCount; i++) {
            if (content.userList[i].email === otherEmail) {
              userListIndex = i;
              break;
            }
          }
          authorizeUserCount = content.userList[userListIndex].newVideo.authorizePeople.length;

          //2명일 때 체크
          if(authorizeUserCount === 2){
            for(var j = 0; j < contentListCount; j++){
              if(otherUser.contentList[j].contentName === contentName){
                contentListIndex = j;
                break;
              }
            }

            if(otherUser.contentList[contentListIndex].calendar.length != 0) {
              calendarIndex = otherUser.contentList[contentListIndex].calendar.length - 1;
            }
            if(otherUser.contentList[contentListIndex].videoPath.length != 0) {
              videoIndex = otherUser.contentList[contentListIndex].videoPath.length - 1;
            }


            for (var i = 0; i < authorizeUserCount; i++) {
              if(content.userList[userListIndex].newVideo.authorizePeople[i].authenInfo == 1){
                successCount++;
              }
              else{
                failAuthenUserArray.push(content.userList[userListIndex].newVideo.authorizePeople[i].email);
                checkReasonArray.push(content.userList[userListIndex].newVideo.authorizePeople[i].checkReason);
              }
            }

            var year = authenDay.substr(0,4);
            var month = authenDay.substr(5,2);
            var day = authenDay.substr(8,2);
            var threeAfterAuthenDate = new Date(year, month-1, day);
            threeAfterAuthenDate.setDate(threeAfterAuthenDate.getDate() + 3);
            voteRate = (successCount / authorizeUserCount) * 100;
            if(voteRate >= 50){
              content.userList[userListIndex].newVideo.authen = 1;

              otherUser.contentList[contentListIndex].videoPath[videoIndex].authen = 1;
              otherUser.contentList[contentListIndex].calendar[calendarIndex].authen = 1;

              otherUser.save(function(err, savedDocument) {
                if (err)
                  return console.error(err);
              });
              content.save(function(err, savedDocument) {
                if (err)
                  return console.error(err);
              });

              if(threeAfterAuthenDate >= content.endDate){
                if(otherUser.pushToken != ""){
                  var emptyArray = new Array();
                  var todayDate = new Date();
                  var todayMonth = todayDate.getMonth() + 1;
                  var todayDay = todayDate.getDate();
                  var todayYear = todayDate.getFullYear();
                  var currentHour = todayDate.getHours();
                  var currentMinute = todayDate.getMinutes();
                  var titleWillSuccess= "성공예정";
                  var sendTime = new Date(todayYear, todayMonth - 1, todayDay, currentHour, currentMinute, todayDate.getSeconds()+5);
                  fcmMessage.sendPushMessage2(otherUser, contentListIndex, sendTime, titleWillSuccess, contentName, emptyArray, emptyArray);
                }
              }
            }
            else{
              content.userList[userListIndex].newVideo.authen = 0;

              otherUser.contentList[contentListIndex].videoPath[videoIndex].authen = 0;
              otherUser.contentList[contentListIndex].calendar[calendarIndex].authen = 0;

              otherUser.save(function(err, savedDocument) {
                if (err)
                  return console.error(err);
              });
              content.save(function(err, savedDocument) {
                if (err)
                  return console.error(err);
              });
              if(otherUser.pushToken != ""){
                var todayDate = new Date();
                var todayMonth = todayDate.getMonth() + 1;
                var todayDay = todayDate.getDate();
                var todayYear = todayDate.getFullYear();
                var currentHour = todayDate.getHours();
                var currentMinute = todayDate.getMinutes();
                var titleFailVideo = "비디오실패";
                var sendTime = new Date(todayYear, todayMonth - 1, todayDay, currentHour, currentMinute, todayDate.getSeconds()+5);
                fcmMessage.sendPushMessage2(otherUser, contentListIndex, sendTime, titleFailVideo, contentName, failAuthenUserArray, checkReasonArray);
              }
              else{
                otherUser.contentList[contentListIndex].fcmVideoFailureFlag = 1;
                otherUser.contentList[contentListIndex].fcmMessageArray.failAuthenUserArray = failAuthenUserArray;
                otherUser.contentList[contentListIndex].fcmMessageArray.reasonArray = checkReasonArray;
                otherUser.save(function(err, savedDocument) {
                  if (err)
                    return;
                });
              }
            }
          }
        });
      })
  })
});

//android쪽에서 반복적으로 호출, 해당 내용 보내주기만 하면 됨.
router.get('/getVideo/:jwtToken/:contentName/:videoPath', function(req,res){
  var decoded = jwt.decode(req.params.jwtToken, req.app.get("jwtTokenSecret"));
  var userEmail = decoded.userCheck;
  var contentName = req.params.contentName;
  var videoPath = req.params.videoPath;

  User.findOne({email: userEmail}, function(err, user){
    var filename = "./server/user/"+userEmail+"/video/"+contentName+"/"+videoPath+".mp4"
    var file = fs.createReadStream(filename, {flags: 'r'});
    file.pipe(res);
  });
});

//타 유저 홈을 들어갈 때 동영상 불러오는 루틴
router.get('/getOtherUserVideo/:email/:contentName/:videoPath', function(req,res){
  var userEmail = req.params.email;
  var contentName = req.params.contentName;
  var videoPath = req.params.videoPath;

  User.findOne({email: userEmail}, function(err, user){
    var filename = "./server/user/"+userEmail+"/video/"+contentName+"/"+videoPath+".mp4"
    var file = fs.createReadStream(filename, {flags: 'r'});
    file.pipe(res);
  });
});

//타인의 인증 비디오를 가져오는 코드
router.get('/getOthersVideo/:email/:contentName', function(req,res){
  var userEmail = req.params.email;
  var contentName = req.params.contentName;

  User.findOne({email: userEmail}, function(err, user){
    var joinContentCount = user.contentList.length;
    var contentIndex;

    for (var i = 0; i < joinContentCount; i++) {
      if (user.contentList[i].contentName === contentName) {
        contentIndex = i;
        break;
      }
    }
    var videoIndex = user.contentList[contentIndex].videoPath.length - 1;
    var videoPath = user.contentList[contentIndex].videoPath[videoIndex].path;
    var filename = "./server/user/"+userEmail+"/video/"+contentName+"/"+videoPath+".mp4"
    var file = fs.createReadStream(filename, {flags: 'r'});
    file.pipe(res);

  });
});

//내가 인증해야 하는 타인들의 비디오를 표시하기 위해 아직 내가 인증 안한 타인의 정보를 가져오는 코드
router.get("/getOthers/:jwtToken/:contentName", function(req,res) {
  var decoded = jwt.decode(req.params.jwtToken,req.app.get("jwtTokenSecret"));
  var userEmail = decoded.userCheck;
  var contentName = req.params.contentName;
  var others = new Array();
  var arrayCount = 0;

  User.findOne({email: userEmail}, function(err, user){
    var userName = user.name;
    if(user.contentList.length == 0){
      var array = new Array();
      res.send(array);
    }
    else{
      var contentListCount = user.contentList.length;
      var contentListIndex;
      for (var i = 0; i < contentListCount; i++) {
        if (user.contentList[i].contentName === contentName) {
          contentListIndex = i;
          break;
        }
      }
      var contentId = user.contentList[contentListIndex].contentId;
      Content.findOne({name: contentName, id: contentId}, function(err, content){
        if(err){
          console.log(err);
          res.send({success: false});
        }
        else {
          var joinUserCount = content.userList.length;
          var userIndex;
          if(joinUserCount != 0){
            for (var i = 0; i < joinUserCount; i++) {
              var authorizePeopleEmail = content.userList[i].email;
              var authorizePeopleName = content.userList[i].name;
              if (content.userList[i].email != userEmail && content.userList[i].newVideo.authen == 2) {
                //내가 다른 사람의 최신 영상을 인증했는지 체크하는 flag
                var userCheckFlag = 0;
                var authorizePeopleCount = content.userList[i].newVideo.authorizePeople.length;
                if(authorizePeopleCount != 0){
                  for(var j = 0; j < authorizePeopleCount; j ++){
                    if(content.userList[i].newVideo.authorizePeople[j].email === userEmail) userCheckFlag = 1;
                  }
                }
                if(userCheckFlag == 0){
                  var othersInfo = new Object();
                  othersInfo.email = authorizePeopleEmail;
                  othersInfo.name = authorizePeopleName;
                  others[arrayCount] = othersInfo;
                  arrayCount++;
                }
              }
            }
            res.send({others: others});
          }
        }
      });
    }
  });
});

module.exports = router ;
