var mysql      = require('mysql');
var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : 'wodud1078!',
  database : 'testdb'
});
 
connection.connect(function(err){
    if(err){
        console.error('error connectiong' + err.stack);
        return ;
    }
    console.log('connected as id : ' + connection.threadId);
});
 
connection.query('SELECT * FROM user', function (error, results, fields) {
  if (error) {
      throw error;
  }
  console.log('The solution is: ', results);
});
 
connection.end();