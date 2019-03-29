let mqhost='127.0.0.1',mqport=61614,reconnectTimeout=2000, useTLS = false,
username = null,
password = null,
cleansession = false,
renderDateTime = function() {
    let date = new Date(),
        month = date.getMonth() + 1,
        day=date.getDate(),
        hour=date.getHours();
        minutes =date.getMinutes(),
        seconds = date.getSeconds();

    $(".timePanel>.timePanelContent").text(
        [
            date.getFullYear(),
            "-",
            month <= 9 ? "0" + month : month,
            "-",
            day <= 9 ? "0" + day : day,
            " ",
            ["星期日","星期一","星期二","星期三","星期四","星期五","星期六"][date.getDay()],
            " ",
            hour <= 9 ? "0" + hour : hour,
            ":",
            minutes <= 9 ? "0" + minutes : minutes,
            ":",
            seconds <= 9 ? "0" + seconds : seconds,
        ].join("")
    );
}, uuid =function()  {
        var uid = [];
        var hexDigits = "0123456789abcdefghijklmnopqrst";
        for (var i = 0; i < 32; i++) {
            uid[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
        }
        uid[6] = "4";
        uid[15] = hexDigits.substr((uid[15] & 0x3) | 0x8, 1);
        var uuid = uid.join("");
        return uuid;
    },getTopic=function(topic){

       let client = new Paho.MQTT.Client(  //实例化一个对象
            mqhost,
            mqport,
            "client" + uuid(),  //防止多个浏览器打开，导致的问题，保证唯一性
        ), options = {
            timeout: 100,
            useSSL: useTLS,
            cleanSession: cleansession,
            onSuccess: function () {
                client.subscribe(topic, {
                    qos: 2
                });
                //发布一个消息，再连接成功后，发送一个响应，确保连接没有问题；
            },
            onFailure: function (message) {
                //连接失败定时重连
                setTimeout(MQTTconnect, reconnectTimeout);
            }
        };
        client.onConnectionLost = function(response) {
            if (response.errorCode !== 0) {
                log("onConnectionLost:"+response.errorMessage);
                log("连接已断开");
            }
        };

        if (username != null) {
            options.userName = username;
            options.password = password;
        }
        try {
            client.connect(options);
        }catch (e) {
            log(e.message)
        }
        return client;
    },sendTopicMsg=function(client,topic,message){
        let msg = new Paho.MQTT.Message(message);
        msg.destinationName = topic;
        log('send stationCode:' + message);
        try {
            client.send(msg);
        }catch (e) {
            log(e.message)
        }
    };
