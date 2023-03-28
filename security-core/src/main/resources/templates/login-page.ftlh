<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${loginPage.title}</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0, user-scalable=no">
    <link rel="stylesheet" type="text/css" href="${path.basePath}/statics/login.css" />
    <link rel="stylesheet" type="text/css" href="${path.basePath}/statics/iconfont.css" />
    <script type="text/javascript" src="${path.basePath}/statics/iconfont.js"></script>
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script type="text/javascript" src="${path.basePath}/statics/login.js"></script>
    <style type="text/css">
        .view {background-image: url('${path.basePath}${loginPage.backendImagePath}')}
    </style>
</head>
<body>
    <div class="view">
        <div class="header">
            <div class="header-wrapper">
                <div class="logo">
                    <img class="img" src="${path.basePath}/images/logo.png" height="40"/>
                    <a class="title" href="javascript:void(0)" >${loginPage.logoTitle}</a>
                </div>
            </div>
        </div>
        <div class="main">
            <div class="main-wrapper">
                <!-- 二维码列表 -->
                <div class="left">
                    <div class="images"></div>
                </div>
                <!-- 登录表格 -->
                <div class="right">
                    <div class="panel">
                        <!-- 普通账户密码登录 -->
                        <#if loginWay == 'account'>
                            <form class="form" action="${path.basePath}${path.formLoginUrl}" method="post">
                                <#if verifyLoginWay?exists>
                                    <div class="icon-switch">
                                        <a class="iconfont icon-smsCode" href="${path.basePath}${path.loginPage}?way=verifyCode"></a>
                                        <span class="shade"></span>
                                    </div>
                                </#if>
                                <div class="err_text"></div>
                                <div class="form-row field-name">
                                    <i class="iconfont icon-user"></i>
                                    <input type="text" name="username" tabindex="1" placeholder="用户名 / 手机号"/>
                                    <i class="iconfont icon-close"></i>
                                </div>
                                <div class="form-row field-password">
                                    <i class="iconfont icon-key"></i>
                                    <input type="password" name="password" tabindex="2" placeholder="密码"/>
                                    <i class="iconfont icon-close"></i>
                                </div>
                                <div class="form-row field-verify-code">
                                    <div class="verify-input">
                                        <i class="iconfont icon-image"></i>
                                        <input type="text" name="validateCode" maxlength="4" tabindex="3" placeholder="验证码"/>
                                        <i class="iconfont icon-close"></i>
                                    </div>
                                    <img class="img" src="${path.basePath}/code/image?v" alt="验证码"/>
                                </div>
                                <div class="form-row field-save-login">
                                    <input id="remember-me-checkbox" type="checkbox" name="remember-me" checked/>
                                    <label class="remember-me" for="remember-me-checkbox">
                                        <i class="iconfont icon-checkbox icon-checkbox-checked"></i>
                                        <span>一周免登录</span>
                                    </label>
                                    <a class="reset" href="#">忘记密码？</a>
                                </div>
                                <div class="form-row field-submit">
                                    <input type="hidden" name="homePage" value="${path.basePath}${path.homePage}"/>
                                    <input type="submit" value="登录" tabindex="4"/>
                                </div>
                                <div class="form-row field-register">还没有账号？ <a class="link" href="javascript:void(0)">立即注册</a></div>
                            </form>
                        </#if>
                        <!-- 验证码登录 -->
                        <#if loginWay == 'verifyCode'>
                            <form class="form" action="${path.basePath}${path.verifyLoginUrl}" method="post">
                                <div class="icon-switch">
                                    <a class="iconfont icon-computer" href="${path.basePath}${path.loginPage}?way=account"></a>
                                    <span class="shade"></span>
                                </div>
                                <div class="err_text"></div>
                                <div class="form-row field-name">
                                    <i class="iconfont icon-user"></i>
                                    <input type="text" name="identifyId" tabindex="1" placeholder="用户名 / 手机号"/>
                                    <i class="iconfont icon-close"></i>
                                </div>
                                <div class="form-row field-verify-input">
                                    <div class="verify-input">
                                        <i class="iconfont icon-image"></i>
                                        <input type="text" name="validateCode" tabindex="3" placeholder="验证码"/>
                                        <i class="iconfont icon-close"></i>
                                        <a class="verify-btn" href="javascript:send('${path.basePath}/code/verify')">发送验证码</a>
                                    </div>
                                </div>
                                <div class="form-row field-save-login">
                                    <input id="remember-me-checkbox" type="checkbox" name="remember-me" checked value="true"/>
                                    <label class="remember-me" for="remember-me-checkbox">
                                        <i class="iconfont icon-checkbox icon-checkbox-checked"></i>
                                        <span>一周免登录</span>
                                    </label>
                                    <a class="reset" href="#">忘记密码？</a>
                                </div>
                                <div class="form-row field-submit">
                                    <input type="hidden" name="homePage" value="${path.basePath}${path.homePage}"/>
                                    <input type="submit" value="登录" tabindex="4"/>
                                </div>
                                <div class="form-row field-register">还没有账号？ <a class="link" href="javascript:void(0)">立即注册</a></div>
                            </form>
                        </#if>
                        <!-- 手机号登录 -->
                        <#if loginWay == 'mobile'>
                            <form class="form" action="${path.basePath}${path.mobileLoginUrl}" method="post">
                                <div class="icon-switch">
                                    <a class="iconfont icon-computer" href="${path.basePath}${path.loginPage}?way=account"></a>
                                    <span class="shade"></span>
                                </div>
                                <div class="err_text"></div>
                                <div class="form-row field-name">
                                    <i class="iconfont icon-user"></i>
                                    <input type="text" name="sms" tabindex="1" placeholder="手机号"/>
                                    <i class="iconfont icon-close"></i>
                                </div>
                                <div class="form-row field-verify-input">
                                    <div class="verify-input">
                                        <i class="iconfont icon-image"></i>
                                        <input type="text" name="validateCode" tabindex="3" placeholder="验证码"/>
                                        <i class="iconfont icon-close"></i>
                                        <a class="verify-btn" href="javascript:sendSms('${path.basePath}/code/sms')">发送验证码</a>
                                    </div>
                                </div>
                                <div class="form-row field-save-login">
                                    <input id="remember-me-checkbox" type="checkbox" name="remember-me" checked value="true"/>
                                    <label class="remember-me" for="remember-me-checkbox">
                                        <i class="iconfont icon-checkbox icon-checkbox-checked"></i>
                                        <span>一周免登录</span>
                                    </label>
                                    <a class="reset" href="#">忘记密码？</a>
                                </div>
                                <div class="form-row field-submit">
                                    <input type="hidden" name="loginWay" value="mobile"/>
                                    <input type="hidden" name="homePage" value="${path.basePath}${path.homePage}"/>
                                    <input type="submit" value="登录" tabindex="4"/>
                                </div>
                                <div class="form-row field-register">还没有账号？ <a class="link" href="javascript:void(0)">立即注册</a></div>
                            </form>
                        </#if>
                        <!-- 邮箱登录 -->
                        <#if loginWay == 'email'>
                            <form class="form" action="${path.basePath}${path.emailLoginUrl}" method="post">
                                <div class="icon-switch">
                                    <a class="iconfont icon-computer" href="${path.basePath}${path.loginPage}?way=account"></a>
                                    <span class="shade"></span>
                                </div>
                                <div class="err_text"></div>
                                <div class="form-row field-name">
                                    <i class="iconfont icon-user"></i>
                                    <input type="text" name="sms" tabindex="1" placeholder="邮箱"/>
                                    <i class="iconfont icon-close"></i>
                                </div>
                                <div class="form-row field-verify-input">
                                    <div class="verify-input">
                                        <i class="iconfont icon-image"></i>
                                        <input type="text" name="validateCode" tabindex="3" placeholder="验证码"/>
                                        <i class="iconfont icon-close"></i>
                                        <a class="verify-btn" href="javascript:sendSms('${path.basePath}/code/email')">发送验证码</a>
                                    </div>
                                </div>
                                <div class="form-row field-save-login">
                                    <input id="remember-me-checkbox" type="checkbox" name="remember-me" checked value="true"/>
                                    <label class="remember-me" for="remember-me-checkbox">
                                        <i class="iconfont icon-checkbox icon-checkbox-checked"></i>
                                        <span>一周免登录</span>
                                    </label>
                                    <a class="reset" href="#">忘记密码？</a>
                                </div>
                                <div class="form-row field-submit">
                                    <input type="hidden" name="loginWay" value="email"/>
                                    <input type="hidden" name="homePage" value="${path.basePath}${path.homePage}"/>
                                    <input type="submit" value="登录" tabindex="4"/>
                                </div>
                                <div class="form-row field-register">还没有账号？ <a class="link" href="javascript:void(0)">立即注册</a></div>
                            </form>
                        </#if>
                        <!-- 第三方登录 -->
                        <div class="oidc">
                            <#if loginWays?exists>
                                <#list loginWays as loginWay>
                                    <a class="iconfont icon-${loginWay.icon}" href="${path.basePath}${loginWay.uri}"></a>
                                </#list>
                            </#if>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="footer">
            <div class="footer-wrapper">
                <div class="copyright">${loginPage.copyright}</div>
                <div class="version">${loginPage.version}</div>
            </div>
        </div>
    </div>
</body>
</html>