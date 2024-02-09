<!--设置-->
var aiCharacterName;

$(document).ready(function(){
    // 监听单选按钮的点击事件
    $(".dadioFilds input[type='radio']").on('click', function(){
        // 将当前点击的单选按钮设置为选中状态
        $(this).prop('checked', true);

        // 将其他单选按钮设置为未选中状态
        $(".dadioFilds input[type='radio']").not(this).prop('checked', false);
    });
});


$(document).on('click', '.chatGPTCharacterMenu .item', function (e) {


    if($(this).hasClass('makeNewChatGPTCharacter')) {
        $('.ui.setNewChatGPTCharacterModal').modal('show');
    } else {
        // 取消所有单选按钮的选中状态
        var $activeLink = $('.chatGPTCharacterMenu').find('a.item.active:first');
        if($activeLink.length > 0) {
            $activeLink.removeClass('active');
        }

        $(this).addClass('active');
        $(this).addClass('activeAiCharacter');
        aiCharacterName = $(this).text();
        var prompt = $(this).data('name');
        $('.chatGPTCharacterContentSegment').text(prompt);
    }

});


$(document).on('click', '.oneStepAllSettingUsingButton', function (e) {
    // 创建一个复选框状态对象
    var checkboxStatus = {
        danmuStatus: $(".danmuSettingSegment input[name='danmuStatus']").prop('checked'),
        normalStatus: $(".danmuSettingSegment input[name='normalStatus']").prop('checked'),
        guardStatus: $(".danmuSettingSegment input[name='guardStatus']").prop('checked'),
        vipStatus: $(".danmuSettingSegment input[name='vipStatus']").prop('checked'),
        managerStatus: $(".danmuSettingSegment input[name='managerStatus']").prop('checked'),

        giftStatus: $(".giftSettingSegment input[name='giftStatus']").prop('checked'),
        normalGiftStatus: $(".giftSettingSegment input[name='normalGiftStatus']").prop('checked'),
        scGiftStatus: $(".giftSettingSegment input[name='scGiftStatus']").prop('checked'),

        danmuLogStatus: $(".otherDanmuSettingSegment input[name='danmuLogStatus']").prop('checked')
    };

    // 发送 AJAX 请求到后端
    $.ajax({
        url: './rest/setting/danmuUsing',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(checkboxStatus),
        success: function(response) {
            if(response.code == "200") {
                alert("弹幕配置应用成功。")
            } else {
                alert("弹幕配置应用失败。")
            }
        },
        error: function(xhr, status, error) {
            // 请求失败后的处理
            console.error('发送复选框状态到后端失败:', error);
        }
    });

    // 获取 aiReplyStatus 复选框的状态
    var aiReplyStatus = $(".aiReplyForm input[name='aiReplyStatus']").prop('checked');

    // 获取被选中的单选按钮的name属性值
    var aiReplyNum = $(".aiReplyForm input[type='radio']:checked").attr('name');

    // 发送 AJAX 请求到后端
    $.ajax({
        url: './rest/setting/chatGPTUsing',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ aiCharacterName : aiCharacterName, aiReplyStatus: aiReplyStatus, aiReplyNum: aiReplyNum }),
        success: function(response) {
            if(response.code == "200") {
                alert("chatGPT连接正常。")
            } else if(response.code == "50000"){
                alert("未连接到chatGPT服务器。")
            } else {
                alert("chatGPT连接异常。")
            }
        },
        error: function(xhr, status, error) {
            // 请求失败后的处理
            console.error('发送数据到后端失败:', error);
        }
    });


});

$(document).on('click', '.danmuSettingUsingButton', function (e) {

    // 创建一个复选框状态对象
    var checkboxStatus = {
        danmuStatus: $(".danmuSettingSegment input[name='danmuStatus']").prop('checked'),
        normalStatus: $(".danmuSettingSegment input[name='normalStatus']").prop('checked'),
        guardStatus: $(".danmuSettingSegment input[name='guardStatus']").prop('checked'),
        vipStatus: $(".danmuSettingSegment input[name='vipStatus']").prop('checked'),
        managerStatus: $(".danmuSettingSegment input[name='managerStatus']").prop('checked'),

        giftStatus: $(".giftSettingSegment input[name='giftStatus']").prop('checked'),
        normalGiftStatus: $(".giftSettingSegment input[name='normalGiftStatus']").prop('checked'),
        scGiftStatus: $(".giftSettingSegment input[name='scGiftStatus']").prop('checked'),

        danmuLogStatus: $(".otherDanmuSettingSegment input[name='danmuLogStatus']").prop('checked')
    };

    // 发送 AJAX 请求到后端
    $.ajax({
        url: './rest/setting/danmuUsing',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(checkboxStatus),
        success: function(response) {
            if(response.code == "200") {
                alert("应用成功。")
            } else {
                alert("应用失败。")
            }
        },
        error: function(xhr, status, error) {
            // 请求失败后的处理
            console.error('发送复选框状态到后端失败:', error);
        }
    });



});


$(document).on('click', '.chatGPTSettingUsingButton', function (e) {

    // 获取 aiReplyStatus 复选框的状态
    var aiReplyStatus = $(".aiReplyForm input[name='aiReplyStatus']").prop('checked');

    // 获取被选中的单选按钮的name属性值
    var aiReplyNum = $(".aiReplyForm input[type='radio']:checked").attr('name');

    // 发送 AJAX 请求到后端
    $.ajax({
        url: './rest/setting/chatGPTUsing',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ aiCharacterName : aiCharacterName, aiReplyStatus: aiReplyStatus, aiReplyNum: aiReplyNum }),
        success: function(response) {
            if(response.code == "200") {
                alert("应用成功。")
            } else if(response.code == "50000"){
                alert("未连接到chatGPT服务器。")
            } else {
                alert("chatGPT连接异常。")
            }
        },
        error: function(xhr, status, error) {
            // 请求失败后的处理
            console.error('发送数据到后端失败:', error);
        }
    });


});


$(document).on('click', '.testChatGPTHelloButton', function (e) {

    var prompt = $(".addNewAiCharacterTextArea").val();

    // 发送 AJAX 请求到后端
    $.ajax({
        url: './rest/setting/testHelloChatGPTByDescription',
        type: 'POST',
        dataType: 'json',
        data: {prompt : prompt},
        success: function(response) {
            if(response.code == "200") {
                console.log(response.result);
                alert(response.result.answers[0]);
            } else if(response.code == "50000"){
                alert("未连接到chatGPT服务器。")
            } else {
                alert("chatGPT连接异常。")
            }
        },
        error: function(xhr, status, error) {
            // 请求失败后的处理
            console.error('发送数据到后端失败:', error);
        }
    });


});


$(document).on('click', '.addNewAiCharacterButton', function (e) {

    // 获取 aiReplyStatus 复选框的状态
    var name = $(".addNewAiCharacterInput").val();

    if(name == null || name == "") {
        alert("请填写角色名称。")
        return;
    }

    // 获取被选中的单选按钮的name属性值
    var prompt = $(".addNewAiCharacterTextArea").val();


    if(prompt == null || prompt == "") {
        alert("请填写角色描述。")
        return;
    }


    // 发送 AJAX 请求到后端
    $.ajax({
        url: './rest/setting/addAiCharacter',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ name : name, prompt: prompt}),
        success: function(response) {
            if(response.code == "200") {
                alert("保存成功。")
            } else if(response.code == "50000"){
                alert("未连接到chatGPT服务器。")
            } else if(response.code == "50010"){
                alert("重复的角色名称。")
            } else {
                alert("chatGPT连接异常。")
            }
        },
        error: function(xhr, status, error) {
            // 请求失败后的处理
            console.error('发送数据到后端失败:', error);
        }
    });


});


$(document).on('click', '.deleteAllChatGptCharacterButton', function (e) {


    // 发送 AJAX 请求到后端
    $.ajax({
        url: './rest/setting/deleteAllChatGPTCharacter',
        async : false,
        cache : false,
        type : 'GET',
        dataType : 'json',
        success: function(response) {
            if(response.code == "200") {
                alert("清空成功。")
                window.location.href = "/";
            } else if(response.code == "50000"){
                alert("未连接到chatGPT服务器。")
            } else if(response.code == "50010"){
                alert("重复的角色名称。")
            } else {
                alert("chatGPT连接异常。")
            }
        },
        error: function(xhr, status, error) {
            // 请求失败后的处理
            console.error('发送数据到后端失败:', error);
        }
    });


});

