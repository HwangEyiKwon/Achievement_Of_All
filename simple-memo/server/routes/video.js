const express = require('express');
const router = express.Router();

var Content = require('../models/content');
var User = require('../models/user');
var jwt = require('jwt-simple'); // jwt token 사용
var fs = require("fs");
var multiparty = require('multiparty');
// make sure the db instance is open before passing into `Grid`

/*
// jwt token이용 -> email 추출 -> ...진행
//to.승현) get->post로 변경하여 이메일 인증을 위해 req.body에서 token가져올 수 있게끔 함
router.post('/video', function(req,res){
  console.log("video connected");
  console.log("video jwt토큰 "+ req.body.token);
  var decoded = jwt.decode(req.body.token,req.app.get("jwtTokenSecret"));
  console.log("video jwt토큰 디코딩 "+ decoded.userCheck);
  var userEmail = decoded.userCheck;

  User.findOne({email: userEmail}, function(err, user){
    var joinContentCount = user.contentList.length;

    console.log("video Path: " +user.videoPath);
    for(var i = 0; i < joinContentCount ;  i++) {
      var numOfVideo = user.contentList[i].videoPath.length;
      if(numOfVideo != null){
        for(var j = 0; j < numOfVideo; j++) {
          var filename = user.contentList[i].videoPath[j];
          var file = fs.createReadStream(filename, {flags: 'r'});
          file.pipe(res);
        }
      }
    }
  });
});
*/

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

    // 내 비디오가 맞는지 검사해야할 듯
    var filename = "./server/user/"+userEmail+"/video/"+contentName+"/"+videoPath+".mp4"
    var file = fs.createReadStream(filename, {flags: 'r'});
    file.pipe(res);

  });
});

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
    // 내 비디오가 맞는지 검사해야할 듯
    var filename = "./server/user/"+userEmail+"/video/"+contentName+"/"+videoPath+".mp4"
    var file = fs.createReadStream(filename, {flags: 'r'});
    file.pipe(res);

  });
});

/*
//jwt토큰 필요 -> email 받아옴
//email로 해당 유저의 user.contentList[0].videoPath를 가져옴
router.get('/getVideoList', function(req, res){
  var userEmail = req.body.email;
  User.findOne({email: userEmail}, function(err, user){
    console.log("video Path: " +user.contentList[0].videoPath);

    res.send(user.contentList[0].videoPath);
  });
});
*/

//video 받아서 저장..
//contents정보 받아야 하고, 현재 date를 넣어줘야 하고, path를 지정해서 해당 user의 video경로에 path를 저장한다.
//video 받아서 저장..
//contents정보 받아야 하고, 현재 date를 넣어줘야 하고, path를 지정해서 해당 user의 video경로에 path를 저장한다.
// router.get('/sendVideo/:jwtToken/:contentName', function(req, res, next){
router.post('/asd', function(req, res, next){

  // console.log("get authorizeVideo ");
  // console.log("authorize Video User jwt토큰 "+ req.params.jwtToken);
  // var decoded = jwt.decode(req.params.jwtToken,req.app.get("jwtTokenSecret"));
  // console.log("authorize Video User jwt토큰 디코딩 "+ decoded.userCheck);
  //var userEmail = decoded.userCheck;
  var userEmail = req.headers.jwt_token;
  console.log("userEmail : "+userEmail);
  // var contentName = req.params.contentName;
  // var contentId = req.params.contentId;
  console.log(req.headers.content_name);
  var contentName = req.headers.content_name;//건희가 보내는 거에서 header에 들어있는 내용 꺼내옴
  var filenamePath;
  var index;

  //createReadStream("./~~~~")이걸로 데이터 받고  createWriteStream("생성할 파일 이름 ")으로 생성 후
  //fs.createReadStream.pipe(fs.createWriteStream);
  Content.findOne({name : contentName, "userList.email" : userEmail}, function(err, content){

    var joinUserCount = content.userList.length;
    var form = new multiparty.Form();
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
        var year = filename.substr(0,4);
        var month = filename.substr(4,2);
        var day = filename.substr(6,2);
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
          if (err)
            return console.error(err);
          console.log("content DB initialization");
        });

        User.findOneAndUpdate({email: userEmail, "contentList.contentName": contentName}, {$push:{"contentList.0.videoPath": [{path : filenamePath, authen: 0}]}},function(err, doc){
          if(err){
            console.log(err);
          }
          console.log("USER !!!!! : "+user);
          console.log("update videoPath : Path : "+filenamePath+" authen : "+0);
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


router.post('/sendVideo', function(req, res) {
  fs.readFile(req.files.video.path, function (err, data){
    var newPath = "./server/user/" + 	req.files.video.originalFilename;
    fs.writeFile(newPath, data, function (err) {
      if(err){
        res.json({'response':"Failure"});
      }else {
        res.json({'response':"Success"});
      }
    });
  });
});

/*
//남에게 videolist를 줘야하는 것... -> 같은 컨텐츠의 참여하는 다른 사람에게 전송 or 같은 방 참여 다른 사람에게 전송
//인증여부에 대한 것도 post로 만들어야 하지 않나?????
//단계 -> server야 나 인증할거야! -> client에게 videopath보내주기 -> 인증 성공 여부 server에게 보내기
router.post('/:contentID/checkVideo', function(req, res){
  // video path읽어서 pipe해주는 코드.. 어떤 비디오 가져올 지 생각해서 추가하기
  var userEmail = req.body.email;
  User.findOne({email: userEmail}, function(err, user){
    console.log("video Path: " +user.videoPath);

    var filename = user.videoPath[0];
    var file = fs.createReadStream(filename, {flags: 'r'});
  });
  file.pipe(res);
});
*/


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
    var userName = user.name;
    Content.findOne({name: contentName}, function(err, content){
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
  });


});

/*
function getOtherInfo(){
  return new Promise((resolve,reject )=>{


    resolve();
  })
}
*/

module.exports = router ;
