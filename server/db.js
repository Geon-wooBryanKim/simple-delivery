var mysql = require('mysql');

var pool;

exports.connect = function(done) {
    pool = mysql.createPool({
        connectionLimit: 100,
        host     : 'localhost',
        user     : 'root',
        password : 'wodud1078!',
        database : 'testdb'
    });
}

exports.get = function() {
  return pool;
}