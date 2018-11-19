const express = require('express');
const router = express.Router();

var Content = require('../models/content');
var User = require('../models/user');
var jwt = require('jwt-simple'); // jwt token 사용
var fs = require("fs");
var multiparty = require('multiparty');

//사용자의 video 받아서 서버에 저장하는 코드
router.post('/sendVideo', function(req, res, next){
  console.log("send Video to server");
  // console.log("send Video User jwt토큰 "+ req.params.jwtToken);
  // var decoded = jwt.decode(req.params.jwtToken,req.app.get("jwtTokenSecret"));
  // console.log("send Video User jwt토큰 디코딩 "+ decoded.userCheck);
  // var userEmail = decoded.userCheck;

  //var userEmail = req.headers.jwt_token;
  var userEmail = "shp17@gmail.com";
  console.log("userEmail : "+userEmail);
  console.log(req.headers.content_name);
  var contentName = req.headers.content_name;//건희가 보내는 거에서 header에 들어있는 내용 꺼내옴
  var filenamePath;
  var index;

  Content.findOne({name : contentName, "userList.email" : userEmail}, function(err, content){

    var joinUserCount = content.userList.length;
    var form = new multiparty.Form();
    var year;
    var month;
    var day;
    // get field name & value
    form.on('field',function(name,value){
      console.log('normal field / name = '+name+' , value = '+value);
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
      console.log("Write Streaming file :"+filename);
      var writeStream = fs.createWriteStream('./server/user/'+userEmail+'/video/'+contentName+'/'+filename);
      writeStream.filename = filename;
      part.pipe(writeStream);
      part.on('data',function(chunk){
        // console.log(filename+' read '+chunk.length + 'bytes');
      });
      part.on('end',function(){
        console.log(filename+' Part read complete');
        writeStream.end();
      });
    });
    // all uploads are completed
    form.on('close',function(){
      if(content == null){
        console.log("not found User and content");
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
        content.userList[userListIndex].newVideo.authen = 0;
        content.userList[userListIndex].newVideo.authorizePeople = [];
        content.save(function(err, savedDocument) {
          if (err) console.log(err);
          console.log("content DB saving in sendVideo");
        });

        User.findOneAndUpdate({email: userEmail, "contentList.contentName": contentName}, {$push:{"contentList.0.videoPath": [{path : filenamePath, authen: 2}]}},function(err, doc){
          if(err){
            console.log(err);
          }
          console.log("send video_update videoPath : Path : "+filenamePath+" authen : "+0);
        });
        User.findOneAndUpdate({email: userEmail, "contentList.contentName": contentName}, {$push:{"contentList.0.calendar": [{year : year, month: month, day: day, authen: 2}]}},function(err, doc){
          if(err){
            console.log(err);
          }
          console.log("send video_update calendar");
        });
        res.send({success : true});
        console.log("send success : true ");
      }
    });
    // track progress
    form.on('progress',function(byteRead,byteExpected){
      // console.log(' Reading total  '+byteRead+'/'+byteExpected);
    });
    form.parse(req);
  });
});

//android쪽에서 반복적으로 호출, 해당 내용 보내주기만 하면 됨.
router.get('/getVideo/:jwtToken/:contentName/:videoPath', function(req,res){

  console.log("video jwt토큰 "+ req.params.jwtToken);
  var decoded = jwt.decode(req.params.jwtToken, req.app.get("jwtTokenSecret"));
  console.log("video jwt토큰 디코딩 "+ decoded.userCheck);
  var userEmail = decoded.userCheck;
  var contentName = req.params.contentName;
  var videoPath = req.params.videoPath;

  console.log("ddd"+userEmail+contentName+videoPath);

  User.findOne({email: userEmail}, function(err, user){
    var filename = "./server/user/"+userEmail+"/video/"+contentName+"/"+videoPath+".mp4"
    var file = fs.createReadStream(filename, {flags: 'r'});
    file.pipe(res);
  });
});

//타인의 비디오를 가져오는 코드
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
  console.log("getOthers jwt토큰 "+ req.params.jwtToken);
  var decoded = jwt.decode(req.params.jwtToken,req.app.get("jwtTokenSecret"));
  console.log("getOthers jwt토큰 디코딩 "+ decoded.userCheck);
  var userEmail = decoded.userCheck;
  var contentName = req.params.contentName;

  console.log(userEmail + contentName);
  var others = new Array();
  var arrayCount = 0;

  User.findOne({email: userEmail}, function(err, user){
    console.log(user);
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
      console.log(contentId + "    " + contentName);
      Content.findOne({name: contentName, id: contentId}, function(err, content){
        console.log("content정보: " + content);
        if(err){
          console.log(err);
          res.send({success: false});
        }
        else {
          var joinUserCount = content.userList.length;
          console.log("user num = "+joinUserCount);
          var userIndex;
          if(joinUserCount != 0){
            for (var i = 0; i < joinUserCount; i++) {
              var authorizePeopleEmail = content.userList[i].email;
              var authorizePeopleName = content.userList[i].name;
              console.log("user리스트 이메일= " + content.userList[i].email + " userList 이름 " + content.userList[i].name);
              if (content.userList[i].email != userEmail && content.userList[i].newVideo.authen == 0) {
                //내가 다른 사람의 최신 영상을 인증했는지 체크하는 flag
                var userCheckFlag = 0;
                var authorizePeopleCount = content.userList[i].newVideo.authorizePeople.length;
                console.log("사람 수 = " + authorizePeopleCount);
                if(authorizePeopleCount != 0){
                  for(var j = 0; j < authorizePeopleCount; j ++){
                    console.log("인증한 사람의 이름: " + content.userList[i].newVideo.authorizePeople[j].name);
                    if(content.userList[i].newVideo.authorizePeople[j].name === userName) userCheckFlag = 1;
                  }
                }
                if(userCheckFlag == 0){
                  console.log("flag is 0");

                  var othersInfo = new Object();
                  othersInfo.email = authorizePeopleEmail;
                  othersInfo.name = authorizePeopleName;

                  console.log("object 출력"+ JSON.stringify(othersInfo));
                  others[arrayCount] = othersInfo;
                  arrayCount++;
                }
              }
            }
            console.log("video를 보여주기 위해 return할 사용자 정보: " + others);
            res.send({others: others});
          }
        }
      });
    }
  });
});

module.exports = router ;
