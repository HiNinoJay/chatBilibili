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
        console.log("连接已打开");

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

        // 自动滚动到底部
        // 找到容器元素
        var container = $(".danmuMessageBoardContainer");

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

    // 0弹幕 1礼物 2消息
    getCmdType: function (t) {
        if (t === 0) {
            return `<span class="danmu-type">弹幕</span>`;
        } else if (t === 1) {
            return `<span class="danmu-type danmu-type-gift">礼物</span>`;
        } else if (t === 2) {
            return `<span class="danmu-type danmu-type-superchat">留言</span>`;
        } else {
            return `<span class="danmu-type danmu-type-msg">消息</span>`;
        }
    },
    getTime: function (d,t) {
        if (String(d.timestamp).length == 10) d.timestamp = d.timestamp * 1000;
        if(t===0) {
            return `<span class="danmu-time">` + format(d.timestamp, false) + `</span>`;
        }else if(t===1){
            return `<span class="danmu-time danmu-time-gift">` + format(d.timestamp, false) + `</span>`;
        }else if(t===2){
            return `<span class="danmu-time danmu-time-superchat">` + format(d.timestamp, false) + `</span>`;
        }else{
            return `<span class="danmu-time danmu-time-msg">` + format(d.timestamp, false) + `</span>`;
        }
    },
    only_time: function (d,t) {
        if (String(d.timestamp).length == 10) d.timestamp = d.timestamp * 1000;
        if(t===0) {
            return `<span class="danmu-time">` + format(d, false) + `</span>`;
        }else if(t===1){
            return `<span class="danmu-time danmu-time-gift">` + format(d, false) + `</span>`;
        }else if(t===2){
            return `<span class="danmu-time danmu-time-superchat">` + format(d, false) + `</span>`;
        }else{
            return `<span class="danmu-time danmu-time-msg">` + format(d, false) + `</span>`;
        }
    },
    medal: function (d) {
        if (d.medal_name !== null && d.medal_name !== '') {
            return `<span class="danmu-medal">` + d.medal_name + addSpace(d.medal_level) + `</span>`;
        }
        return '';
    },
    guard: function (d) {
        if (d.uguard > 0) {
            return `<span class="danmu-guard">舰</span>`;
        } else {
            return '';
        }
    },
    vip: function (d) {
        if (d.vip === 1 || d.svip === 1) {
            return `<span class="danmu-vip">爷</span>`;
        } else {
            return '';
        }
    },
    manager: function (d) {
        if (d.manager > 0) {
            if (d.manager > 1) {
                return `<span class="danmu-manager">播</span>`;
            } else {
                return `<span class="danmu-manager">管</span>`;
            }
        } else {
            return '';
        }
    },
    ul: function (d) {
        if (d.ulevel != null) {
            return `<span class="danmu-ul">UL` + addSpace(d.ulevel) + `</span>`;
        }
        return '';
    },
    dname: function (d) {
        let clazz = "";
        if (d.uguard > 0) clazz = "name-guard";
        if (d.manager > 0) clazz = "name-manager";
        return `<a href="javascript:;"><span class="danmu-name` + (clazz === "" ? "" : (" " + clazz)) + `">` + d.uname + `:</span></a>`;
    },
    dmessage: function (d) {
        return `<span class="danmu-text">` + d.msg + `</span>`;
    },
    gname: function (d) {
        let clazz = "";
        if (d.uguard > 0) clazz = "name-guard";
        return `<a href="javascript:;"><span class="danmu-name` + (clazz === "" ? "" : (" " + clazz)) + `">` + d.uname + `</span></a>`;
    },
    gguard: function (d) {
        if (d.guard_level) {
            return `<span class="danmu-guard">舰</span>`;
        } else {
            return '';
        }
    },
    gmessage: function (d) {
        return `<span class="danmu-text">` + d.action + `了 ` + `<span class="danmu-text-gift">`+d.giftName+`</span>` + ` x ` + d.num + `</span>`;
    },
    stext: function (d) {
        return `<span class="danmu-text">留言了` + d.time + `秒说:` + `<span class="danmu-text-superchat">`+d.message+`</span>` + `</span>`;
    },
    block_type: function (d) {
        if (d.operator === 1) {
            return "房管";
        } else {
            return "主播";
        }
    },
    tips: function (d) {
        return `<div class="danmu-tips" uid="` + d.uid + `"><ul class="danmu-tips-ul"><li class="danmu-tips-li" data-bs-toggle="modal" data-bs-target="#block-model">禁言</li><li class="danmu-tips-li">查看</li><li class="danmu-tips-li">关闭</li></ul></div>`;
    },

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

        var formattedDateTime = year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;
        var content = formattedDateTime + " 弹幕 ";

        if(dataJson.medal_name != null) {
            content = content + dataJson.medal_name  + dataJson.medal_level;
        }
        if(dataJson.uguard > 0) {
            content = content + "[舰长]";
        }
        if(dataJson.vip == 1 || dataJson.svip == 1) {
            content = content + "[爷]";
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
        content = content + "：";
        content = content + dataJson.msg;


        return `<div class="ui blue visible message">` +
                    `<p>` + content + `</p>` +
                `</div>`;
    },
    drawDanmu: function (cmd, d) {
        var type_index = 0;
        switch (cmd) {
            case "DANMU_MSG":
                return danmuDataUtils.constructMessageDiv(d);
            case "gift":
                type_index=1;
                d.timestamp = d.timestamp * 1000;
                return `<div class="danmu-child" uid="` + d.uid + `">` + danmuDataUtils.getType(type_index) + danmuDataUtils.time(d,type_index) + danmuDataUtils.gguard(d) + danmuDataUtils.gname(d) + danmuDataUtils.gmessage(d) + danmuDataUtils.tips(d) + `</div>`;
            case "superchat":
                type_index=2;
                d.start_time = d.start_time * 1000;
                d.timestamp = d.start_time;
                d.uguard = d.user_info.guard_level;
                d.manager = d.user_info.manager;
                d.uname = d.user_info.uname;
                return `<div class="danmu-child" uid="` + d.uid + `">` + danmuDataUtils.getType(type_index) + danmuDataUtils.time(d,type_index) + danmuDataUtils.dname(d) + danmuDataUtils.stext(d) + danmuku.tips(d) + `</div>`;
            default:
                return "";
        }
    }
}
