// set up Express
var express = require('express');
var app = express();

// set up BodyParser
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));

// set up EJS
app.set('view engine', 'ejs');

// import the Person class from Person.js
var Event = require('./Event.js');
var User = require('./User.js');

app.use('/test', (req, res) => {
	res.json( { "message" : "it works!" } ) 
})


//endpoint for creating user with events
app.use('/createUserWithEvents', (req, res) => {

	var filter = {'username': "usernumberThree"};
	
		Event.find({})
		.then((events) =>{

			console.log("found events")

			update = {'username': "josephTest", 
			'password': "jojojo",
			'firstName': "Joseph",
				
			'lastName': "Kim",
			'orgEmail': "jkim5@haverford.edu",
			isAdmin: false,
			accountsFollowing: [],
			postedEvents: events,
			attendedEvents: []}

			User.findOneAndReplace(filter, update) 
			.then((user) => {
				console.log("updated user!")
			})
		})
})

app.use('/appMyEvents', (req, res) => {
	var filter = {'username' : req.query.username}

	Event.find(filter)
	.then((events) => {
		res.json(events)
	})
	.catch((err) => {
		res.type('html').status(200);
		   console.log('uh oh: ' + err);
		   res.send(err);
	})
})

app.use('/appEditEvent', (req, res) => {
	
	var filter = {'title' : req.query.title}

	var update = { 'title' : req.query.title, 'category': req.query.category, 'host': req.query.host, 
	'location': req.query.location, 'date': req.query.date, 'startTime': req.query.startTime, 
	'endTime': req.query.endTime, 'toBring': req.query.toBring, 'intendedAudience': req.query.intendedAudience,
	'accessibilityInfo': req.query.accessibilityInfo, 'additionalDescription': req.query.additionalDescription, 
	'username' : req.query.userName}
	// now update the person in the database
	Event.findOneAndUpdate(filter, update)
	.then((orig) => { 
		res.json({'result' : true})
	})
	.catch((err) => {
		res.type('html').status(200);
		console.log('uh oh: ' + err);
		res.json({'result' : false});
	})
})

//endpoint for the homepage
app.get('/', (req, res)=>{
	res.render('homepage');
})




// endpoint for listing all events
app.use('/all', (req, res) => {

	//current date
	var present = new Date();

	//get all events
	Event.find({})
		// sorting by increasing date and start time
		.sort({date: 1})
		.then((events) => {
			var presentEvents = events.filter((event) => event.date >= present);
			res.render('presentevents', {'events' : presentEvents})
		})
		.catch((err) => {
			res.type('html').status(200);
		    console.log('uh oh: ' + err);
		    res.send(err);
		})
})
// endpoint for listing all users
app.use('/allUsers', (req, res) => {
	User.find({})
		//sort alphabetically by last name
		.sort({lastName: 1})
		.then((users) => {
			res.render('allusers', {'users': users})
		})
		.catch((err) => {
			res.type('html').status(200);
		    console.log('uh oh: ' + err);
		    res.send(err);
		})
})
app.use('/create', (req, res) => {
	// construct the Event from the form data which is in the request BODY

	var newEvent = new Event ({
		title: req.body.title,
		category: req.body.category,
        host: req.body.host,
        location: req.body.location,
        date: req.body.date, // how to set time zone to eastern?
        startTime: req.body.startTime,
        endTime: req.body.endTime,
        toBring: req.body.toBring,
        intendedAudience: req.body.intendedAudience,
        accessibilityInfo: req.body.accessibilityInfo,
        additionalDescription: req.body.additionalDescription,
        attendees: []
	    });

	// write it to the database
	newEvent.save()
		.then((e) => { 
			console.log('successfully added ' + e.title + ' to the database'); 
			// use EJS to render the page that will be displayed
			res.render('newevent', {'event': e})
		} )
		.catch((err) => { 
			res.type('html').status(200);
		    console.log('uh oh: ' + err);
		    res.send(err);
		})
	});

    app.use('/delete', (req, res) => {
        var title = req.query.title
        var filter = { 'title' : title };
        Event.deleteOne(filter)
        .then(() => {
            console.log('successfully deleted ' + title + ' from the database');
            res.render('deletedEvent', {'title' : title})
        })
        .catch((err) => {
            res.type('html').status(200);
            console.log('uh oh: ' + err);
            res.send(err);
        })
    })

//delete user endpoint
app.use('/deleteUser', (req, res) => {
	var username = req.query.username
	var filter = {'username': username};
	User.deleteOne(filter)
	.then(() => {
		console.log('successfully deleted ' + username + ' from the database');
		res.render('deletedUser', {'username': username})
	})
	.catch((err) => {
		res.type('html').status(200);
		console.log('uh oh: ' + err);
		res.render(err);
	})
})

// this endpoint is called when the user SUBMITS the form to edit an event
app.use('/edit', (req, res) => {
	// get the name and age from the BODY of the request
	var filter = { 'title' : req.body.title };
	var date = { 'date' : req.body.date };
	var update = { 'title' : req.body.title, 'category': req.body.category, 'host': req.body.host, 
	'location': req.body.location, 'date': new Date(req.body.date), 'startTime': req.body.startTime, 
	'endTime': req.body.endTime, 'toBring': req.body.toBring, 'intendedAudience': req.body.intendedAudience,
	'accessibilityInfo': req.body.accessibilityInfo, 'additionalDescription': req.body.additionalDescription};
	// now update the person in the database
	Event.findOneAndUpdate(filter, update)
	.then((orig) => { // 'orig' refers to the original object before we updated it
		res.render('editedevent', {'title' : req.body.title, 'category': req.body.category, 'host': req.body.host, 
		'location': req.body.location, 'date': req.body.date, 'startTime': req.body.startTime, 
		'endTime': req.body.endTime, 'toBring': req.body.toBring, 'intendedAudience': req.body.intendedAudience,
		'accessibilityInfo': req.body.accessibilityInfo, 'additionalDescription': req.body.additionalDescription})
	})
	.catch((err) => {
		res.type('html').status(200);
		console.log('uh oh: ' + err);
		res.send(err);
	})

})

app.use('/loginRequest', async (req, res) => {
	try {
        const givenusername = req.query.username;
	    const givenPassword = req.query.password;
        console.log(givenusername);
        console.log(givenPassword);

        
	    const filter = {'username' : givenusername, 'password': givenPassword};

        const userExists = await User.findOne(filter);

        console.log(userExists);

        if (userExists) {res.send({'result': "success"});}
        else {res.send({'result': "failure"});}

    } catch (err) {
        console.log('Error: ' + err);
        res.status(500).send('Internal Server Error');
    }

})

//View past events
app.use('/pastevent', (req, res) => {
	//current date
	present = new Date();
	//get all
	Event.find({})
	//asc if want desc do -1
		.sort({date: -1})
		.sort({endTime: -1})
		//each event
		.then((events) => {
			//if less than current date filter will return array .find returns
			//find will stop after one match
			pastEvents = events.filter((event) => event.date < present);
				//console.log(pastEvents);
				res.render('pastevent', {'pastevents' : pastEvents})
		})
		.catch((err) => {
			res.type('html').status(200);
		    console.log('uh oh: ' + err);
		    res.send(err);
		})
})
app.use('/changePasswordRequest', async(req, res) => {
	try{
		const newPassword = req.query.newPassword;
		const username = req.query.username;

		//find user
		const user = await User.findOne({ username: req.query.username });
		//console.log(user);
		const currentPassword = user.password;
		// console.log("username is: " + username);
		// console.log("current password is: " + currentPassword);
		// console.log("new password is: " + newPassword);
		//update user
		if(user){
			user.password = newPassword;
			await user.save();
            res.json({ result: "true" });
		}
		else{
			res.json({ result: "false" });
		}
	}
	catch(err){
		console.log('Error: ' + err);
        res.status(500).send('Internal Server Error');
	}
})

app.use('/registerRequest', async (req, res) => {
	try {
        const givenusername = req.query.username;
	    const givenFName = req.query.firstName;
	    const givenLName = req.query.lastName;
	    const givenOrgEmail = req.query.orgEmail;
	    const givenPassword = req.query.password;
        //at this point they all contain something so we do not need to check
        
        //we must check if the username or email or full name is in use
        //case insensitive queries!
	    const usernameFilter = {'username' : { $regex : new RegExp(givenusername, "i") }};
        const nameFilter = {'firstName' : { $regex : new RegExp(givenFName, "i") }, "lastName" : { $regex : new RegExp(givenLName, "i") }};
        const orgEmailFilter = {"orgEmail" : { $regex : new RegExp(givenOrgEmail, "i") }};

        var userExists = "false";
        var error = "none";

        const existingUser = await User.findOne({
            $or: [usernameFilter, nameFilter, orgEmailFilter]
        });

        if (existingUser) {
            userExists = "true";
            // console.log(existingUser);
            if (existingUser.username.toLowerCase() === givenusername.toLowerCase()) {
                userExists = true;
                error = "username";
            } else if (existingUser.orgEmail.toLowerCase() === givenOrgEmail.toLowerCase()) {
                userExists = true;
                error = "email";
            } else {
                userExists = true;
                error = "first and last name";
            }
        } else {
            const newUser = new User({
                username: givenusername,
                firstName: givenFName,
                lastName: givenLName,
                orgEmail: givenOrgEmail,
                password: givenPassword
            });

            await newUser.save();
            console.log('successfully added ' + newUser.username + ' to the database');
        }
        res.send({ 'result': userExists, 'error': error });
    } catch (err) {
        console.log('Error: ' + err);
        res.status(500).send('Internal Server Error');
    }

}) 
//////APP ADD

app.use('/appPostEvent', (req, res) => {
	// construct the Event from the form data which is in the request BODY
	var newEvent = new Event ({
		title: req.query.title,
		category: req.query.category,
		host: req.query.host,
		location: req.query.location,
		date: req.query.date, // how to set time zone to eastern?
		startTime: req.query.startTime,
		endTime: req.query.endTime,
		toBring: req.query.toBring,
		intendedAudience: req.query.intendedAudience,
		accessibilityInfo: req.query.accessibilityInfo,
		additionalDescription: req.query.additionalDescription,
		attendees: [],
		username: req.query.username
		});

	// write it to the database
	//console.log(newEvent); 
	newEvent.save()
		//promise chain
		.then((e) => { 
			console.log(e); 
			console.log('successfully added ' + e.title + ' to the database'); 
			//return promise
			return User.findOneAndUpdate(
				//find/fliter
				{ username: req.query.username },
				//push new/update "The operator appends a specified value to an array.""
				{ $push: { postedEvents: e} },
				//return updated/ options
				{ returnDocument: 'after' }
			);
		} )//then pass the user and grab the posted event
		.then((updatedUser) => {
            // use EJS to render the page that will be displayed
            res.render('newevent', { 'event': updatedUser.postedEvents[updatedUser.postedEvents.length - 1] });
        })
		//if any promise is rejected
		.catch((err) => { 
			res.type('html').status(200);
			console.log('uh oh: ' + err);
			res.send(err);
		})
	});

	app.use('/appView', (req, res) => {

		//current date
		var present = new Date();
	
		//get all events
		Event.find({})
			// sorting by increasing date and start time
			.sort({date: 1})
			.then((events) => {
				var presentEvents = events.filter((event) => event.date >= present);
				//json
				res.json({'events' : presentEvents})
				//console.log(presentEvents);
			})
			.catch((err) => {
				//error???
				res.type('html').status(200);
				console.log('uh oh: ' + err);
			})
	})

	app.use('/appSearch', (req, res) => {
		var host = req.query.host;
		var location = req.query.location;
		var date = req.query.date;
		var startTime = req.query.startTime;
		var category = req.query.category;
		var title = req.query.title;
		console.log(location)
	
		var filter = {}

		if (startTime) {
			filter['startTime'] = startTime;
		}
	
		if (date) {
			filter['date'] = new Date(date);
		}
	
		Event.find(filter)
			//.sort({date: 1})
			.then((events) => {
				var searchEvents = events;
				if (title) {
					searchEvents = searchEvents.filter((event) => event.title.toLowerCase().includes(title.toLowerCase()));
				}
				if (location) {
					searchEvents = searchEvents.filter((event) => event.location.toLowerCase().includes(location.toLowerCase()));
				}
				if (host) {
					searchEvents = searchEvents.filter((event) => event.host.toLowerCase().includes(host.toLowerCase()));
				}
				if (category) {
					searchEvents = searchEvents.filter((event) => event.category.toLowerCase().includes(category.toLowerCase()));
				}
				console.log(searchEvents)
				res.status(200).json({'events': searchEvents});
			})
			.catch((err) => {
				res.type('html').status(200);
				console.log('uh oh: ' + err);
				res.send(err);
			})
	}) 


app.use('/search', (req, res) => {
	
	var category = req.body.category;
	var date = req.body.date;
	var keyword = req.body.keyword;

	var filter = {}
	filter['category'] = category;

	if (date) {
		filter['date'] = new Date(date);
	}

	Event.find(filter)
		.then((events) => {
			var searchEvents = events;
			if (keyword) {
				searchEvents = events.filter((event) => event.title.toLowerCase().includes(keyword.toLowerCase()));
			}
			res.render('searchevents', {'events' : searchEvents})
		})
		.catch((err) => {
			res.type('html').status(200);
		    console.log('uh oh: ' + err);
		    res.send(err);
		})
}) 

//this endpoint is for editing a person
// this one shows the HTML form for editing the person
//editing the events - Bella
app.use('/showEditForm', (req, res) => {
	var filter = { 'title' : req.query.title };
	// do a query to get the info for this person
	Event.findOne(filter)
	.then((e) => {
		// then show the form from the EJS template
		res.render('editevents', {'event' : e})
	})
	.catch((err) => {
		res.type('html').status(200);
		console.log('uh oh: ' + err);
		res.send(err);
	})
})

//View info about event 
app.use('/viewinfo', (req, res) => {
    var title = req.query.title
    var filter = { 'title' : title };
 Event.findOne(filter)
  .then((event) => {
   res.render('viewinfo', {'event' : event, 'requestedtitle' : title})
  })
  .catch((err) => {
   res.type('html').status(200);
      console.log('uh oh: ' + err);
      res.send(err);
  })
})

app.use('/viewUser', (req, res) =>{
	var username = req.query.username
	var filter = {'username': username};
User.findOne(filter)
	.then((u) => {
		res.render('viewusers', {'u': u, 'username': username})
	})
	.catch((err) => {
		res.type('html').status(200);
		   console.log('uh oh: ' + err);
		   res.send(err);
	})
})

	
/*************************************************
Do not change anything below here!
*************************************************/

app.use('/public', express.static('public'));

// this redirects any other request to the "all" endpoint
app.use('/', (req, res) => { res.redirect('/all'); } );

// this port number has been assigned to your group
var port = 3006

app.listen(port,  () => {
	console.log('Listening on port ' + port);
    });
