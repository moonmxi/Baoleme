package org.demo.baoleme.service;

import org.demo.baoleme.pojo.Rider;

/**
 * 骑手业务接口
 * 说明：此接口不涉及任何认证上下文，完全以 riderId 和 rider 对象为参数处理业务。
 */
public interface RiderService {

    /**
     * 注册骑手（不传入 ID，由框架生成）
     * @param rider 新注册信息
     * @return Rider 成功注册的 Rider 对象，失败返回 null
     */
    Rider register(Rider rider);

    /**
     * 骑手登录验证
     * @param phone 手机号
     * @param password 密码明文
     * @return 验证成功返回 Rider，失败返回 null
     */
    Rider login(String phone, String password);

    /**
     * 获取骑手信息
     * @param riderId 骑手主键 ID
     * @return Rider 或 null
     */
    Rider getInfo(Long riderId);

    /**
     * 更新骑手资料（需包含 ID）
     * @param rider 要更新的 Rider 对象
     * @return true 表示成功，false 表示失败
     */
    boolean updateInfo(Rider rider);

    /**
     * 注销（逻辑删除或物理删除）
     * @param riderId 主键 ID
     * @return true 表示成功，false 表示失败
     */
    boolean delete(Long riderId);

    boolean randomSendOrder(Long riderId);

    boolean updateRiderOrderStatusAfterOrderGrab(Long riderId);

    /**
     * 专门把某个骑手的 avatar 字段更新成新的相对路径
     * @param riderId    要更新的Rider主键
     * @param avatarPath 形如 "rider/avatar/2025-06-04/abcdef.jpg" 的相对路径
     * @return true 表示更新成功，false 表示失败
     */
    boolean updateAvatar(Long riderId, String avatarPath);
}