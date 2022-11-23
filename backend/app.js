var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
const port = 8081;

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

// Setup the monk MongoDB
let monk = require("monk")
let db = monk('127.0.0.1:27017/EatAtHKU')

// Setup routers.
let indexRouter = require('./routes/index')
let loginRouter = require('./routes/login')
let signupRouter = require('./routes/signup')
let canteensRouter = require('./routes/canteens')

// Connect to db and make them accessible to the routers.
app.use(function(req, res, next) {
    console.log("Binding database reference to request.")
    req.db = db
    next()
})

// Setting up the routers.
app.use('/', indexRouter)
app.use('/login', loginRouter)
app.use('/signup', signupRouter)
app.use('/canteens', canteensRouter)


// for requests not matching the above routes, create 404 error and forward to error handler
app.use(function(req, res, next) {
    next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development environment
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};

    // render the error page
    res.status(err.status || 500);
    res.render('error');
});



app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
})