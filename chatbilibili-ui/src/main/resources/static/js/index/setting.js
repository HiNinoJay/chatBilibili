<!--设置-->
var pre;
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



    if(pre != null) {
        $(pre).removeClass('active');
    }
    console.log($(this).text());
    if($(this).hasClass('makeNewChatGPTCharacter')) {
        $('.ui.setNewChatGPTCharacterModal').modal('show');
    } else {
        $(this).addClass('active');
        $(this).addClass('activeAiCharacter');
        aiCharacterName = $(this).text();
        $('.chatGPTCharacterContentSegment').text($(this).text());
        pre = e.currentTarget;
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
            // 请求成功后的处理
            console.log('成功发送了数据到后端！');
            console.log(response); // 假设后端返回的响应
        },
        error: function(xhr, status, error) {
            // 请求失败后的处理
            console.error('发送数据到后端失败:', error);
        }
    });


});
