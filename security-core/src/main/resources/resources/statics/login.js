$(function() {
    var form = $(".form");
    var formInputClose = form.find(".icon-close");
    var formInputField = form.find("input[name='username'],input[name='password'],input[name='validateCode'],input[name='identifyId'],input[name='sms']");
    var verifyCodeImage = form.find(".field-verify-code").find(".img");
    var rememberMe = form.find("#remember-me-checkbox");
    var submit = form.find("input[type='submit']");

    formInputField.filter("input[name='username'],input[name='identifyId'],input[name='sms']").focus();

    formInputField.bind("keyup", function(e) {
        var val = $(this).val()
        var close = $(this).parent().find(".icon-close")
        if (val !== undefined && val.length > 0) {
            close.show();
        } else {
            close.hide();
        }
    })

    formInputClose.click(function(e) {
        var inputField = $(this).parent().find("input")
        var inputFieldValue = inputField.val();
        if (inputFieldValue !== undefined && inputFieldValue.length > 0) {
            inputField.val("");
            inputField.focus();
            $(this).hide();
        }
    });

    verifyCodeImage.click(function() {
        var imgURI = $(this).attr("src")
        $(this).attr("src", imgURI);
    })

    rememberMe.click(function(e) {
        var value = $(this).prop("checked");
        var label = $(this).parent().find(".icon-checkbox");
        if (value) {
            label.addClass("icon-checkbox-checked");
            label.removeClass("icon-checkbox-unchecked");
        } else {
            label.removeClass("icon-checkbox-checked");
            label.addClass("icon-checkbox-unchecked");
        }
    })

    submit.click(function() {
        var errText = $(".form").find(".err_text");
        var url = form.attr('action');
        $.ajax({
            url: url + "?" + form.serialize(),
            type: 'POST',
            success: function(response, status, xhr) {
                window.location.href = $(".form").find("input[name='homePage']").val();
            },
            error: function(xhr, status, error) {
                showAndHide(xhr.responseText);
            }
        })
        return false;
    })
})



function sendSms(url) {
    var verifyCodeCountDown, sendVerifyCodeUrl;
    var form = $(".form");
    var smsInput = form.find("input[name='sms']");
    var validateCodeInput = form.find("input[name='validateCode']");
    var smsValue = smsInput.val();
    if (smsValue === undefined || smsValue.length === 0) {
        showAndHide("手机号未填写");
        return
    }

    window.clearInterval(verifyCodeCountDown);
    $.get(url + '?sms=' + smsValue, function(response, status, xhr) {
        if (status === 'success') {
            var btn = form.find(".verify-btn");
            btn.text("发送成功");
            validateCodeInput.focus();
            sendVerifyCodeUrl = btn.prop('href');
            btn.prop('href', 'javascript:void(0)');
            var time = 60;
            verifyCodeCountDown = setInterval(function() {
                btn.text(--time + "秒后可重试");
                if (time <= 0) {
                    window.clearInterval(verifyCodeCountDown)
                    btn.text("发送验证码");
                    btn.prop('href', sendVerifyCodeUrl);
                }
            }, 1000);
        }
    })
}

function send(url) {
    var verifyCodeCountDown, sendVerifyCodeUrl;
    var form = $(".form");
    var identifyInput = form.find("input[name='identifyId']");
    var validateCodeInput = form.find("input[name='validateCode']");
    var identifyId = identifyInput.val();
    if (identifyId === undefined || identifyId.length === 0) {
        showAndHide("用户名或手机号未填写");
        return
    }

    window.clearInterval(verifyCodeCountDown);
    $.get(url + '?identifyId=' + identifyId, function(response, status, xhr) {
        if (status === 'success') {
            var btn = form.find(".verify-btn");
            btn.text("发送成功");
            validateCodeInput.focus();
            sendVerifyCodeUrl = btn.prop('href');
            btn.prop('href', 'javascript:void(0)');
            var time = 60;
            verifyCodeCountDown = setInterval(function() {
                btn.text(--time + "秒后可重试");
                if (time <= 0) {
                    window.clearInterval(verifyCodeCountDown)
                    btn.text("发送验证码");
                    btn.prop('href', sendVerifyCodeUrl);
                }
            }, 1000);
        }
    })
}

function showAndHide(text, timeout) {
    timeout = timeout || 3000;
    var errText = $(".form").find(".err_text");
    errText.text(text);
    setTimeout(function() {
        errText.text('');
    }, timeout);
}
