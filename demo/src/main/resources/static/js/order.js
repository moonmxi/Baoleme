// 订单状态筛选
document.querySelectorAll('.order-filter button').forEach(btn => {
    btn.addEventListener('click', () => {
        // 切换激活状态
        document.querySelector('.order-filter button.active').classList.remove('active');
        btn.classList.add('active');
        
        // 获取筛选状态
        const status = btn.dataset.status;
        
        // 执行筛选逻辑（示例）
        document.querySelectorAll('.data-table tbody tr').forEach(row => {
            const rowStatus = row.querySelector('.status-tag').textContent;
            const shouldShow = status === 'ALL' || rowStatus === status;
            row.style.display = shouldShow ? '' : 'none';
        });
    });
});