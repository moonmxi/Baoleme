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
                                    avatar VARCHAR(511),
                                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) AUTO_INCREMENT=10000001;

-- 二、商家表 merchant
CREATE TABLE IF NOT EXISTS merchant (
                                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                        username VARCHAR(50) NOT NULL UNIQUE,
                                        password VARCHAR(100) NOT NULL,
                                        phone VARCHAR(20) UNIQUE,
                                        avatar VARCHAR(511),
                                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP
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
                                     avatar VARCHAR(511),
                                     INDEX (order_status),
                                     INDEX (dispatch_mode),
                                     created_at DATETIME DEFAULT CURRENT_TIMESTAMP
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
                                     type VARCHAR(10),
                                     description VARCHAR(50),
                                     location VARCHAR(100),
                                     distance  DECIMAL(5,2),
                                     rating DECIMAL(2,1) DEFAULT 0,
                                     status TINYINT DEFAULT 0,
                                     avg_price DECIMAL(10,2),
                                     created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                     image VARCHAR(511)
) AUTO_INCREMENT=50000001;

-- 六、商品表 product
CREATE TABLE IF NOT EXISTS product (
                                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                       store_id BIGINT NOT NULL,
                                       name VARCHAR(100) NOT NULL,
                                       description VARCHAR(255),
                                       price DECIMAL(10,2) NOT NULL,
                                       category VARCHAR(50),
                                       stock int not null,
                                       rating DECIMAL(2,1),
                                       status TINYINT DEFAULT 0,
                                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                       image VARCHAR(511)
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
                                       remark VARCHAR(255),
                                       delivery_price  DECIMAL(10,2),
                                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                       deadline DATETIME DEFAULT CURRENT_TIMESTAMP,
                                       ended_at DATETIME NULL,
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
                                          order_id BIGINT NOT NULL,
                                          product_id BIGINT NOT NULL,
                                          quantity INT NOT NULL DEFAULT 1,
                                          PRIMARY KEY (order_id, product_id)
) AUTO_INCREMENT=90000001;

-- 十、评价表 review
CREATE TABLE IF NOT EXISTS review (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      user_id BIGINT NOT NULL,
                                      store_id BIGINT,
                                      product_id BIGINT,
                                      rating INT NOT NULL,
                                      comment varchar(300),
                                      image  VARCHAR(511),
                                      created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) AUTO_INCREMENT=100000001;

-- 十一、购物车表 cart
CREATE TABLE IF NOT EXISTS cart (
                                    user_id BIGINT NOT NULL,
                                    product_id BIGINT NOT NULL,
                                    quantity INT NOT NULL DEFAULT 1,
                                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                    PRIMARY KEY (user_id, product_id)
);

-- 十二、优惠券表 coupon
CREATE TABLE IF NOT EXISTS coupon (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      user_id BIGINT NOT NULL,
                                      store_id BIGINT NOT NULL,
                                      type int NOT NULL,
                                      discount DECIMAL(5,2) NOT NULL,
                                      expiration_date DATETIME,
                                      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
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
                                      created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) AUTO_INCREMENT=120000001;

-- 十四、收藏夹表 favorite
CREATE TABLE IF NOT EXISTS favorite (
                                        user_id BIGINT NOT NULL,
                                        product_id BIGINT,
                                        store_id BIGINT,
                                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
-- 十五、浏览记录表
CREATE TABLE IF NOT EXISTS browse_history (
                                              user_id BIGINT NOT NULL,
                                              store_id BIGINT NOT NULL,
                                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                              UNIQUE KEY uc_user_store (user_id, store_id)  -- 联合唯一约束
);

-- 1. 创建第一个触发器：当order状态更新为3时，将order_item插入sales表
DELIMITER $$
DROP TRIGGER IF EXISTS order_status_to_sales;
CREATE TRIGGER order_status_to_sales
    AFTER UPDATE ON `order`
    FOR EACH ROW
BEGIN
    -- 检查状态是否更新为3（已完成）且之前不是3
    IF NEW.status = 3 AND OLD.status <> 3 THEN
        -- 插入销售记录
        INSERT INTO sales (product_id, store_id, sale_date, quantity, unit_price)
        SELECT
            oi.product_id,
            o.store_id,
            COALESCE(o.ended_at, CURDATE()),  -- 使用订单结束时间或当前日期
            oi.quantity,
            p.price
        FROM order_item oi
                 JOIN product p ON oi.product_id = p.id
                 JOIN `order` o on o.id = oi.order_id
        WHERE oi.order_id = NEW.id;
    END IF;
END$$
DELIMITER ;

-- 2. 创建存储过程：计算并更新商店的加权均价
DELIMITER $$
DROP PROCEDURE IF EXISTS UpdateStoreWeightedPrice;
CREATE PROCEDURE UpdateStoreWeightedPrice(IN store_id BIGINT)
BEGIN
    DECLARE avg_price DECIMAL(10,2);

    -- 计算前3名热销商品的加权均价
    SET avg_price = COALESCE((
                                 SELECT SUM(p.price * sale.total_quantity) / SUM(sale.total_quantity)
                                 FROM (
                                          SELECT s2.product_id, SUM(s2.quantity) AS total_quantity
                                          FROM sales s2
                                          WHERE s2.store_id = store_id
                                            AND s2.sale_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
                                          GROUP BY s2.product_id
                                          ORDER BY total_quantity DESC
                                          LIMIT 3
                                      ) sale
                                          JOIN product p ON p.id = sale.product_id
                             ), 0);

    -- 更新store表的avgPrice字段
    UPDATE store
    SET avg_price = avg_price
    WHERE id = store_id;
END$$
DELIMITER ;

-- 3. 创建第二个触发器：当sales表更新后更新store表
DELIMITER $$
DROP TRIGGER IF EXISTS sales_update_store_price;
CREATE TRIGGER sales_update_store_price
    AFTER INSERT ON sales
    FOR EACH ROW
BEGIN
    -- 调用存储过程更新对应商店的均价
    CALL UpdateStoreWeightedPrice(NEW.store_id);
END$$
DELIMITER ;

-- 4. 为UPDATE和DELETE操作创建额外触发器
DELIMITER $$
DROP TRIGGER IF EXISTS sales_update_store_price_update;
CREATE TRIGGER sales_update_store_price_update
    AFTER UPDATE ON sales
    FOR EACH ROW
BEGIN
    -- 处理store_id变更的情况
    IF OLD.store_id <> NEW.store_id THEN
        CALL UpdateStoreWeightedPrice(OLD.store_id);
        CALL UpdateStoreWeightedPrice(NEW.store_id);
    ELSE
        CALL UpdateStoreWeightedPrice(NEW.store_id);
    END IF;
END$$
DELIMITER ;

DELIMITER $$
DROP TRIGGER IF EXISTS sales_update_store_price_delete;
CREATE TRIGGER sales_update_store_price_delete
    AFTER DELETE ON sales
    FOR EACH ROW
BEGIN
    CALL UpdateStoreWeightedPrice(OLD.store_id);
END$$
DELIMITER ;
-- 插入语句
-- 一、用户表 user 15 条插入语句
INSERT INTO user (username, password, description, location, gender, phone, avatar) VALUES
                                                                                        ('user01', 'pwd000001', '美食爱好者', '北京', '女', '13800001111', 'avatar1.jpg'),
                                                                                        ('user02', 'pwd000002', '程序员', '上海', '男', '13800002222', 'avatar2.jpg'),
                                                                                        ('user03', 'pwd000003', '学生党', '天津', '女', '13800003333', 'avatar3.jpg'),
                                                                                        ('user04', 'pwd000004', '上班族', '广州', '男', '13800004444', 'avatar4.jpg'),
                                                                                        ('user05', 'pwd000005', '健身达人', '深圳', '女', '13800005555', 'avatar5.jpg'),
                                                                                        ('user06', 'pwd000006', '摄影爱好者', '南京', '男', '13800006666', 'avatar6.jpg'),
                                                                                        ('user07', 'pwd000007', '游戏玩家', '杭州', '女', '13800007777', 'avatar7.jpg'),
                                                                                        ('user08', 'pwd000008', '旅游达人', '成都', '男', '13800008888', 'avatar8.jpg'),
                                                                                        ('user09', 'pwd000009', '电影爱好者', '武汉', '女', '13800009999', 'avatar9.jpg'),
                                                                                        ('user10', 'pwd000010', '阅读爱好者', '西安', '男', '13800001010', 'avatar10.jpg'),
                                                                                        ('user11', 'pwd000011', '学生党', '长沙', '女', '13800001111', 'avatar11.jpg'),
                                                                                        ('user12', 'pwd000012', '设计师', '青岛', '男', '13800001212', 'avatar12.jpg'),
                                                                                        ('user13', 'pwd000013', '音乐发烧友', '大连', '女', '13800001313', 'avatar13.jpg'),
                                                                                        ('user14', 'pwd000014', '科技极客', '昆明', '男', '13800001414', 'avatar14.jpg'),
                                                                                        ('user15', 'pwd000015', '美食评论家', '沈阳', '女', '13800001515', 'avatar15.jpg');

-- 二、商家表 merchant 15 条插入语句
INSERT INTO merchant (username, password, phone, avatar) VALUES
                                                             ('merchant01', 'mkpass0001', '13900001111', 'merchant1.png'),
                                                             ('merchant02', 'mkpass0002', '13900002222', 'merchant2.png'),
                                                             ('merchant03', 'mkpass0003', '13900003333', 'merchant3.png'),
                                                             ('merchant04', 'mkpass0004', '13900004444', 'merchant4.png'),
                                                             ('merchant05', 'mkpass0005', '13900005555', 'merchant5.png'),
                                                             ('merchant06', 'mkpass0006', '13900006666', 'merchant6.png'),
                                                             ('merchant07', 'mkpass0007', '13900007777', 'merchant7.png'),
                                                             ('merchant08', 'mkpass0008', '13900008888', 'merchant8.png'),
                                                             ('merchant09', 'mkpass0009', '13900009999', 'merchant9.png'),
                                                             ('merchant10', 'mkpass0010', '13900001010', 'merchant10.png'),
                                                             ('merchant11', 'mkpass0011', '13900001112', 'merchant11.png'),
                                                             ('merchant12', 'mkpass0012', '13900001212', 'merchant12.png'),
                                                             ('merchant13', 'mkpass0013', '13900001313', 'merchant13.png'),
                                                             ('merchant14', 'mkpass0014', '13900001414', 'merchant14.png'),
                                                             ('merchant15', 'mkpass0015', '13900001515', 'merchant15.png');

-- 三、骑手表 rider 15 条插入语句
INSERT INTO rider (username, password, order_status, dispatch_mode, phone, balance, avatar) VALUES
                                                                                                ('rider01', 'rdpass0001', 1, 1, '15000001111', 1200, 'rider1.jpg'),
                                                                                                ('rider02', 'rdpass0002', 1, 2, '15000002222',  800, 'rider2.jpg'),
                                                                                                ('rider03', 'rdpass0003', 0, 1, '15000003333',  500, 'rider3.jpg'),
                                                                                                ('rider04', 'rdpass0004', 1, 1, '15000004444', 1500, 'rider4.jpg'),
                                                                                                ('rider05', 'rdpass0005', 0, 2, '15000005555',  300, 'rider5.jpg'),
                                                                                                ('rider06', 'rdpass0006', 1, 1, '15000006666', 2000, 'rider6.jpg'),
                                                                                                ('rider07', 'rdpass0007', 1, 2, '15000007777',  950, 'rider7.jpg'),
                                                                                                ('rider08', 'rdpass0008', 0, 1, '15000008888',  400, 'rider8.jpg'),
                                                                                                ('rider09', 'rdpass0009', 1, 1, '15000009999', 1100, 'rider9.jpg'),
                                                                                                ('rider10', 'rdpass0010', 1, 2, '15000001010',  750, 'rider10.jpg'),
                                                                                                ('rider11', 'rdpass0011', 0, 1, '15000001111',  650, 'rider11.jpg'),
                                                                                                ('rider12', 'rdpass0012', 1, 2, '15000001212',  900, 'rider12.jpg'),
                                                                                                ('rider13', 'rdpass0013', 1, 1, '15000001313', 1300, 'rider13.jpg'),
                                                                                                ('rider14', 'rdpass0014', 0, 2, '15000001414',  550, 'rider14.jpg'),
                                                                                                ('rider15', 'rdpass0015', 1, 1, '15000001515', 1750, 'rider15.jpg');

-- 四、管理员表 admin 15 条插入语句
INSERT INTO admin (password) VALUES
                                 ('adminpass001'),
                                 ('adminpass002'),
                                 ('adminpass003'),
                                 ('adminpass004'),
                                 ('adminpass005'),
                                 ('adminpass006'),
                                 ('adminpass007'),
                                 ('adminpass008'),
                                 ('adminpass009'),
                                 ('adminpass010'),
                                 ('adminpass011'),
                                 ('adminpass012'),
                                 ('adminpass013'),
                                 ('adminpass014'),
                                 ('adminpass015');

-- 五、店铺表 store 15 条插入语句
INSERT INTO store (merchant_id, name, type, description, location, rating, status, image) VALUES
                                                                                              (20000001, '快餐小屋01', '快餐', '快捷美味', '北京海淀区', 4.7, 1, 'store1.jpg'),
                                                                                              (20000002, '甜品乐园02', '甜品', '甜品烘焙坊', '上海浦东新区', 4.5, 1, 'store2.jpg'),
                                                                                              (20000003, '清新沙拉03', '饮品', '健康沙拉和果汁', '天津河西区', 4.6, 1, 'store3.jpg'),
                                                                                              (20000004, '面食大师04', '面食', '手工面条', '广州天河区', 4.8, 1, 'store4.jpg'),
                                                                                              (20000005, '烧烤天地05', '烧烤', '夜市烧烤串', '深圳罗湖区', 4.3, 1, 'store5.jpg'),
                                                                                              (20000006, '汉堡王06', '快餐', '经典汉堡', '南京鼓楼区', 4.4, 1, 'store6.jpg'),
                                                                                              (20000007, '果汁清吧07', '饮品', '新鲜果汁', '杭州西湖区', 4.2, 1, 'store7.jpg'),
                                                                                              (20000008, '小龙虾08', '烧烤', '麻辣小龙虾', '成都武侯区', 4.9, 1, 'store8.jpg'),
                                                                                              (20000009, '咖啡时光09', '饮品', '精品咖啡', '武汉洪山区', 4.1, 1, 'store9.jpg'),
                                                                                              (20000010, '比萨之家10', '快餐', '意式披萨', '西安雁塔区', 4.6, 1, 'store10.jpg'),
                                                                                              (20000011, '盖饭达人11', '快餐', '快餐盖浇饭', '长沙岳麓区', 4.5, 1, 'store11.jpg'),
                                                                                              (20000012, '奶茶物语12', '饮品', '特色奶茶', '青岛市南区', 4.3, 1, 'store12.jpg'),
                                                                                              (20000013, '寿司吧13', '快餐', '新鲜寿司', '大连甘井子区', 4.7, 1, 'store13.jpg'),
                                                                                              (20000014, '重庆小面14', '面食', '重庆辣味小面', '昆明五华区', 4.8, 1, 'store14.jpg'),
                                                                                              (20000015, '花样蛋糕15', '甜品', '精致蛋糕店', '沈阳和平区', 4.9, 1, 'store15.jpg');

-- 六、商品表 product 15 条插入语句
INSERT INTO product (store_id, name, description, price, category, stock, rating, status, image) VALUES
                                                                                                     (50000001, '鸡腿汉堡', '经典鸡腿堡', 22.00, '快餐', 150, 4.7, 1, 'prod1.jpg'),
                                                                                                     (50000002, '草莓蛋糕', '新鲜草莓蛋糕', 45.00, '甜品', 80, 4.9, 1, 'prod2.jpg'),
                                                                                                     (50000003, '凯撒沙拉', '健康蔬菜沙拉', 28.00, '饮品', 60, 4.5, 1, 'prod3.jpg'),
                                                                                                     (50000004, '牛肉拉面', '手工牛肉拉面', 30.00, '面食', 100, 4.8, 1, 'prod4.jpg'),
                                                                                                     (50000005, '孜然羊肉串', '经典孜然味', 25.00, '烧烤', 200, 4.6, 1, 'prod5.jpg'),
                                                                                                     (50000006, '双层芝士汉堡', '浓郁芝士风味', 35.00, '快餐', 120, 4.8, 1, 'prod6.jpg'),
                                                                                                     (50000007, '芒果冰沙', '新鲜芒果制成', 18.00, '饮品', 90, 4.4, 1, 'prod7.jpg'),
                                                                                                     (50000008, '蒜香小龙虾', '麻辣蒜香风味', 58.00, '烧烤', 50, 4.9, 1, 'prod8.jpg'),
                                                                                                     (50000009, '卡布奇诺', '香醇拿铁', 20.00, '饮品', 70, 4.3, 1, 'prod9.jpg'),
                                                                                                     (50000010, '夏威夷披萨', '菠萝火腿披萨', 60.00, '快餐', 40, 4.6, 1, 'prod10.jpg'),
                                                                                                     (50000011, '照烧鸡腿盖饭', '香甜照烧酱', 32.00, '快餐', 110, 4.5, 1, 'prod11.jpg'),
                                                                                                     (50000012, '珍珠奶茶', '经典黑糖珍珠', 15.00, '饮品', 150, 4.7, 1, 'prod12.jpg'),
                                                                                                     (50000013, '三文鱼寿司', '新鲜三文鱼', 48.00, '快餐', 60, 4.8, 1, 'prod13.jpg'),
                                                                                                     (50000014, '麻辣小面', '地道重庆风味', 22.00, '面食', 130, 4.9, 1, 'prod14.jpg'),
                                                                                                     (50000015, '巧克力蛋糕', '浓郁巧克力味', 50.00, '甜品', 75, 4.9, 1, 'prod15.jpg');

-- 七、销量表 sales 15 条插入语句
INSERT INTO sales (product_id, store_id, sale_date, quantity, unit_price, payment_method, customer_id) VALUES
                                                                                                           (60000001, 50000001, '2025-05-18',  2, 22.00, '微信支付',   10000001),
                                                                                                           (60000002, 50000002, '2025-05-17',  1, 45.00, '支付宝',     10000002),
                                                                                                           (60000003, 50000003, '2025-05-16',  3, 28.00, '现金',       10000003),
                                                                                                           (60000004, 50000004, '2025-05-15',  1, 30.00, '信用卡',     10000004),
                                                                                                           (60000005, 50000005, '2025-05-14',  4, 25.00, '微信支付',   10000005),
                                                                                                           (60000006, 50000006, '2025-05-13',  2, 35.00, '支付宝',     10000006),
                                                                                                           (60000007, 50000007, '2025-05-12',  1, 18.00, '现金',       10000007),
                                                                                                           (60000008, 50000008, '2025-05-11',  5, 58.00, '微信支付',   10000008),
                                                                                                           (60000009, 50000009, '2025-05-10',  2, 20.00, '支付宝',     10000009),
                                                                                                           (60000010, 50000010, '2025-05-09',  1, 60.00, '信用卡',     10000010),
                                                                                                           (60000011, 50000011, '2025-05-08',  3, 32.00, '微信支付',   10000011),
                                                                                                           (60000012, 50000012, '2025-05-07',  2, 15.00, '现金',       10000012),
                                                                                                           (60000013, 50000013, '2025-05-06',  1, 48.00, '支付宝',     10000013),
                                                                                                           (60000014, 50000014, '2025-05-05',  2, 22.00, '微信支付',   10000014),
                                                                                                           (60000015, 50000015, '2025-05-04',  4, 50.00, '信用卡',     10000015);

-- 八、订单表 `order` 15 条插入语句
INSERT INTO `order` (user_id, store_id, rider_id, status, user_location, store_location, total_price, actual_price, delivery_price, remark, ended_at) VALUES
                                                                                                                                                          (10000001, 50000001, 30000001, 2, '北京海淀区小区A栋',   '北京海淀区',  66.00,  63.00, 3.00, '尽快送达', NULL),
                                                                                                                                                          (10000002, 50000002, 30000002, 1, '上海浦东新区小区B栋',   '上海浦东新区', 45.00,  45.00, 0.00, NULL,          NULL),
                                                                                                                                                          (10000003, 50000003, 30000003, 3, '天津河西区小区C栋',     '天津河西区',   28.00,  28.00, 0.00, '少放沙拉汁', '2025-05-17 12:30:00'),
                                                                                                                                                          (10000004, 50000004, 30000004, 0, '广州天河区小区D栋',     '广州天河区',   30.00,  30.00, 0.00, NULL,          NULL),
                                                                                                                                                          (10000005, 50000005, 30000005, 2, '深圳罗湖区小区E栋',     '深圳罗湖区',   50.00,  47.00, 3.00, '加快配送',    NULL),
                                                                                                                                                          (10000006, 50000006, 30000001, 1, '南京鼓楼区小区F栋',     '南京鼓楼区',   35.00,  35.00, 0.00, NULL,          NULL),
                                                                                                                                                          (10000007, 50000007, 30000002, 3, '杭州西湖区小区G栋',     '杭州西湖区',   36.00,  36.00, 0.00, '去冰',       '2025-05-15 18:45:00'),
                                                                                                                                                          (10000008, 50000008, 30000003, 0, '成都武侯区小区H栋',     '成都武侯区',   58.00,  58.00, 0.00, NULL,          NULL),
                                                                                                                                                          (10000009, 50000009, 30000004, 2, '武汉洪山区小区I栋',     '武汉洪山区',   40.00,  37.00, 3.00, '少糖',       NULL),
                                                                                                                                                          (10000010, 50000010, 30000005, 1, '西安雁塔区小区J栋',     '西安雁塔区',   60.00,  60.00, 0.00, NULL,          NULL),
                                                                                                                                                          (10000011, 50000011, 30000001, 0, '长沙岳麓区小区K栋',     '长沙岳麓区',   32.00,  32.00, 0.00, NULL,          NULL),
                                                                                                                                                          (10000012, 50000012, 30000002, 3, '青岛市南区小区L栋',     '青岛市南区',   30.00,  30.00, 0.00, '多放珍珠',  '2025-05-12 14:20:00'),
                                                                                                                                                          (10000013, 50000013, 30000003, 2, '大连甘井子区小区M栋',  '大连甘井子区', 48.00,  45.00, 3.00, '送餐电话',  NULL),
                                                                                                                                                          (10000014, 50000014, 30000004, 1, '昆明五华区小区N栋',     '昆明五华区',   22.00,  22.00, 0.00, NULL,          NULL),
                                                                                                                                                          (10000015, 50000015, 30000005, 0, '沈阳和平区小区O栋',     '沈阳和平区',   50.00,  50.00, 0.00, NULL,          NULL);

-- 九、订单明细表 order_item 30 条插入语句（保证每个订单至少两个商品）
INSERT INTO order_item (order_id, product_id, quantity) VALUES
-- 订单 80000001
(80000001, 60000001, 1),
(80000001, 60000002, 2),
-- 订单 80000002
(80000002, 60000002, 1),
(80000002, 60000003, 1),
-- 订单 80000003
(80000003, 60000003, 2),
(80000003, 60000004, 1),
-- 订单 80000004
(80000004, 60000004, 1),
(80000004, 60000005, 2),
-- 订单 80000005
(80000005, 60000005, 1),
(80000005, 60000006, 1),
-- 订单 80000006
(80000006, 60000006, 2),
(80000006, 60000007, 1),
-- 订单 80000007
(80000007, 60000007, 1),
(80000007, 60000008, 1),
-- 订单 80000008
(80000008, 60000008, 2),
(80000008, 60000009, 1),
-- 订单 80000009
(80000009, 60000009, 1),
(80000009, 60000010, 1),
-- 订单 80000010
(80000010, 60000010, 2),
(80000010, 60000011, 1),
-- 订单 80000011
(80000011, 60000011, 1),
(80000011, 60000012, 1),
-- 订单 80000012
(80000012, 60000012, 2),
(80000012, 60000013, 1),
-- 订单 80000013
(80000013, 60000013, 1),
(80000013, 60000014, 1),
-- 订单 80000014
(80000014, 60000014, 2),
(80000014, 60000015, 1),
-- 订单 80000015
(80000015, 60000015, 1),
(80000015, 60000001, 1);

-- 十、评价表 review 15 条插入语句
INSERT INTO review (user_id, store_id, product_id, rating, comment, image) VALUES
                                                                               (10000001, 50000001, 60000001, 5, '鸡腿汉堡味道很赞！',    'review1.jpg'),
                                                                               (10000002, 50000002, 60000002, 4, '蛋糕很新鲜，甜度适中。', NULL),
                                                                               (10000003, 50000003, 60000003, 5, '沙拉很健康，配送迅速。', 'review3.jpg'),
                                                                               (10000004, 50000004, 60000004, 4, '拉面面条劲道，非常好吃。', NULL),
                                                                               (10000005, 50000005, 60000005, 5, '羊肉串烤得恰到好处。',  'review5.jpg'),
                                                                               (10000006, 50000006, 60000006, 5, '芝士汉堡浓郁，推荐！',   NULL),
                                                                               (10000007, 50000007, 60000007, 4, '冰沙果味十足，很清爽。', NULL),
                                                                               (10000008, 50000008, 60000008, 5, '小龙虾非常鲜美，回味无穷。','review8.jpg'),
                                                                               (10000009, 50000009, 60000009, 4, '咖啡香醇，杯型漂亮。',   NULL),
                                                                               (10000010, 50000010, 60000010, 5, '披萨热乎好吃，推荐辣味。', 'review10.jpg'),
                                                                               (10000011, 50000011, 60000011, 4, '盖饭分量足，味道不错。', NULL),
                                                                               (10000012, 50000012, 60000012, 5, '奶茶珍珠Q弹，味道超好。', 'review12.jpg'),
                                                                               (10000013, 50000013, 60000013, 5, '寿司新鲜，鱼肉入口即化。',NULL),
                                                                               (10000014, 50000014, 60000014, 5, '小面麻辣过瘾，非常好吃。', 'review14.jpg'),
                                                                               (10000015, 50000015, 60000015, 4, '巧克力蛋糕浓郁但略甜。', NULL);

-- 十一、购物车表 cart 15 条插入语句
INSERT INTO cart (user_id, product_id, quantity) VALUES
                                                     (10000001, 60000001, 1),
                                                     (10000002, 60000002, 2),
                                                     (10000003, 60000003, 1),
                                                     (10000004, 60000004, 1),
                                                     (10000005, 60000005, 3),
                                                     (10000006, 60000006, 1),
                                                     (10000007, 60000007, 2),
                                                     (10000008, 60000008, 1),
                                                     (10000009, 60000009, 1),
                                                     (10000010, 60000010, 2),
                                                     (10000011, 60000011, 1),
                                                     (10000012, 60000012, 3),
                                                     (10000013, 60000013, 1),
                                                     (10000014, 60000014, 1),
                                                     (10000015, 60000015, 2);

-- 十二、优惠券表 coupon 15 条插入语句
INSERT INTO coupon (user_id, store_id, type, discount, expiration_date, is_used, full_amount, reduce_amount) VALUES
                                                                                                                 (10000001, 50000001, 1, 0.90, '2025-06-30 23:59:59', FALSE,  50.00, 5.00),
                                                                                                                 (10000002, 50000002, 2, 0.80, '2025-07-15 23:59:59', FALSE, 100.00, 20.00),
                                                                                                                 (10000003, 50000003, 1, 0.85, '2025-05-31 23:59:59', TRUE,   30.00, 3.00),
                                                                                                                 (10000004, 50000004, 2, 0.75, '2025-06-20 23:59:59', FALSE, 120.00, 30.00),
                                                                                                                 (10000005, 50000005, 1, 0.88, '2025-07-01 23:59:59', FALSE,  60.00, 7.00),
                                                                                                                 (10000006, 50000006, 2, 0.70, '2025-06-10 23:59:59', TRUE,    80.00, 20.00),
                                                                                                                 (10000007, 50000007, 1, 0.92, '2025-06-25 23:59:59', FALSE,  40.00, 3.00),
                                                                                                                 (10000008, 50000008, 2, 0.85, '2025-06-05 23:59:59', FALSE,  90.00, 15.00),
                                                                                                                 (10000009, 50000009, 1, 0.90, '2025-06-15 23:59:59', FALSE,  55.00, 5.00),
                                                                                                                 (10000010, 50000010, 2, 0.80, '2025-07-10 23:59:59', FALSE, 110.00, 25.00),
                                                                                                                 (10000011, 50000011, 1, 0.88, '2025-06-18 23:59:59', FALSE,  70.00, 8.00),
                                                                                                                 (10000012, 50000012, 2, 0.75, '2025-06-08 23:59:59', TRUE,    95.00, 20.00),
                                                                                                                 (10000013, 50000013, 1, 0.85, '2025-06-12 23:59:59', FALSE,  65.00, 10.00),
                                                                                                                 (10000014, 50000014, 2, 0.80, '2025-07-05 23:59:59', FALSE, 100.00, 20.00),
                                                                                                                 (10000015, 50000015, 1, 0.90, '2025-06-28 23:59:59', FALSE,  50.00, 5.00);

-- 十三、消息表 message 15 条插入语句
INSERT INTO message (content, sender_id, receiver_id, sender_role, receiver_role) VALUES
                                                                                      ('您好，请问今天有优惠活动吗？',    10000001, 20000001, 'user',      'merchant'),
                                                                                      ('您的订单已确认，正在备餐。',      20000001, 10000001, 'merchant',  'user'),
                                                                                      ('骑手已出发，请您耐心等待。',      30000001, 10000001, 'rider',     'user'),
                                                                                      ('请问蛋糕可以准时送到吗？',      10000002, 20000002, 'user',      'merchant'),
                                                                                      ('已打包，请稍候，预计20分钟送达。',20000002, 10000002, 'merchant',  'user'),
                                                                                      ('收到，谢谢！',                  10000002, 30000002, 'user',      'rider'),
                                                                                      ('订单已经完成，谢谢惠顾！',      20000003, 10000003, 'merchant',  'user'),
                                                                                      ('好的，下次再来',                10000003, 20000003, 'user',      'merchant'),
                                                                                      ('骑手正在赶往商家，请耐心等候。', 30000003, 20000003, 'rider',     'merchant'),
                                                                                      ('麻烦快一点，时间比较赶。',      10000004, 30000004, 'user',      'rider'),
                                                                                      ('正在加急处理，预计15分钟送达。', 30000004, 10000004, 'rider',     'user'),
                                                                                      ('请问牛肉拉面有加汤选项吗？',    10000005, 20000004, 'user',      'merchant'),
                                                                                      ('可以加汤，也可指定辣度。',      20000004, 10000005, 'merchant',  'user'),
                                                                                      ('骑手已取餐，请耐心等候。',      30000005, 10000005, 'rider',     'user'),
                                                                                      ('活动优惠已为您发送，请查收。',  20000005, 10000006, 'merchant',  'user');

-- 十四、收藏夹表 favorite 15 条插入语句
INSERT INTO favorite (user_id, product_id, store_id) VALUES
                                                         (10000001, 60000001, NULL),
                                                         (10000002, NULL,       50000002),
                                                         (10000003, 60000003, NULL),
                                                         (10000004, NULL,       50000004),
                                                         (10000005, 60000005, NULL),
                                                         (10000006, NULL,       50000006),
                                                         (10000007, 60000007, NULL),
                                                         (10000008, NULL,       50000008),
                                                         (10000009, 60000009, NULL),
                                                         (10000010, NULL,       50000010),
                                                         (10000011, 60000011, NULL),
                                                         (10000012, NULL,       50000012),
                                                         (10000013, 60000013, NULL),
                                                         (10000014, NULL,       50000014),
                                                         (10000015, 60000015, NULL);
