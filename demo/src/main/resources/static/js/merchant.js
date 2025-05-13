// 初始化导航切换
function initNavigation() {
    const navItems = document.querySelectorAll('.nav-item');
    const contentAreas = document.querySelectorAll('.content-area');

    // 默认显示第一个内容区域
    contentAreas[0].style.display = 'block';
    navItems[0].style.backgroundColor = '#34495e';

    navItems.forEach(item => {
        item.addEventListener('click', function(e) {
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
}

// 主初始化函数
document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
});