const mongoose = require('mongoose');

const { Schema } = mongoose;
const userSchema = new Schema({
    name: {
        type: String,
    },
    email: {
        type: String,
    },
    password: {
        type: String,
    },
    authority: {
        type: String,
    },
    phoneNumber: {
        type: String,
    },
    nickname: {
        type: String,
    },
    imagePath: {
        type: String,
    },
    reportCount: {
        type: Number,
    },
    contentList: {
        type: Array,
    },
});
//
// userSchema.methods.comparePassword = function(password) {
//   //if (inputPassword === this.password) {
//    // cb(null, true);
//   //} else {
//   //  cb('error');
//   //}
//   return bcrypt.compareSync(password, this.password)
// };

//password를 암호화
userSchema.methods.generateHash = function(password) {
  return bcrypt.hashSync(password, bcrypt.genSaltSync(8), null);
};


userSchema.methods.validPassword = function(password, cb) {
  if (password == this.password) {
    cb(null, true);
  } else {
    cb('error');
  }
};



module.exports = mongoose.model('User', userSchema);
