package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    private Long id;
    private Long userId;
    private Long storeId;
    private Long riderId;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    private LocalDateTime endedAt;

    // 状态枚举（与数据库值严格对应）
    public enum OrderStatus {
        PENDING(0, "等待处理", "success"),
        PREPARING(1, "准备中", "warning"),
        DELIVERING(2, "配送中", "warning"),
        COMPLETED(3, "已完成", "warning"),
        CANCELLED(4, "已取消", "danger");

        private final int code;
        private final String description;
        private final String styleClass;

        OrderStatus(int code, String description, String styleClass) {
            this.code = code;
            this.description = description;
            this.styleClass = styleClass;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static OrderStatus fromCode(int code) {
            for (OrderStatus status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            throw new IllegalArgumentException("无效状态码: " + code);
        }

        public String getStyleClass() {
            return styleClass;
        }
    }

    // 构造方法
    public Order() {
        // 无参构造方法（JPA/MyBatis等框架需要）
    }

    public Order(Long userId, Long storeId, BigDecimal totalPrice) {
        this.userId = userId;
        this.storeId = storeId;
        this.totalPrice = totalPrice;
        this.status = OrderStatus.PENDING; // 默认状态
        this.createdAt = LocalDateTime.now();
    }

    // Getter/Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getRiderId() {
        return riderId;
    }

    public void setRiderId(Long riderId) {
        this.riderId = riderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    // 数据库状态码转换方法（用于持久层操作）
    public int getStatusCode() {
        return status.getCode();
    }

    public void setStatusCode(int code) {
        this.status = OrderStatus.fromCode(code);
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", storeId=" + storeId +
                ", riderId=" + riderId +
                ", status=" + status +
                ", totalPrice=" + totalPrice +
                ", createdAt=" + createdAt +
                ", deadline=" + deadline +
                ", endedAt=" + endedAt +
                '}';
    }
}
