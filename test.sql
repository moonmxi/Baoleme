-- 《饱了么》数据库设计完整初始化SQL

-- 一、用户表 user
CREATE TABLE IF NOT EXISTS user (
                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    username VARCHAR(50) NOT NULL UNIQUE,
                                    password VARCHAR(100) NOT NULL,
                                    description VARCHAR(100),
                                    location VARCHAR(100),
                                    gender varchar(2),
                                    phone VARCHAR(20),
                                    avatar VARCHAR(50),
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) AUTO_INCREMENT=10000001;

-- 二、商家表 merchant
CREATE TABLE IF NOT EXISTS merchant (
                                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                        username VARCHAR(50) NOT NULL UNIQUE,
                                        password VARCHAR(100) NOT NULL,
                                        phone VARCHAR(20) UNIQUE,
                                        avatar VARCHAR(50),
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) AUTO_INCREMENT=20000001;

-- 三、骑手表 rider
CREATE TABLE IF NOT EXISTS rider (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     username VARCHAR(50) NOT NULL UNIQUE,
                                     password VARCHAR(100) NOT NULL,
                                     order_status INT DEFAULT 1,
                                     dispatch_mode INT DEFAULT 1,
                                     phone VARCHAR(20),
                                     balance BIGINT,
                                     avatar VARCHAR(50),
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) AUTO_INCREMENT=30000001;

-- 四、管理员表 administrator
CREATE TABLE IF NOT EXISTS admin (
                                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                            password VARCHAR(100) NOT NULL
) AUTO_INCREMENT=40000001;

-- 五、店铺表 store
CREATE TABLE IF NOT EXISTS store (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     merchant_id BIGINT NOT NULL,
                                     name VARCHAR(100) NOT NULL UNIQUE ,
                                     description VARCHAR(50),
                                     location VARCHAR(100),
                                     rating DECIMAL(2,1) DEFAULT 5.0,
                                     status TINYINT DEFAULT 1,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     image VARCHAR(50),
                                     FOREIGN KEY (merchant_id) REFERENCES merchant(id)
) AUTO_INCREMENT=50000001;

-- 六、商品表 product
CREATE TABLE IF NOT EXISTS product (
                                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                       store_id BIGINT NOT NULL,
                                       name VARCHAR(100) NOT NULL,
                                       description VARCHAR(255),
                                       price DECIMAL(10,2) NOT NULL,
                                       category VARCHAR(50),
                                       stock int,
                                       rating DECIMAL(2,1),
                                       status TINYINT DEFAULT 1,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       image VARCHAR(50),
                                       FOREIGN KEY (store_id) REFERENCES store(id)
) AUTO_INCREMENT=60000001;

-- 七、销量表 sales
CREATE TABLE IF NOT EXISTS sales (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     product_id BIGINT NOT NULL,
                                     store_id BIGINT NOT NULL,
                                     sale_date DATE NOT NULL,
                                     quantity INT NOT NULL DEFAULT 1,
                                     unit_price DECIMAL(10,2) NOT NULL,
                                     total_amount DECIMAL(10,2) GENERATED ALWAYS AS (quantity * unit_price) STORED,
                                     payment_method VARCHAR(20),
                                     customer_id BIGINT,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     FOREIGN KEY (product_id) REFERENCES product(id),
                                     FOREIGN KEY (store_id) REFERENCES store(id),
                                     FOREIGN KEY (customer_id) REFERENCES user(id),
                                     INDEX (product_id),
                                     INDEX (store_id),
                                     INDEX (sale_date)
) AUTO_INCREMENT=70000001;

-- 八、订单表 order
CREATE TABLE IF NOT EXISTS `order` (
                                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                       user_id BIGINT NOT NULL,
                                       store_id BIGINT NOT NULL,
                                       rider_id BIGINT,
                                       status INT DEFAULT 0,
                                       user_location VARCHAR(100),
                                       store_location VARCHAR(100),
                                       total_price DECIMAL(10,2),
                                       actual_price DECIMAL(10,2),
                                       delivery_price  DECIMAL(10,2),
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       deadline TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       ended_at TIMESTAMP NULL,
                                       FOREIGN KEY (user_id) REFERENCES user(id),
                                       FOREIGN KEY (store_id) REFERENCES store(id),
                                       FOREIGN KEY (rider_id) REFERENCES rider(id),
                                       INDEX (status),
                                       INDEX (rider_id),
                                       INDEX (store_id)
) AUTO_INCREMENT=80000001;

-- 触发器：订单创建时自动设置deadline为created_at + 45分钟
DROP TRIGGER IF EXISTS order_set_deadline;
DELIMITER $$
CREATE TRIGGER order_set_deadline
    BEFORE INSERT ON `order`
    FOR EACH ROW
BEGIN
    IF NEW.deadline IS NULL THEN
        SET NEW.deadline = DATE_ADD(NEW.created_at, INTERVAL 45 MINUTE);
    END IF;
END$$
DELIMITER ;

-- 九、订单明细表 order_item
CREATE TABLE IF NOT EXISTS order_item (
                                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                          order_id BIGINT NOT NULL,
                                          product_id BIGINT NOT NULL,
                                          quantity INT NOT NULL DEFAULT 1,
                                          FOREIGN KEY (order_id) REFERENCES `order`(id),
                                          FOREIGN KEY (product_id) REFERENCES product(id)
) AUTO_INCREMENT=90000001;

-- 十、评价表 review
CREATE TABLE IF NOT EXISTS review (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      user_id BIGINT NOT NULL,
                                      store_id BIGINT,
                                      product_id BIGINT,
                                      rating INT NOT NULL,
                                      comment varchar(300),
                                      image  VARCHAR(50),
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      FOREIGN KEY (user_id) REFERENCES user(id),
                                      FOREIGN KEY (store_id) REFERENCES store(id),
                                      FOREIGN KEY (product_id) REFERENCES product(id)
) AUTO_INCREMENT=100000001;

-- 十一、购物车表 cart
CREATE TABLE IF NOT EXISTS cart (
                                    user_id BIGINT NOT NULL,
                                    product_id BIGINT NOT NULL,
                                    quantity INT NOT NULL DEFAULT 1,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    PRIMARY KEY (user_id, product_id),
                                    FOREIGN KEY (user_id) REFERENCES user(id),
                                    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- 十二、优惠券表 coupon
CREATE TABLE IF NOT EXISTS coupon (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      user_id BIGINT NOT NULL,
                                      store_id BIGINT NOT NULL,
                                      type int NOT NULL,
                                      discount DECIMAL(5,2) NOT NULL,
                                      expiration_date DATETIME,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      is_used BOOLEAN DEFAULT FALSE,
                                      full_amount DECIMAL(10, 2),
                                      reduce_amount DECIMAL(10, 2)
) AUTO_INCREMENT=110000001;

-- 十三、消息表 message
CREATE TABLE IF NOT EXISTS message(
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      content VARCHAR(300) NOT NULL,
                                      sender_id BIGINT NOT NULL,
                                      receiver_id BIGINT NOT NULL,
                                      sender_role VARCHAR(10) NOT NULL,
                                      receiver_role VARCHAR(10) NOT NULL,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      FOREIGN KEY (sender_id) REFERENCES user(id),
                                      FOREIGN KEY (receiver_id) REFERENCES user(id)
) AUTO_INCREMENT=120000001;

-- 十四、收藏夹表 favorite
CREATE TABLE IF NOT EXISTS favorite (
                                        user_id BIGINT NOT NULL,
                                        product_id BIGINT,
                                        store_id BIGINT,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 一、用户表 user 三条插入语句
INSERT INTO user (username, password, description, location, gender, phone, avatar) VALUES
                                                                                        ('alice', 'pwd123456', '热爱美食的学生', '天津', '女', '13800001111', 'avatar1.jpg'),
                                                                                        ('bob', 'pwd234567', '程序员', '北京', '男', '13900002222', 'avatar2.jpg'),
                                                                                        ('charlie', 'pwd345678', '爱运动的大学生', '上海', '男', '13700003333', 'avatar3.jpg');

-- 二、商家表 merchant 三条插入语句
INSERT INTO merchant (username, password, phone, avatar) VALUES
                                                             ('foodking', 'mkpass123', '13100004444', 'merchant1.png'),
                                                             ('tastyhouse', 'mkpass234', '13200005555', 'merchant2.png'),
                                                             ('quickbites', 'mkpass345', '13300006666', 'merchant3.png');

-- 三、骑手表 rider 三条插入语句
INSERT INTO rider (username, password, order_status, dispatch_mode, phone, balance, avatar) VALUES
                                                                                                ('rider01', 'rdpass123', 1, 1, '15000001111', 1000, 'rider1.jpg'),
                                                                                                ('rider02', 'rdpass234', 1, 2, '15000002222', 500, 'rider2.jpg'),
                                                                                                ('rider03', 'rdpass345', 0, 1, '15000003333', 300, 'rider3.jpg');

-- 四、管理员表 admin 三条插入语句
INSERT INTO admin (password) VALUES
                                 ('adminpass123'),
                                 ('adminpass234'),
                                 ('adminpass345');

-- 五、店铺表 store 三条插入语句
INSERT INTO store (merchant_id, name, description, location, rating, status, image) VALUES
                                                                                        (20000001, '美味炸鸡', '校园内最好吃的炸鸡店', '天津校园内A区', 4.8, 1, 'store1.jpg'),
                                                                                        (20000002, '健康沙拉', '新鲜健康，绿色食品', '天津校园内B区', 4.5, 1, 'store2.jpg'),
                                                                                        (20000003, '快餐小馆', '快捷实惠，味道不错', '天津校园内C区', 4.0, 1, 'store3.jpg');

-- 六、商品表 product 三条插入语句
INSERT INTO product (store_id, name, description, price, category, stock, rating, status, image) VALUES
                                                                                                     (50000001, '炸鸡翅', '香辣可口炸鸡翅', 15.00, '炸鸡', 100, 4.7, 1, 'prod1.jpg'),
                                                                                                     (50000001, '薯条', '黄金脆薯条', 8.00, '小吃', 200, 4.3, 1, 'prod2.jpg'),
                                                                                                     (50000002, '凯撒沙拉', '新鲜蔬菜搭配特制酱汁', 20.00, '沙拉', 50, 4.6, 1, 'prod3.jpg');

-- 七、销量表 sales 三条插入语句
INSERT INTO sales (product_id, store_id, sale_date, quantity, unit_price, payment_method, customer_id) VALUES
                                                                                                           (60000001, 50000001, '2025-05-20', 2, 15.00, '微信支付', 10000001),
                                                                                                           (60000002, 50000001, '2025-05-20', 1, 8.00, '支付宝', 10000002),
                                                                                                           (60000003, 50000002, '2025-05-19', 3, 20.00, '现金', 10000003);

-- 八、订单表 order 三条插入语句
INSERT INTO `order` (user_id, store_id, rider_id, status, user_location, store_location, total_price, actual_price, delivery_price, ended_at) VALUES
                                                                                                                                                  (10000001, 50000001, 30000001, 2, '天津校园A宿舍楼', '天津校园内A区', 38.00, 35.00, 3.00, NULL),
                                                                                                                                                  (10000002, 50000002, 30000002, 1, '天津校园B宿舍楼', '天津校园内B区', 20.00, 20.00, 0.00, NULL),
                                                                                                                                                  (10000003, 50000003, NULL, 0, '天津校园C宿舍楼', '天津校园内C区', 15.00, 15.00, 0.00, NULL);

-- 九、订单明细表 order_item 三条插入语句
INSERT INTO order_item (order_id, product_id, quantity) VALUES
                                                            (80000001, 60000001, 2),
                                                            (80000001, 60000002, 1),
                                                            (80000002, 60000003, 1);

-- 十、评价表 review 三条插入语句
INSERT INTO review (user_id, store_id, product_id, rating, comment, image) VALUES
                                                                               (10000001, 50000001, 60000001, 5, '味道很好，配送快', 'review1.jpg'),
                                                                               (10000002, 50000002, 60000003, 4, '沙拉很新鲜', NULL),
                                                                               (10000003, 50000001, 60000002, 3, '薯条稍微油腻', 'review2.jpg');

-- 十一、购物车表 cart 三条插入语句
INSERT INTO cart (user_id, product_id, quantity) VALUES
                                                     (10000001, 60000001, 1),
                                                     (10000002, 60000002, 2),
                                                     (10000003, 60000003, 1);

-- 十二、优惠券表 coupon 三条插入语句
INSERT INTO coupon (user_id, store_id, type, discount, expiration_date, is_used, full_amount, reduce_amount) VALUES
                                                                                                                 (10000001, 50000001, 1, 0.90, '2025-06-30 23:59:59', FALSE, 50.00, 5.00),
                                                                                                                 (10000002, 50000002, 2, 0.80, '2025-07-15 23:59:59', FALSE, 100.00, 20.00),
                                                                                                                 (10000003, 50000003, 1, 0.85, '2025-05-31 23:59:59', TRUE, 30.00, 3.00);

-- 十三、消息表 message 三条插入语句
INSERT INTO message (content, sender_id, receiver_id, sender_role, receiver_role) VALUES
                                                                                      ('您好，请问订单什么时候送达？', 10000001, 10000002, 'user', 'merchant'),
                                                                                      ('订单已确认，我们正在备餐。', 10000002, 10000001, 'merchant', 'user'),
                                                                                      ('骑手已经出发，请耐心等待。', 10000003, 10000001, 'rider', 'user');

-- 十四、收藏夹表 favorite 三条插入语句
INSERT INTO favorite (user_id, product_id, store_id) VALUES
                                                         (10000001, 60000001, NULL),
                                                         (10000002, NULL, 50000002),
                                                         (10000003, 60000003, 50000003);