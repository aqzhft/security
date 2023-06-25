$(function() {
    var _form = $('form');

    // 让第一个输入框聚焦
    letFirstInputFieldFocus(_form);

    // 每个输入框聚焦自动变色
    changeInputBorderColorEvent(_form);

    // 输入外边框点击要让内输入框获取焦点
    letInnerInputFocusEvent(_form);

    // 提交事件
    submitEvent(_form);
})

function letFirstInputFieldFocus(_form) {
    _form.find("input[name='oldPassword']").focus();
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

function submitEvent(_form) {
    var _submit = _form.find("input[type='submit']")
    _submit.bind('click', function(e) {
        e.preventDefault();
        var url = _form.attr('action');
        if (validate(_form)) {
            $.ajax({
                url: url + "?" + _form.serialize(),
                type: 'POST',
                success: function(response, status, xhr) {
                    window.location.href = "/";
                },
                error: function(xhr, status, error) {
                    showAndHide(xhr.responseText);
                }
            })
            return false;
        }
    })
}

function validate(_form) {
    var _oldPassword = _form.find("input[name='oldPassword']");
    var _newPassword = _form.find("input[name='newPassword']");
    var _reInput = _form.find("input[name='reInput']");
    if (_oldPassword.val() === '') {
        show("原密码不能为空");
        return false;
    }
    if (_newPassword.val() === '') {
        show("新密码不能为空");
        return false;
    }
    if (_newPassword.val() !== _reInput.val()) {
        show("新密码输入不一致");
        return false;
    }
    show("")
    return true;
}

function show(text) {
    var errText = $(".form").find(".err_text");
    errText.text(text);
}

function showAndHide(text, timeout) {
    timeout = timeout || 3000;
    var errText = $(".form").find(".err_text");
    errText.text(text);
    setTimeout(function() {
        errText.text('');
    }, timeout);
}