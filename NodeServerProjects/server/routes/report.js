const express = require('express');
const router = express.Router();
var User = require('../models/user');
var Content = require('../models/content');
var Report = require('../models/report');
var jwt = require('jwt-simple'); // jwt token 사용

router.get('/reportUserList/:jwtToken/:contentName/:reportReason', function (req,res) {
  console.log("reportUserList Start!!!");
  var decoded = jwt.decode(req.params.jwtToken,req.app.get("jwtTokenSecret"));
  // console.log("isParticipated jwt토큰 디코딩 "+ decoded.userCheck);
  var userEmail = decoded.userCheck;
  var contentName = req.params.contentName;
  var reportContentId;
  var reportContentName = contentName;
  var reportUserEmail = userEmail;
  var reportReason = req.params.reportReason;
  var reportAuthenDay;
  var complete;
  var reportUser = new Array();

  User.findOne({email: userEmail}, function(err, user) {

    var contentListCount = user.contentList.length;
    var contentListIndex;
    for (var i = 0; i < contentListCount; i++) {
      if (user.contentList[i].contentName === contentName) {
        contentListIndex = i;
        break;
      }
    }
    var contentId = user.contentList[contentListIndex].contentId;
    Content.findOne({id: contentId, name : contentName}, function(err, content) {
      var userListCount = content.userList.length;
      var userListIndex;
      var authrizePeopleCount;
      var authrizePeopleIndex;
      for(var i = 0; i < userListCount; i++){
        if(content.userList[i].email === userEmail){
          userListIndex = i;
          break;
        }
      }
      reportAuthenDay = content.userList[userListIndex].newVideo.path;
      authrizePeopleCount = content.userList[userListIndex].newVideo.authorizePeople.length;
      for(var i = 0; i < authrizePeopleCount; i++){
        if(content.userList[userListIndex].newVideo.authorizePeople[i].authenInfo === 0){
          reportUser.push(content.userList[userListIndex].newVideo.authorizePeople[i].email);
        }
      }

      var newReport = new Report({
        contentId: reportContentId,
        contentName: reportContentName,
        userEmail: reportUserEmail,
        reportReason: reportReason,
        authenDay: reportAuthenDay,
        reportUser: reportUser,
        complete: 0
      });
      newReport.save(function(err, savedDocument) {
        if (err)
          return console.error(err);
        else{
          console.log(savedDocument);
          console.log("report save");
          res.send({success:true});
        }
      });
    });
  });
});
// -----------------------------------------------------
module.exports = router ;
