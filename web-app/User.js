var mongoose = require('mongoose');

// DO NOT CHANGE THE URL FOR THE DATABASE!
// Please speak to the instructor if you need to do so or want to create your own instance
mongoose.connect('mongodb://mongo.cs.swarthmore.edu:27017/group06_ferret');

var Schema = mongoose.Schema;

var userSchema = new Schema({
	username: {type: String, required: true, unique: true},
    password: {type: String, required: true, minlength: 8},
    firstName: {type: String, required: true, unique: true},
    lastName: {type: String, required: true, unique: true},
    orgEmail: {type: String, required: true, unique: true},
    accountsFollowing: {type: Array, required: true},
    postedEvents: {type: Array, required: true},
    attendedEvents: {type: Array, required: true}
    });

// export userSchema as a class called User
module.exports = mongoose.model('User', userSchema);

// this is so that the names are case-insensitive
userSchema.methods.standardizeFName = function() {
    this.firstName = this.firstName.toLowerCase();
    return this.firstName;
}

userSchema.methods.standardizeLName = function() {
    this.lastName = this.lastName.toLowerCase();
    return this.lastName;
}

userSchema.methods.standardizeEmail = function() {
    this.orgEmail = this.orgEmail.toLowerCase();
    return this.orgEmail;
}

