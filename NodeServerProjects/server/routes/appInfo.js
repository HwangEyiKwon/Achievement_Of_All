const express = require('express');
const router = express.Router();

const App = require('../models/app');

router.get('/getAppInfo', function (req,res) {

    App.findOne(function (err, info) {
      console.log("AppInfo: " + info);
      res.send(info);
    })
  }
)

module.exports = router ;
