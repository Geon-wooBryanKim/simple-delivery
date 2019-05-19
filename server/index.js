var express = require("express");
var bodyParser = require("body-parser");
var app = express();
var db = require("./db");
var FCM = require("fcm-node");
let jwt = require("jsonwebtoken");
let config = require("./config");
let middleware = require("./middleware");
var server = require("http").Server(app);
var io = require("socket.io")(server);

app.use(bodyParser.json());
app.use(
    bodyParser.urlencoded({
        extended: true
    })
);

//db.js를 통해서 커넥션 풀을 생성함
db.connect(function(err) {
    if (err) {
        console.log("db connection fail");
        return;
    } else {
        console.log("db connection success");
    }
});

server.listen(6060);

//
io.on("connection", function(socket) {
    socket.emit("new message", { username: "shim jae young", message: "hello" });

    socket.on("sendToSomeone", function(message, destinationId) {
        var queryString = "SELECT socket_id FROM USER WHERE ID = ?";
        db.get().query(queryString, destinationId, function(err, results) {
            if (err) {
                console.log(err);
                return;
            }

            console.log("message from client > " + message);
            io.to(results[0].socket_id).emit("new message", message);
        });
    });

    socket.on("update socket_id", function(userId) {
        var queryString = "UPDATE user SET socket_id = ? WHERE id = ?";
        db.get().query(queryString, [socket.id, userId], function(err, results) {
            if (err) {
                console.log(err);
                return;
            }
            console.log("user " + userId + " socket_id is updated!");
        });
    });
});

//5050포트를 열고 클라이언트 접속을 기다림.
app.listen(5050, function() {
    console.log("listening on port 5050");
});

//db에 존재하는 모든 유저들을 미들웨어로 토큰 유효성 검사 후 response로 넘겨준다.
app.get("/user", middleware.checkToken, function(req, res) {
    //db.js의 get함수를 통해서 미리 생성해두었던 커넥션 풀을 사용함
    db.get().query("SELECT * FROM user", function(error, results, fields) {
        if (error) {
            throw error;
        }
        console.log("verify success! user id = " + req.decoded.id);
        res.status(200).send(results);
        console.log(results);
        console.log(req.query.phone);
    });
});

//토큰이 유효한지 확인하고 유저의 id값을 response로 넘겨준다.
app.get("/me", middleware.checkToken, function(req, res) {
    var userId = req.decoded.id;
    res.json({
        id: userId
    });
});

//req에 "fcmToken" query로 fcmToken값을 db에 업데이트
app.patch("/me", middleware.checkToken, function(req, res) {
    var fcmToken = req.query.fcmToken;
    var id = req.decoded.id;

    console.log(fcmToken, id);

    var queryString = "UPDATE user SET fcm_token = ? WHERE id = ?";

    db.get().query(queryString, [fcmToken, id], function(err, results) {
        if (err) {
            res.sendStatus(400);
            console.log(err);
            return;
        }
        res.sendStatus(200);
        console.log("update me success");
    });
});

//클라이언트로 부터 온 심부름 정보를 errand 테이블에 저장함.
app.post("/errand", middleware.checkToken, function(req, res) {
    var buyer_id = req.decoded.id; //토큰에 해당하는 유저의 id값을 가져옴
    var destination = req.body.destination;
    var latitude = req.body.latitude;
    var longitude = req.body.longitude;
    var price = req.body.price;
    var contents = req.body.contents;
    var status = 0; // 현재 심부름의 상태 0 = 진행 전, 1 = 진행 중, 2 = 진행 완료

    var queryString =
        "INSERT INTO errand (buyer_id, destination, latitude, longitude, price, contents) VALUES (?, ?, ?, ?, ?, ?)";
    db.get().query(queryString, [buyer_id, destination, latitude, longitude, price, contents], function(err, result) {
        if (err) {
            console.log("error is occured");
            return res.sendStatus(400);
        }
        console.log("errand insert success!");
        res.sendStatus(200);
    });
});

app.get("/errand", function(req, res) {
    //porter_id가 NULL인것만 조회함 아직 매칭되지 않은 심부름만 조회
    var queryString = "SELECT * FROM errand WHERE porter_id IS NULL";
    var ary = [];

    db.get().query(queryString, function(err, results) {
        if (err) {
            console.log("GET ERRAND FAIL :", err);
        }
        console.log(JSON.stringify(results));
        res.json(results);
    });
});

//나와 관계된 심부름만 조회 (내가 의뢰받은 심부름이거나, 주문한 심부름)
app.get("/errand/me", middleware.checkToken, function(req, res) {
    var id = req.decoded.id;
    var queryString = "SELECT * FROM errand WHERE buyer_id = ? OR porter_id = ?";
    var ary = [];

    db.get().query(queryString, [id, id], function(err, results) {
        if (err) {
            console.log("GET MY ERRAND FAIL :", err);
        }
        console.log(JSON.stringify(results));
        res.json(results);
    });
});

//post방식으로 날라온 request의 body에서 이메일과 비밀번호를 꺼내 로그인 기능을 함
app.post("/user/login", function(req, res) {
    var email = req.body.email;
    var password = req.body.password;

    var queryString = "SELECT * FROM user WHERE email = ?";

    db.get().query(queryString, email, function(err, result) {
        if (err) {
            console.log("error is occured");
            console.log(err);
            return res.sendStatus(400);
        }
        //가입된 email이 존재하는 않는 경우 response body에 false를 전달하고 함수 종료
        if (result.length === 0) {
            console.log("Email is not exist");
            return res.json({
                success: false,
                message: "Email is not exist"
            });
        }

        //로그인이 성공한 경우 jwt를 통해 토큰을 만들고 res에 담아서 보내준다.
        if (result[0].password === password) {
            let token = jwt.sign({ id: result[0].id }, config.secret, { expiresIn: "24h" });

            res.json({
                success: true,
                message: "Authentication successful",
                token: token
            });
        }

        //비밀번호가 불일치 하는 경우
        else {
            res.json({
                success: false,
                message: "Incorrect password"
            });
        }
    });
});

//post방식으로 날라온 request의 body에서 이메일과 핸드폰번호 비밀번호를 꺼내 로그인 기능을 함
app.post("/user/signup", function(req, res) {
    var email = req.body.email;
    var phone = req.body.phone;
    var password = req.body.password;

    var queryString = "insert into user (email, password, phone) values(?, ?, ?);";

    db.get().query(queryString, [email, password, phone], function(err, result) {
        if (err) {
            console.log("error is occured");
            return res.sendStatus(400);
        }
        res.sendStatus(200);
        console.log("signup success");
    });
});

//심부름을 심부름꾼이 수락했을 때 심부름 레코드의 porter_id를 설정해주고 심부름 상태를 진행중으로 UPDATE
app.patch("/errand", middleware.checkToken, function(req, res) {
    var id = req.query.id;
    var porter_id = req.decoded.id;
    console.log(id, porter_id);
    // var porter_id = req.decoded.id;
    var status = 1; // 진행 전 = 0, 진행 중 = 1, 진행 완료 = 2

    var queryString = "UPDATE errand SET porter_id = ? WHERE id = ?";
    var queryString2 = "UPDATE errand SET status = ? WHERE id = ?";

    db.get().query(queryString, [porter_id, id], function(err, results) {
        if (err) {
            console.log("erros occured");
        }
        db.get().query(queryString2, [status, id], function(err, results) {
            if (err) {
                console.log("erros occured");
            }
            res.sendStatus(200);
            console.log("update success");
        });
    });
});

app.get("/message", middleware.checkToken, function(req, res) {
    var id = req.decoded.id;
    var queryString = "SELECT * FROM message WHERE sender_id = ? OR reciever_id = ?";

    db.get().query(queryString, [id, id], function(err, results) {
        if (err) {
            console.log("GET MESSAGE FAIL :", err);
        }
        console.log(JSON.stringify(results));
        res.json(results);
    });
});

app.post("/message", middleware.checkToken, function(req, res) {
    var sender_id = req.body.sender_id;
    var reciever_id = req.body.reciever_id;
    var contents = req.body.contents;
    var timestamp = req.body.timestamp;

    var queryString = "INSERT INTO (sender_id, reciever_id, contents, timestamp) message VALUES (?, ?, ?, ?)";

    db.get().query(queryString, [sender_id, reciever_id, contents, timestamp], function(err, results) {
        if (err) {
            console.log("POST MESSAGE FAIL :", err);
        }
        console.log("POST MESSAGE SUCCESS");
        res.sendStatus(200);
    });
});

//쿼리로 buyer_id를 받아 그에 해당하는 fcmToken을 찾아서 fcm을 전송함
app.post("/fcm", middleware.checkToken, function(req, res) {
    var queryString = "SELECT * FROM user WHERE id = ?";
    var id = req.query.buyer_id;
    db.get().query(queryString, id, function(err, results) {
        if (err) {
            res.sendStatus(400);
            return;
        }
        var fcmToken = results[0].fcm_token;
        console.log(fcmToken);
        var push_data = {
            // 수신대상
            to: fcmToken,
            // App이 실행중이지 않을 때 상태바 알림으로 등록할 내용
            notification: {
                title: "Hello Node",
                body: "Node로 발송하는 Push 메시지 입니다.",
                sound: "default",
                click_action: "FCM_PLUGIN_ACTIVITY",
                icon: "fcm_push_icon"
            },
            // 메시지 중요도
            priority: "high",
            // App 패키지 이름
            restricted_package_name: "com.example.shim.simpledelivery",
            // App에게 전달할 데이터
            data: {
                num1: 2000,
                num2: 3000
            }
        };
        var fcm = new FCM(config.fcm_api_key);
        fcm.send(push_data, function(err, response) {
            if (err) {
                console.log(err);
                return;
            }
            console.log("send gcm success");
            console.log(response);
            res.sendStatus(200);
        });
    });
});

module.exports = app;
