const express = require('express');
const router = express.Router();
var User = require('../models/user');
var jwt = require('jwt-simple'); // jwt token 사용

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
