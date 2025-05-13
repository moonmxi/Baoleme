// 未使用
function handleSubmit(event) {
    event.preventDefault(); // 阻止表单默认提交

    // 获取输入值
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    // 新增合法性检查
    const errors = [];
    if (username.length <= 6 || username.length >= 20) {
        errors.push("用户名长度必须大于6且小于20个字符");
    }
    if (password.length <= 6 || password.length >= 20) {
        errors.push("密码长度必须大于6且小于20个字符");
    }

    // 如果存在错误则提示并终止流程
    if (errors.length > 0) {
        alert(errors.join('\n'));
        return;
    }

    // 在控制台输出
    console.log('注册信息 - 用户名:', username, '密码:', password);

    // 跳转到注册成功页面
    window.location.href = 'regist-success';
}