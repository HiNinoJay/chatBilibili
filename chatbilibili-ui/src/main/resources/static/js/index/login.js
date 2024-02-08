<!--二维码相关脚本-->

function getQrCodeImg(init){
    $.ajax({
        url: '../rest/login/getQrCodeInfo',
        async: false,
        cache: false,
        type: 'POST',
        dataType: 'json',
        success: function (data) {
            if (data.code == "200") {
                console.log("二维码信息获取成功." );
                var url = data.result.url;
                $('.qrCodeImg').attr('src', '../rest/login/generateQrCodeByUrl?url=' + encodeURIComponent(url));
                $('.imageLoaderDiv').removeClass('loader');
                // 显示刷新成功的消息
                if(!init) {
                    // showMessage("二维码刷新成功！", "success",2);
                    console.log("二维码刷新成功!");
                }// 显示消息，然后在2秒后消失
            }else{
                // showMessage("二维码请求错误！", "danger",2);
                console.log("二维码请求错误!");
            }
        },
        error: function () {
            // $('.imageLoaderDiv').removeClass('loader');  // 当AJAX请求失败时，隐藏载入图标
            console.log("二维码请求错误!");
            // showMessage("二维码请求错误！", "danger",2);
        }
    });
}

function checkScanQrCodeStatus() {
    $.ajax({
        url: '../rest/login/checkScanQrCodeStatus',
        async: false,
        cache: false,
        type: 'POST',
        dataType: 'json',
        success: function (data) {
            var code = data.code;
            if (code == 200) {
                clearInterval(time);
                window.location.replace("/");
                //二维码失效
            }else if(code == 86038){
                // showMessage("二维码过期自动刷新！", "warning",2);
                getQrImg(0)
            }else if(code == 86101){
                console.log("未扫码");
            }else if(code == 86090){
                console.log("已扫码,请在手机上确认登录");
            }else{
                // showMessage("二维码请求错误！"+code, "danger",2);
            }
        }
    })
}


<!-- 显示登录页面 -->

$(document).on('click', '.loginButton', function (e) {
    $('.pageDimmer').dimmer('show');
    // 开始获取二维码 以及 持续判断二维码扫描情况
   getQrCodeImg(1);
   time = setInterval(checkScanQrCodeStatus, 3000);
});

$(document).on('click', '.loginFailCloseIcon', function (e) {
    $(this).closest('.loginFailMessage').transition('fade');
});



<!-- 通过cookie登录 -->

function loginByCookie(cookieValue) {
    $.ajax({
        url: '../rest/login/loginByCookie',
        async: false,
        cache: false,
        type: 'POST',
        data: {
            cookieValue: cookieValue
        },
        dataType: 'json',
        success: function (data) {
            const flag = data.result;
            if (flag == true) {
                $(".cookieValue").val("");
                $('.loginSuccessMessage').removeClass('hidden');
                // 延迟1秒后执行页面刷新
                setTimeout(function(){window.location.href = "/";}, 1000);
            } else {
                $('.loginFailMessage').removeClass('hidden');
            }
        }
    })
}

$(document).on('click', '.setCookieButton', function () {
    const cookieValue = $(".cookieValue").val();
    loginByCookie(cookieValue);
});

