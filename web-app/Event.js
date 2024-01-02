var mongoose = require('mongoose');

// DO NOT CHANGE THE URL FOR THE DATABASE!
// Please speak to the instructor if you need to do so or want to create your own instance
mongoose.connect('mongodb://mongo.cs.swarthmore.edu:27017/group06_ferret');

var Schema = mongoose.Schema;

var eventSchema = new Schema({
	title: {type: String, required: true},
	category: {type: String, required: true},
	host: {type: String, required: true},
	location: {type: String, required: true},
	date: {type: Date, required: true},
    startTime:{type: String, required: true},
    endTime:{type: String, required: true},
	toBring: {type: String, required: true},
	intendedAudience: {type: String, required: true},
	accessibilityInfo: {type: String, required: true},
	additionalDescription: {type: String, required: true},
    attendees : {type: Array, required: true},
	username: {type: String, required: false}
    });

// export userSchema as a class called User
module.exports = mongoose.model('Event', eventSchema);

// this is so that the names are case-insensitive
eventSchema.methods.standardizeTitle = function() {
    this.title = this.title.toLowerCase();
    return this.title;
}

//do we need to standardize category? host? etc?

