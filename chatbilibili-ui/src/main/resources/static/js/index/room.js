<!-- 连接相关 -->

// 连接按钮点击事件
$(document).on('click', '.connectButton', function (e) {
    console.log("点击了连接按钮。")

    var inputRoomId = $('.connectInput').val();

    if(inputRoomId == '') {
        console.log("没有输入直播间号码");
    } else {
        console.log("连接中...");

        $.when(connectMethod.connectRoom(inputRoomId).done(function(data) {

            if (data.code === "200") {
                var roomInfo = data.result;
                alert("连接成功");

                $('.connectSegment').addClass('dimmer');
                $('.afterConnectSegment').removeClass('dimmer');
                // 修改内容
                $('.connectStatusSpan').text("tips:根据B站官方隐私保护规则，如果没有登录，接收到的弹幕，用户名可能会带有星号。");
                $('.roomInfoSegment').empty();
                var connectRoomInfo  = `<span>` +
                                            "已连接到直播间：" +
                                           `<a href=`  + "https://live.bilibili.com/" + roomInfo.roomId  + `>` +
                                                                                           roomInfo.roomId +
                                                                                       `</a>` +
                                           "，UP主：" +
                                            `<a href=`  + "https://space.bilibili.com/" + roomInfo.anchorUid  + `>` +
                                                roomInfo.anchorName +
                                            `</a>` +
                                            "，当前直播状态：" + roomInfo.liveStatus +
                                         `</span>`;
                $('.roomInfoSegment').append(connectRoomInfo);

            } else {
                alert("连接失败");
            }
        }));
    }
});

// 关闭连接按钮点击事件
$(document).on('click', '.closeConnectButton', function (e) {
    console.log("关闭连接");

    var result = connectMethod.closeConnectRoom();


    if (result) {
        $('.roomInfoSegment').empty();
        var connectRoomInfo  = `<a href="https://live.bilibili.com/">B站直播房间</a>`;
        $('.roomInfoSegment').append(connectRoomInfo);
        // ajax 成功的话执行以下语句
        $('.connectStatusSpan').text("当前未连接到任何房间...");
        $('.connectSegment').removeClass('dimmer');
        $('.afterConnectSegment').addClass('dimmer');
        $('.danmuSegment').addClass('dimmer');
        $('.showDanmuButton').text('显示弹幕');
        $('.connectStatusContentSegment').removeClass('dimmer');
        $('.connectStatusContentSegment').removeClass('dimmer');
        refresh = false;
    }
});

// 显示弹幕或刷新
$(document).on('click', '.showDanmuButton', function (e) {
    console.log("点击显示弹幕/刷新");
    if($('.showDanmuButton').text() == "刷新") {
        $('.danmuMessageBoard').empty();
    } else {
        $('.danmuMessageBoard').empty();
        var flag = connectMethod.startReceiveDanmu();
        if(flag) {
            // 连接websocket服务器，开始接受弹幕
            var localWebsocketUrl = "ws://localhost:1999/chatbilibili/danmu/sub";
            openWebsocketConnection(localWebsocketUrl, null);
        }
    }
});

const connectMethod = {

    connectRoom : function(roomId) {
        "use strict";
        var deferred = $.Deferred();
        $.ajax({
            url : './rest/room/connectRoom',
            data : {
                roomId : roomId,
            },
            async : true,
            cache : false,
            type : 'GET',
            dataType : 'json',
            success : function(data) {
                deferred.resolve(data);
            }
        });
        return deferred.promise();
    },

    closeConnectRoom : function() {
        "use strict";
        var flag = false;
        $.ajax({
            url : './rest/room/closeConnection',
            async : false,
            cache : false,
            type : 'GET',
            dataType : 'json',
            success : function(data) {
                if (data.code === "200") {
                    flag = data.result;
                }
            }
        });
        return flag;
    },

    connectCheck : function() {
        "use strict";
        var flag = false;
        $.ajax({
            url : './connectCheck',
            async : false,
            cache : false,
            type : 'GET',
            dataType : 'json',
            success : function(data) {
                if (data.code === "200") {
                    flag = data.result;
                }
            }
        });
        return flag
    },

    startReceiveDanmu : function() {
            "use strict";
            var flag = false;
            $.ajax({
                url : './rest/room/startReceiveDanmu',
                async : false,
                cache : false,
                type : 'GET',
                dataType : 'json',
                success : function(data) {
                    if (data.code === "200") {
                        flag = data.result;
                    }
                }
            });
            return flag
    },
};
