$(function () {
    var clientId = 'middle' + parseInt(100 * Math.random()); // 可以改自己喜欢的名字，不冲突就行，最好是页面的名字
    var client = new Messaging.Client(mqHost, Number(mqPort), clientId);
    var destination = 'LED_DISPLAY_SERVANT_TOPIC';

    client.connect({
        onSuccess: function (frame) {
            console.log('success connected to ActiveMQ');
            client.subscribe(destination);
        },
        onFailure: function onFailure(failure) {
            console.log('fail connected to ActiveMQ: ' + failure.errorMessage);
        }
    });

    client.onMessageArrived = function onMessageArrived(message) {
        // 接收业务逻辑
        console.log('接收到ActiveMQ信息(Demo主题)：' + JSON.stringify(JSON.parse(message.payloadString)));
        var msg = JSON.parse(message.payloadString);
        var self_href = window.location.href.substring(0, window.location.href.lastIndexOf('demoProject'));
        if (msg.msgReceiver == 'All') {//群发消息
            if (msg.msgType == 'changeTheme') {
                if (msg.msgValue == 'theme1') {
                    $(window).attr('location', self_href + 'demoProject/DemoTheme/html/middleDemo.html');
                } else if (msg.msgValue == 'theme2') {
                    $(window).attr('location', self_href + 'demoProject/DemoTheme/html/middleDemo2.html');
                }
            }
        } else if (msg.msgReceiver == document.title) {//点对点消息
            if (msg.msgType == 'displayDiv') {
                if ($(".displayDiv").css("display") == "none") {
                    $(".displayDiv").css("display", 'block');
                } else {
                    $(".displayDiv").css("display", 'none');
                }
            }
        }
    };

    client.onConnectionLost = function onConnectionLost(responseObject) {
        if (responseObject.errorCode !== 0) {
            console.log(client.clientId + ': ' + responseObject.errorCode);
        }
    };

    var dataInfo = {'dbType': dbType};
    getData(ajaxUrl, 'middle', dataInfo);
});

function getData(ajaxUrl, keyInfo, dataInfo) {
    $.ajax({
        // url: ajaxUrl + '/servlet/DemoServlet?key=' + keyInfo + '&param=' + JSON.stringify(dataInfo),
        // type: 'GET',
        url: ajaxUrl + '/servlet/DemoServlet',
        type: 'POST',
        dataType: "JSONP",
        jsonp: 'callback',
        jsonpCallback: 'jsonpCallback',
        async: true,//异步请求
        data: {
            key: keyInfo,
            param: JSON.stringify(dataInfo)
        },
        success: function (data) {
            show(data);
        },
        error: function (msg) {
            console.log('请求失败');
        }
    });
}

function show(data) {
    $('.middleTitle').append(' 屏幕位置：' + data.location + '<br /> ' + data.today);
}

//数字加速动画
function NumFlash() {
    $(".counter").counterUp({
        delay: 50,
        time: 2000
    })
}

//空值处理
function nvl(object, returnInfo) {
    if (object == undefined || object == null) {
        return returnInfo;
    } else {
        return object;
    }
}