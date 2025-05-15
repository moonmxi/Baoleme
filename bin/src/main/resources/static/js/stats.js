// 初始化统计视图切换
function initStatsViewToggle() {
    const viewButtons = document.querySelectorAll('.view-toggle button');
    const chartContainer = document.getElementById('revenueChart');
    const tableContainer = document.getElementById('dataTable');

    if (!chartContainer || !tableContainer) return;

    viewButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            // 切换按钮状态
            viewButtons.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');

            // 切换显示内容
            const isChart = btn.dataset.view === 'chart';
            chartContainer.style.display = isChart ? 'block' : 'none';
            tableContainer.style.display = isChart ? 'none' : 'block';

            // 图表自适应
            if (isChart && window.myChart) {
                setTimeout(() => window.myChart.resize(), 100);
            }
        });
    });
}

// 初始化图表（使用动态数据）
function initRevenueChart() {
    const ctx = document.getElementById('revenueChart');
    if (!ctx) return;

    // 销毁旧实例
    if (window.myChart instanceof Chart) {
        window.myChart.destroy();
    }

    // 从数据埋点获取动态数据
    const labels = window.chartData?.labels || [];
    const amounts = window.chartData?.amounts || [];

    window.myChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: '每日营业额',
                data: amounts,
                borderColor: '#4CAF50',
                tension: 0.4,
                borderWidth: 2,
                pointRadius: 4,
                fill: false
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    title: { display: true, text: '金额（元）' },
                    grid: { color: 'rgba(0,0,0,0.1)' }
                },
                x: {
                    title: { display: true, text: '日期' },
                    grid: { display: false }
                }
            }
        }
    });
}

// 处理时间范围变化（需实现API）
function handleTimeRangeChange(select) {
    const storeId = document.getElementById('storeId').value;
    const days = select.value;
    
    if (days === 'custom') {
        // 实现自定义日期选择逻辑
        return;
    }

    // AJAX 获取新数据（示例需实现后端API）
    fetch(`/api/stats?storeId=${storeId}&days=${days}`)
        .then(response => response.json())
        .then(data => {
            // 更新数据埋点
            window.chartData = {
                labels: data.dailySales.map(d => d.date),
                amounts: data.dailySales.map(d => d.amount)
            };
            // 重新渲染
            initRevenueChart();
        });
}

// 初始化函数
document.addEventListener('DOMContentLoaded', () => {
    initStatsViewToggle();
    initRevenueChart();
    
    // 优化resize处理
    let resizeTimer;
    window.addEventListener('resize', () => {
        clearTimeout(resizeTimer);
        resizeTimer = setTimeout(() => {
            if (window.myChart) {
                window.myChart.resize();
            }
        }, 200);
    });
});