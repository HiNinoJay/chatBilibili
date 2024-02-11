$(document).ready(function(){

     setInterval(getAnswerLog, 3000);

});

function getAnswerLog(){
    console.log("获取AI回答记录。");
    $.ajax({
        url: '../rest/ai/getAnswer',
        async: false,
        cache: false,
        type: 'GET',
        dataType: 'json',
        success: function (responseData) {
            if (responseData.code == "200") {
                console.log("AI回答记录获取成功."  + responseData.result);
                if(responseData.result == null) {
                    return;
                }

                // 获得消息，就去绘制
                var result = aiDataUtils.drawAIAnswer(responseData.result);
                $('.aiMessageBoard').append(result);
                var maxNumberOfDivs = 30;
                var numberOfDivs = $(".aiMessageBoard").children("div").length;

                // 自动滚动到底部
                // 找到容器元素
                var container = $(".aiMessageBoardContainer");


                console.log("当前的ai个数：" + numberOfDivs);
                // 检查容器高度是否超过最大高度
                if (numberOfDivs >= maxNumberOfDivs) {
                    // 删除第一个子元素
                    $('.aiMessageBoard').children("div").first().remove();
                }

                // 滚动到底部函数
                function scrollToBottom(){
                    container.scrollTop(container[0].scrollHeight);
                }

                // 在添加子元素后自动滚动到底部
                $(".aiMessageBoard").on("DOMNodeInserted", scrollToBottom);

                // 初始化时滚动到底部
                scrollToBottom();

            }else{
                console.log("AI回答记录错误!");
            }
        },
        error: function () {
            console.log("AI回答记错误!");
        }
    });
}

const aiDataUtils = {

    drawAIAnswer: function (dataJson) {

        return `<div class="comment">
                    <div class="content">
                        <a class="author">` + dataJson.userName +`</a>` +
                        `<div class="metadata">
                            <span class="date">` + dataJson.questionTime + `</span>` +
                        `</div>
                        <div class="text">
                            <p>` + dataJson.prompt + `</p>` +
                        `</div>
                    </div>
                    <div class="comments">
                        <div class="comment">
                            <div class="content">
                                <a class="author">ChatGPT</a>
                                <div class="metadata">
                                    <span class="date">` + dataJson.answerTime + `</span>` + `
                                </div>
                                <div class="text"> `+
                                     dataJson.answers[0] + `
                                </div>
                            </div>
                        </div>
                    </div>
                </div>`
    }
}