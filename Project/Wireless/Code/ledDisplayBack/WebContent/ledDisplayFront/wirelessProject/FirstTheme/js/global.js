var env = 1, //0本机 1生产
    apiurlArr = ['http://10.1.4.194:8080/ledDisplayBack/', 'http://10.9.3.20:8080/ledDisplayBack/'],
    g={};

    g.doAjax = function (url,_data,func,sync, cache, isGet ) { //封装ajax的一些常用参数  //data数据可以为空
        // _data = $.extend({}, {
        //     "ASYNC": true,
        //     "head": {"token": ""},
        //     // "body":_data
        // } ,_data); //默认参数
        // console.log(_data,11)
        $.ajax({
            // cache: cache,
            // type: isGet ? "GET" : "POST",
            // dataType: "JSON",
            // jsonp: 'callback',
            // jsonpCallback: 'jsonpCallback',
            dataType: 'json',   
            type: 'POST',
            async: true,//异步请求
            url: apiurlArr[env]+url,
            data:_data,
            success: function (res) {
                
                func(res);
            },
            beforSend: function () {
                // 禁用按钮防止重复提交
                // $("#submit").attr({disabled: "disabled"});
            },
            error: function (res) {
                //请求失败时被调用的函数 
                // alert(JSON.stringify(msg));
                console.log(res,"error");
            }
            
        });
       
        return func;
    }

