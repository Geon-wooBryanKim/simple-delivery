var mysql = require("mysql");
var config = require("./config.js");
var pool;

exports.connect = function(done) {
    pool = mysql.createPool({
        connectionLimit: 100,
        host: config.host,
        user: config.user,
        password: config.password,
        database: config.database
    });
};

exports.get = function() {
    return pool;
};
