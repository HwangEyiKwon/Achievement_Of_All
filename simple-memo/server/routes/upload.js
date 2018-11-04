const express = require('express');
const router = express.Router();

var fs = require('fs');


router.get('/',function(req,res){
    res.end("Node-File-Upload");
});

router.post('/upload', function(req, res) {
    console.log(req.files.video.originalFilename);
    console.log(req.files.video.path);
    fs.readFile(req.files.video.path, function (err, data){
      var dirname = "./";
      var newPath = dirname + "/uploads/" + 	req.files.video.originalFilename;
      fs.writeFile(newPath, data, function (err) {
        if(err){
          res.json({'response':"Error"});
        }else {
          res.json({'response':"Saved"});
        }
      });
    });
});


router.get('/uploads/:file', function (req, res){
  file = req.params.file;
  var dirname = "./";
  var video = fs.readFileSync(dirname + "/uploads/" + file);
  res.writeHead(200, {'Content-Type': 'video/mp4' });
  res.end(video, 'binary');
});

module.exports = router ;
