// src/main/java/org/demo/baoleme/controller/ImageController.java
package org.demo.baoleme.controller;

import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.service.RiderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 统一放置所有“图片上传/访问”相关接口。
 * 本例实现骑手头像上传/更改： POST /image/uploadRiderAvatar
 * 后续可在此 Controller 中增加： uploadMerchantAvatar、uploadProductImage 等
 */
@RestController
@RequestMapping("/image")
public class ImageController {

    // 从 application.yml 里读取的本地文件根目录，已由 WebMvcConfig 映射到 /images/**
    @Value("${file.storage.upload-dir}")
    private String uploadDir;

    // 从 application.yml 里读取的外部访问前缀，比如 /images/
    @Value("${file.storage.base-url}")
    private String baseUrl;

    private final RiderService riderService;

    public ImageController(RiderService riderService) {
        this.riderService = riderService;
    }

    /**
     * 【骑手头像上传/更改】
     * 请求方式：POST /image/uploadRiderAvatar
     * Content-Type: multipart/form-data
     * Body: form-data, key="file", value=选择的图片文件
     *
     * 请求体里不传其他字段，当前登录的骑手ID 从 UserHolder.getId() 拿到
     * - 将图片保存到本地 disk: upload/rider/avatar/yyyy-MM-dd/{UUID}.{ext}
     * - 拼一个相对路径(不带域名)，如 "rider/avatar/2025-06-04/abcdef1234.jpg"
     * - 调用 riderService.updateAvatar(riderId, relativePath)，将 avatar 列写入数据库
     * - 返回值：CommonResponse.ok(fullUrl)，fullUrl = baseUrl + relativePath (前端可直接 <img src="fullUrl"> 预览)
     */
    @PostMapping("/uploadRiderAvatar")
    public CommonResponse uploadRiderAvatar(@RequestParam("file") MultipartFile file) {
        // 1. 前置校验
        if (file == null || file.isEmpty()) {
            return ResponseBuilder.fail("上传文件不能为空");
        }

        // 2. 从 UserHolder 拿到当前骑手 ID（注意：保证用户在发请求之前已登录，UserHolder.getId() 不会为 null）
        Long riderId = UserHolder.getId();
        if (riderId == null) {
            return ResponseBuilder.fail("当前身份无效，请先登录");
        }

        try {
            // 3. 构造子目录：rider/avatar/yyyy-MM-dd/
            String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String subFolder = "rider/avatar/" + dateFolder;
            Path folderPath = Paths.get(uploadDir, subFolder);
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            // 4. 从原始文件名提取扩展名
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String ext = "";
            int dotIdx = originalFilename.lastIndexOf('.');
            if (dotIdx >= 0) {
                ext = originalFilename.substring(dotIdx);
            }

            // 5. 生成唯一文件名：UUID + 扩展名
            String uuidFilename = UUID.randomUUID().toString().replace("-", "") + ext;
            Path destination = folderPath.resolve(uuidFilename);

            // 6. 写文件到本地：
            file.transferTo(destination.toFile());

            // 7. 构造相对路径（存到 DB），以及可对外访问的完整 URL
            //    - relativePath = "rider/avatar/2025-06-04/abcdef1234.jpg"
            String relativePath = subFolder + "/" + uuidFilename;
            //    - fullUrl = baseUrl + relativePath, 例如 "/images/rider/avatar/2025-06-04/abcdef1234.jpg"
            String fullUrl = baseUrl + relativePath;

            // 8. 调用 Service 层：把 rider 表的 avatar 列更新成 relativePath
            boolean updated = riderService.updateAvatar(riderId, relativePath);
            if (!updated) {
                // 如果更新 DB 失败，删掉刚写入的本地文件，避免“脏文件”累积
                try {
                    Files.deleteIfExists(destination);
                } catch (IOException ex) {
                    // 忽略删除失败
                }
                return ResponseBuilder.fail("头像保存失败，请稍后重试");
            }

            // 9. 返回前端完整 URL，前端直接用 <img :src="fullUrl" /> 预览
            return ResponseBuilder.ok(fullUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseBuilder.fail("文件上传出错：" + e.getMessage());
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // 如果后续要做“商家头像上传”或“商品图片上传”，只能按照相同思路，
    // 在这个 Controller 里添加方法。例如：
    //
    // @PostMapping("/uploadMerchantAvatar")
    // public CommonResponse uploadMerchantAvatar(@RequestParam("file") MultipartFile file) { … }
    //
    // @PostMapping("/uploadProductImage")
    // public CommonResponse uploadProductImage(@RequestParam("file") MultipartFile file,
    //                                          @RequestParam("productId") Long productId) { … }
    //
    // 这样所有“/image/…” 路径都放在同一个类里，统一管理图片相关的逻辑。
    // ────────────────────────────────────────────────────────────────────────────
}
