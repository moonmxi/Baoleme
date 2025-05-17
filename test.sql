-- 《饱了么》数据库设计完整初始化SQL

-- 一、用户表 user
CREATE TABLE IF NOT EXISTS user (
                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    gander varchar(5) NOT NULL,
                                    username VARCHAR(50) NOT NULL UNIQUE,
                                    password VARCHAR(100) NOT NULL,
                                    phone VARCHAR(20),
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) AUTO_INCREMENT=10000001;

-- 二、商家表 merchant
CREATE TABLE IF NOT EXISTS merchant (
                                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                        username VARCHAR(50) NOT NULL UNIQUE,
                                        password VARCHAR(100) NOT NULL,
                                        phone VARCHAR(20),
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
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) AUTO_INCREMENT=30000001;

-- 四、管理员表 administrator
CREATE TABLE IF NOT EXISTS admin (
                                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                             password VARCHAR(100) NOT NULL
) AUTO_INCREMENT=10001;

-- 五、店铺表 store
CREATE TABLE IF NOT EXISTS store (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     merchant_id BIGINT NOT NULL,
                                     name VARCHAR(100) NOT NULL,
                                     type VARCHAR(50),
                                     location VARCHAR(100),
                                     rating DECIMAL(2,1) DEFAULT 5.0,
                                     balance DECIMAL(10,2) DEFAULT 0.0,
                                     status TINYINT DEFAULT 1,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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
                                       status TINYINT DEFAULT 1,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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
                                       total_price DECIMAL(10,2),
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
                                          delivery_price DECIMAL(10,2) NOT NULL,
                                          price DECIMAL(10,2) NOT NULL,
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
                                      comment TEXT,
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
                                      merchant_id BIGINT,
                                      discount DECIMAL(5,2) NOT NULL,
                                      expiration_date DATE,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) AUTO_INCREMENT=110000001;

-- 十三、消息表 message
CREATE TABLE IF NOT EXISTS message(
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      content VARCHAR(300) NOT NULL,
                                      sender_id BIGINT NOT NULL,
                                      receive_id BIGINT NOT NULL,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      FOREIGN KEY (sender_id) REFERENCES user(id),
                                      FOREIGN KEY (receive_id) REFERENCES user(id)
) AUTO_INCREMENT=120000001;

-- 十四、收藏夹表 favorite
CREATE TABLE IF NOT EXISTS favorite (
                                        user_id BIGINT NOT NULL,
                                        shop_id BIGINT NOT NULL,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



-- 插入商家
INSERT INTO merchant (id, username, password, phone) VALUES
                                                         (20000001, 'merchant01', '123456', '13900000001'),
                                                         (20000002, 'merchant02', '123456', '13900000002');

-- 插入管理员
INSERT INTO administrator (id, username, password, phone) VALUES
    (50000001, 'admin01', '123456', '13700000001');

-- 插入骑手
INSERT INTO rider (id, username, password, phone, order_status, dispatch_mode, balance) VALUES
                                                                                            (30000001, 'rider01', '123456', '19900000001', 1, 1, 100),
                                                                                            (30000002, 'rider02', '123456', '19900000002', 1, 0, 200);

-- 插入店铺
INSERT INTO store (id, merchant_id, name, type, location) VALUES
                                                              (40000001, 20000001, 'store01', '快餐', '北京'),
                                                              (40000002, 20000002, 'store02', '饮品', '上海');

-- 插入商品
INSERT INTO product (id, store_id, name, description, price, category) VALUES
                                                                           (60000001, 40000001, '汉堡', '美味汉堡', 20.00, '食品'),
                                                                           (60000002, 40000002, '奶茶', '香浓奶茶', 15.00, '饮品');

-- 插入订单
INSERT INTO `order` (id, user_id, store_id, rider_id, status, total_price, created_at, deadline) VALUES
                                                                                                     (70000001, 10000001, 40000001, 30000001, 2, 35.00, NOW(), DATE_ADD(NOW(), INTERVAL 45 MINUTE)),
                                                                                                     (70000002, 10000002, 40000002, 30000002, 1, 25.00, NOW(), DATE_ADD(NOW(), INTERVAL 45 MINUTE));

-- 插入订单明细
INSERT INTO order_item (id, order_id, product_id, quantity, delivery_price, price) VALUES
                                                                                       (80000001, 70000001, 60000001, 2, 5.00, 20.00),
                                                                                       (80000002, 70000002, 60000002, 1, 5.00, 15.00);

-- 插入评价
INSERT INTO review (id, user_id, store_id, product_id, rating, comment) VALUES
                                                                            (90000001, 10000001, 40000001, 60000001, 5, '很好吃'),
                                                                            (90000002, 10000002, 40000002, 60000002, 4, '不错喝');

-- 插入购物车
INSERT INTO cart (user_id, product_id, quantity) VALUES
                                                     (10000001, 60000001, 2),
                                                     (10000002, 60000002, 3);

INSERT INTO admin(id, password) VALUES (10001, '123456'), (10002, '123456');
