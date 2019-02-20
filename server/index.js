var express = require('express');
var bodyParser = require('body-parser');
var app = express();
var db = require('./db');
let jwt = require('jsonwebtoken');
let config = require('./config');
let middleware = require('./middleware');

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
    extended : true
}));

//db.js를 통해서 커넥션 풀을 생성함
db.connect(function(err){
    if(err){
        console.log("db connection fail");
        return ;
    }
    else{
        console.log("db connection success");
    }
});

//5050포트를 열고 클라이언트 접속을 기다림.
app.listen(5050, function(){
    console.log('listening on port 5050');
});

//db에 존재하는 모든 유저들을 미들웨어로 토큰 유효성 검사 후 response로 넘겨준다.
app.get('/user', middleware.checkToken ,function(req, res){
    //db.js의 get함수를 통해서 미리 생성해두었던 커넥션 풀을 사용함
    db.get().query('SELECT * FROM user', function(error, results, fields){
        if(error){
            throw error;
        }
        console.log("verify success! user id = " + req.decoded.id);
        res.status(200).send(results);
        console.log(results);
        console.log(req.query.phone);
    });
});

//토큰이 유효한지 확인하고 유저의 id값을 response로 넘겨준다.
app.get('/me', middleware.checkToken, function(req, res){
    var userId = req.decoded.id;
    res.json({
        id : userId
    });
});

//클라이언트로 부터 온 심부름 정보를 errand 테이블에 저장함.
app.post('/errand', middleware.checkToken, function(req, res){
    var buyer_id = req.decoded.id; //토큰에 해당하는 유저의 id값을 가져옴
    var destination = req.body.destination;
    var latitude = req.body.latitude;
    var longitude = req.body.longitude;
    var price = req.body.price;
    var contents = req.body.contents;

    var queryString = "INSERT INTO errand (buyer_id, destination, latitude, longitude, price, contents) VALUES (?, ?, ?, ?, ?, ?)";
    db.get().query(queryString, [buyer_id, destination, latitude, longitude, price, contents], function(err, result){
        if(err){
            console.log('error is occured');
            return res.sendStatus(400);
        }
        console.log('errand insert success!')
        res.sendStatus(200);
    })
});
//post방식으로 날라온 request의 body에서 이메일과 비밀번호를 꺼내 로그인 기능을 함
app.post('/user/login', function(req, res){
    var email = req.body.email;
    var password = req.body.password;
 
    var queryString = "SELECT * FROM user WHERE email = ?";
 
    db.get().query(queryString, email, function(err, result){
        if(err){
            console.log('error is occured');
            console.log(err);
            return res.sendStatus(400);
        }
        //가입된 email이 존재하는 않는 경우 response body에 false를 전달하고 함수 종료
        if(result.length === 0){
            console.log('Email is not exist');
            return res.json({
                success: false,
                message: 'Email is not exist'
            });
        }

        //로그인이 성공한 경우 jwt를 통해 토큰을 만들고 res에 담아서 보내준다.
        if(result[0].password === password){
            let token = jwt.sign({id: result[0].id}, config.secret, {expiresIn: '24h'});

            res.json({
                success: true,
                message: 'Authentication successful',
                token: token
            });
        }
        
        //비밀번호가 불일치 하는 경우
        else{
            res.json({
                success: false,
                message: 'Incorrect password'
            });
        }
    });
 });

 //post방식으로 날라온 request의 body에서 이메일과 핸드폰번호 비밀번호를 꺼내 로그인 기능을 함
app.post('/user/signup', function(req, res){
   var email = req.body.email;
   var phone = req.body.phone;
   var password = req.body.password;

   var queryString = "insert into user (email, password, phone) values(?, ?, ?);";

   db.get().query(queryString, [email, password, phone], function(err, result){
       if(err){
           console.log('error is occured');
           return res.sendStatus(400);
       }
       res.sendStatus(200);
       console.log('signup success');
   });
});
 
module.exports = app;