<!--websocket相关-->

var socket = null;

function openWebsocketConnection(ip, sliceh) {

    if (typeof (WebSocket) == "undefined") {
        console.log("您的浏览器不支持WebSocket，显示弹幕功能异常，请升级你的浏览器版本，推荐谷歌，网页显示弹幕失败 但不影响其他功能使用!");
        return;
    }

    console.log("本地webSocket服务器正在连接");

    let socketUrl = ip;

    // 有连接的话先关闭
    if (socket != null) {
        socket.close();
        socket = null;
    }

    try {
        socket = new WebSocket(socketUrl);
    } catch (err) {
        console.log(err);
    }

    // 打开事件
    socket.onopen = function () {

        console.log("第一次连接已打开");
        $('.connectSegment').addClass('dimmer');
        $('.connectStatusContentSegment').addClass('dimmer');
        $('.danmuSegment').removeClass('dimmer');
        $('.showDanmuButton').text('刷新');
    };

    // 获得消息事件
    socket.onmessage = function (msg) {

        let responseData = JSON.parse(msg.data);
        if(responseData.status != 0) {
            return;
        }
        // 获得消息，就去绘制弹幕
        var result = danmuDataUtils.drawDanmu(responseData.cmd, responseData.result);
        $('.danmuMessageBoard').append(result);
        var maxNumberOfDivs = 30;
        var numberOfDivs = $(".danmuMessageBoard").children("div").length;

        // 自动滚动到底部
        // 找到容器元素
        var container = $(".danmuMessageBoardContainer");


        console.log("当前的个数：" + numberOfDivs);
        // 检查容器高度是否超过最大高度
        if (numberOfDivs >= maxNumberOfDivs) {
            // 删除第一个子元素
            $('.danmuMessageBoard').children("div").first().remove();
        }

        // 滚动到底部函数
        function scrollToBottom(){
            container.scrollTop(container[0].scrollHeight);
        }

        // 在添加子元素后自动滚动到底部
        $(".danmuMessageBoard").on("DOMNodeInserted", scrollToBottom);

        // 初始化时滚动到底部
        scrollToBottom();

    };

    // 关闭事件
    socket.onclose = function () {
        console.log("连接已关闭，网页显示弹幕失败 但不影响其他功能使用");
        $('.connectSegment').removeClass('dimmer');
        $('.afterConnectSegment').addClass('dimmer');
        $('.danmuSegment').addClass('dimmer');
        $('.showDanmuButton').text('显示弹幕');
        $('.connectStatusContentSegment').removeClass('dimmer');
    };

    // 发生了错误事件
    socket.onerror = function (evt) {
        console.log("连接到弹幕服务器发生了错误" + evt.data);
    }
}

const danmuDataUtils = {

    constructMessageDiv: function(dataJson) {


        // 创建一个日期对象
        var date = new Date(dataJson.timestamp);

        // 提取年月日时分秒
        var year = date.getFullYear();
        var month = date.getMonth() + 1; // 月份从0开始，所以要加1
        var day = date.getDate();
        var hours = date.getHours();
        var minutes = date.getMinutes();
        var seconds = date.getSeconds();

        var formattedDateTime = year + "/" + month + "/" + day + " " + hours + ":" + minutes + ":" + seconds;
        var content = formattedDateTime + " 收到弹幕: ";

        var higherLevelDanmuFlag = false;

        if(dataJson.medal_name != null) {
            content = content + dataJson.medal_name  + dataJson.medal_level;
        }
        if(dataJson.uguard > 0) {
            content = content + "[舰长]";
            higherLevelDanmuFlag = true;
        }
        if(dataJson.vip == 1 || dataJson.svip == 1) {
            content = content + "[爷]";
            higherLevelDanmuFlag = true;
        }
        if(dataJson.manager > 0) {
            if(dataJson.manager == 1) {
                content = content + "[房]";
            } else {
                content = content + "[播]";
            }
        }
        if(dataJson.ulevel != null) {

        }
        content = content + " ";
        content = content + dataJson.uname;
        content = content + " ta说：";
        content = content + dataJson.msg;

        var color = higherLevelDanmuFlag ? "pink" : "blue";

        return `<div class= \"ui ` + color  + ` visible message \"> ` +
                    `<p>` + content + `</p>` +
                `</div>`;
    },

    constructGiftMessageDiv: function(dataJson) {


        // 创建一个日期对象
        var date = new Date(dataJson.timestamp);

        // 提取年月日时分秒
        var year = date.getFullYear();
        var month = date.getMonth() + 1; // 月份从0开始，所以要加1
        var day = date.getDate();
        var hours = date.getHours();
        var minutes = date.getMinutes();
        var seconds = date.getSeconds();

        var formattedDateTime = year + "/" + month + "/" + day + " " + hours + ":" + minutes + ":" + seconds;
        var content = " 收到道具：";

        content = content + " ";
        content = content + dataJson.uname;
        content = content + " 投喂的：";
        content = content + dataJson.giftName;
        content = content + " x ";
        content = content + dataJson.num;

        var color = "red";

        return `<div class= \"ui ` + color  + ` visible message \"> ` +
                    `<p>` + content + `</p>` +
                `</div>`;
    },

    drawDanmu: function (cmd, dataJson) {
        switch (cmd) {
            case "DANMU_MSG":
                return danmuDataUtils.constructMessageDiv(dataJson);
            case "SEND_GIFT":
                return danmuDataUtils.constructGiftMessageDiv(dataJson);
            case "superchat":
                return danmuDataUtils.constructSCMessageDiv(dataJson);
            default:
                return "";
        }
    }
}
