const express = require('express');
const router = express.Router();
var User = require('../models/user');

router.get('/getCalendarInfo/:jwtToken/:contentName', function (req,res) {
  console.log("calendar jwt토큰 "+ req.body.token);
  var decoded = jwt.decode(req.body.token,req.app.get("jwtTokenSecret"));
  console.log("calendar jwt토큰 디코딩 "+ decoded.userCheck);
  var userEmail = decoded.userCheck;

  var contentName = req.params.contentName;

  User.findOne({ email : userEmail , "contentList.contentName": contentName}, function(err, user) {
    var joinContentCount = user.contentList.length;
    var contentIndex;
    for(var i = 0; i < joinContentCount; i++){
      if(user.contentList[i].contentName === contentName) {
        contentIndex = j;
        break;
      }
    }
    
    res.send(contentList[contentIndex].calendar);
  });
});

module.exports = router ;
