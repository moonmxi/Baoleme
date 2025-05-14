// 初始化导航切换
function initNavigation() {
    const navItems = document.querySelectorAll('.nav-item');
    const exitButtons = document.querySelectorAll('.btn-rtn'); // 单独监听返回按钮
    const contentAreas = document.querySelectorAll('.content-area');

    // 默认显示第一个内容区域
    contentAreas[0].style.display = 'block';
    navItems[0].style.backgroundColor = '#34495e';

    navItems.forEach(item => {
        item.addEventListener('click', function (e) {
            e.preventDefault();

            // 移除所有激活状态
            contentAreas.forEach(area => area.style.display = 'none');
            navItems.forEach(nav => nav.style.backgroundColor = '');

            // 显示目标区域
            const targetId = this.querySelector('a').getAttribute('href');
            document.querySelector(targetId).style.display = 'block';
            this.style.backgroundColor = '#34495e';

            history.pushState(null, null, targetId);
        });
    });

    // 处理返回按钮的点击事件
    exitButtons.forEach(button => {
        button.addEventListener('click', function (e) {
            e.preventDefault();

            // 移除所有激活状态
            // console.log("[DEBUG] 正在隐藏所有内容区域:");
            contentAreas.forEach(area => {
                // console.log(` - 区域ID: ${area.id}`);  // 输出每个区域的ID
                area.style.display = 'none';           // 隐藏操作
            });
            navItems.forEach(nav => nav.style.backgroundColor = '');

            // 显示目标区域
            const targetId = this.getAttribute('href');
            // console.log(`[DEBUG] 指定返回目标: ${targetId}`);  // 输出目标ID
            document.querySelector(targetId).style.display = 'block';

            // 根据目标区域设置对应的导航项激活状态
            const targetNavItem = document.querySelector(`.nav-item a[href="${targetId}"]`).parentNode;
            targetNavItem.style.backgroundColor = '#34495e';

            history.pushState(null, null, targetId);
        });
    });
}

// 主初始化函数
document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
});