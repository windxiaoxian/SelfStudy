/**
 * Created by 徐文正 on 2019/1/4.
 */
$(function () {
    var clientId = 'console' + parseInt(100 * Math.random()); // 可以改自己喜欢的名字，不冲突就行，最好是页面的名字
    var client = new Messaging.Client(mqHost, Number(mqPort), clientId);
    var destination = 'LED_DISPLAY_MASTER_TOPIC';

    client.connect({
        onSuccess: function (frame) {
            console.log('success connected to ActiveMQ');
            client.subscribe(destination);
        },
        onFailure: function onFailure(failure) {
            console.log('fail connected to ActiveMQ: ' + failure.errorMessage);
        }
    });

    client.onConnectionLost = function onConnectionLost(responseObject) {
        if (responseObject.errorCode !== 0) {
            console.log(client.clientId + ': ' + responseObject.errorCode);
        }
    };

    // 发送MQ消息业务逻辑 begin
    function sendMessage(reqMsg) { //切换主题消息
        var message = new Messaging.Message(JSON.stringify(reqMsg));
        message.destinationName = 'LED_DISPLAY_SERVANT_TOPIC';
        client.send(message);
        console.log('MQ消息(切换大屏主题)发出：' + JSON.stringify(reqMsg));
    };
    // 发送MQ消息业务逻辑 end

    //切换主题事件绑定 begin
    $('#theme1').click(function () {
        var reqMsg = {'msgReceiver': "All", 'msgType': "changeTheme", 'msgValue': "theme1"};
        console.log("切换主题名称：theme1");
        sendMessage(reqMsg);
    });
    $('#theme1_left').click(function () {
        var reqMsg = {'msgReceiver': "Demo_left", 'msgType': "displayDiv", 'msgValue': ""};
        console.log("单独控制主题一左侧页面");
        sendMessage(reqMsg);
    });
    $('#theme2').click(function () {
        var reqMsg = {'msgReceiver': "All", 'msgType': "changeTheme", 'msgValue': "theme2"};
        console.log("切换主题名称：theme2");
        sendMessage(reqMsg);
    });
    //切换主题事件绑定 end
});