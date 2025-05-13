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

// 初始化图表（示例使用Chart.js）
function initRevenueChart() {
    // 清理旧图表实例
    if (window.myChart instanceof Chart) {
        window.myChart.destroy();
    }

    // 获取canvas元素
    const ctx = document.getElementById('revenueChart');
    if (!ctx) return;

    // 创建新实例
    window.myChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May'],
            datasets: [{
                label: '营业收入',
                data: [6500, 5900, 8000, 8100, 5600],
                borderColor: '#4CAF50',
                tension: 0.4,
                borderWidth: 2,
                pointRadius: 4,
                fill: false
            }]
        },
        options: {
            responsive: true, // 开启响应式
            maintainAspectRatio: false, // 关闭默认宽高比
            scales: {
                y: {
                    beginAtZero: true,
                    grid: {
                        color: 'rgba(0,0,0,0.1)'
                    }
                },
                x: {
                    grid: {
                        display: false
                    }
                }
            }
        }
    });
}

// 主初始化函数
document.addEventListener('DOMContentLoaded', () => {
    initStatsViewToggle();
    initRevenueChart();

    // 窗口resize时重置图表
    window.addEventListener('resize', function() {
        initRevenueChart();
    });
});