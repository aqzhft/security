$(function() {
    var _form = $(".form");

    // 首行输入自动获取焦点
    firstInputFieldAutoFocus(_form);

    // 输入行有输入内容显示清空按钮
    showClearBtnEvent(_form);

    // 清空按钮增加单击事件
    clearBtnEvent(_form);

    // 验证码图片增加单击事件
    changeVerifyCodeImageEvent(_form);

    // 记住密码增加单击事件
    rememberMeClickEvent(_form);

    // 提交增加单击事件
    submitBtnClickEvent(_form);

    // 输入外边框点击要让内输入框获取焦点
    changeInputBorderColorEvent(_form);

    // 输入外边框点击要让内输入框获取焦点
    letInnerInputFocusEvent(_form)
})

function submitBtnClickEvent(_form) {
    var _target = _form.find("input[type='submit']");
    var _verifyCodeInput = _form.find("input[name='validateCode']");
    _target.click(function(e) {
        e.preventDefault();
        var errText = _form.find(".err_text");
        var url = _form.attr('action');
        $.ajax({
            url: url + "?" + _form.serialize(),
            type: 'POST',
            success: function(response, status, xhr) {
                window.location.href = $(".form").find("input[name='homePage']").val();
            },
            error: function(xhr, status, error) {
                showAndHide(xhr.responseText);
                changeVerifyCodeImage(_form);
            }
        })
        return false;
    })
}

function rememberMeClickEvent(_form) {
    var _target = _form.find("#remember-me-checkbox");
    _target.click(function(e) {
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
}

function changeVerifyCodeImageEvent(_form) {
    var _target = _form.find(".field-verify-code").find(".img");
    var _verifyCodeInput = _form.find("input[name='validateCode']");
    _target.click(function() {
        var imgURI = $(this).attr("src");
        $(this).attr("src", imgURI);
        _verifyCodeInput.val("");
        _verifyCodeInput.focus();
    })
}

function changeVerifyCodeImage(_form) {
    var _verifyCodeImage = _form.find(".field-verify-code").find("img");
    var _verifyCodeInput = _form.find("input[name='validateCode']");
    if (_verifyCodeImage) {
        var imgURI = _verifyCodeImage.attr("src");
        _verifyCodeImage.attr("src", imgURI);
        _verifyCodeInput.val("");
        _verifyCodeInput.focus();
    }
}

function clearBtnEvent(_form) {
    var _target = _form.find(".icon-close");
    _target.click(function(e) {
        var inputField = $(this).parent().find("input")
        var inputFieldValue = inputField.val();
        if (inputFieldValue !== undefined && inputFieldValue.length > 0) {
            inputField.val("");
            inputField.focus();
            $(this).hide();
        }
    })
}

function showClearBtnEvent(_form) {
    var _target = _form.find("input[name='username'],input[name='password'],input[name='validateCode'],input[name='identifyId'],input[name='sms']");
    _target.bind("keyup", function(e) {
        var val = $(this).val()
        var close = $(this).parent().find(".icon-close")
        if (val !== undefined && val.length > 0) {
            close.show();
        } else {
            close.hide();
        }
    })
}

function firstInputFieldAutoFocus(_form) {
    var _target = _form.find("input[name='username'],input[name='identifyId'],input[name='sms']");
    _target.focus();
}

function changeInputBorderColorEvent(_form) {
    var _fields = _form.find('.form-field').find("input");
    _fields.bind('focus', function() {
        var _target = $(this).parent()
        if (!_target.hasClass('input-focus')) {
            _target.addClass('input-focus')
        }
    })
    _fields.bind('blur', function() {
        var _target = $(this).parent()
        if (_target.hasClass('input-focus')) {
            _target.removeClass('input-focus')
        }
    })
}

function letInnerInputFocusEvent(_form) {
    var _fields = _form.find('.form-field')
    _fields.bind('click', function() {
        var _target = $(this).find("input");
        _target.focus();
    })
}

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
