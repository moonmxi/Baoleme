package org.demo.baoleme.controller;

import org.demo.baoleme.service.MerchantService;
import org.demo.baoleme.service.RiderService;
import org.demo.baoleme.service.UserService;
import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.common.UserHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 处理所有图片上传的接口
 */
@RestController
@RequestMapping("/image")
public class ImageController {

    // 从 application.yml 中读取的文件存储根路径和外部访问路径
    @Value("${file.storage.upload-dir}")
    private String uploadDir;

    @Value("${file.storage.base-url}")
    private String baseUrl;

    private final RiderService riderService;
    private final MerchantService merchantService;
    private final UserService userService;

    public ImageController(RiderService riderService, MerchantService merchantService, UserService userService) {
        this.riderService = riderService;
        this.merchantService = merchantService;
        this.userService = userService;
    }

    /**
     * 【骑手头像上传接口】
     * 上传骑手头像图片并将其存储到本地磁盘，返回图片的相对路径。
     */
    @PostMapping("/upload-rider-avatar")
    public CommonResponse uploadRiderAvatar(@RequestParam("file") MultipartFile file) {
        // 1. 校验文件是否为空
        if (file.isEmpty()) {
            System.out.println("上传文件为空！");
            return ResponseBuilder.fail("上传文件不能为空");
        }

        try {
            // 2. 获取当前日期，并格式化路径
            String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String subFolder = "rider/avatar/" + dateFolder;
            Path folderPath = Paths.get(uploadDir, subFolder);

            // 3. 打印调试输出：输出 uploadDir 和 folderPath
            System.out.println("上传目录: " + uploadDir);
            System.out.println("文件保存路径: " + folderPath);

            // 4. 如果目录不存在，则创建目录
            if (!Files.exists(folderPath)) {
                System.out.println("目录不存在，创建目录...");
                Files.createDirectories(folderPath);
            }

            // 5. 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String ext = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0) {
                ext = originalFilename.substring(dotIndex);
            }

            // 6. 生成唯一的文件名，避免文件名冲突
            String fileName = UUID.randomUUID().toString() + ext;
            Path destination = folderPath.resolve(fileName);

            // 7. 打印文件名
            System.out.println("文件保存名: " + fileName);
            System.out.println("文件保存路径: " + destination);

            // 8. 将文件写入磁盘
            file.transferTo(destination.toFile());
            System.out.println("文件成功保存至: " + destination);

            // 9. 构造相对路径（存入数据库），以及可对外访问的 URL
            String relativePath = subFolder + "/" + fileName;
            String fileUrl = baseUrl + relativePath;

            // 10. 调用 RiderService 更新骑手的头像路径到数据库
            Long riderId = UserHolder.getId();
            boolean success = riderService.updateAvatar(riderId, relativePath);
            if (!success) {
                System.out.println("更新骑手数据库失败！");
                return ResponseBuilder.fail("骑手头像更新失败");
            }

            // 11. 返回文件的完整 URL（前端可以直接用于展示）
            System.out.println("返回骑手头像 URL: " + fileUrl);
            return ResponseBuilder.ok(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("骑手头像上传失败：" + e.getMessage());
            return ResponseBuilder.fail("骑手头像上传失败：" + e.getMessage());
        }
    }

    /**
     * 【商家头像上传接口】
     * 上传商家头像图片并将其存储到本地磁盘，返回图片的相对路径。
     */
    @PostMapping("/upload-merchant-avatar")
    public CommonResponse uploadMerchantAvatar(@RequestParam("file") MultipartFile file) {
        // 1. 校验文件是否为空
        if (file.isEmpty()) {
            System.out.println("上传文件为空！");
            return ResponseBuilder.fail("上传文件不能为空");
        }

        try {
            // 2. 获取当前日期，并格式化路径
            String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String subFolder = "merchant/avatar/" + dateFolder;
            Path folderPath = Paths.get(uploadDir, subFolder);

            // 3. 打印调试输出：输出 uploadDir 和 folderPath
            System.out.println("上传目录: " + uploadDir);
            System.out.println("文件保存路径: " + folderPath);

            // 4. 如果目录不存在，则创建目录
            if (!Files.exists(folderPath)) {
                System.out.println("目录不存在，创建目录...");
                Files.createDirectories(folderPath);
            }

            // 5. 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String ext = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0) {
                ext = originalFilename.substring(dotIndex);
            }

            // 6. 生成唯一的文件名，避免文件名冲突
            String fileName = UUID.randomUUID().toString() + ext;
            Path destination = folderPath.resolve(fileName);

            // 7. 打印文件名
            System.out.println("文件保存名: " + fileName);
            System.out.println("文件保存路径: " + destination);

            // 8. 将文件写入磁盘
            file.transferTo(destination.toFile());
            System.out.println("文件成功保存至: " + destination);

            // 9. 构造相对路径（存入数据库），以及可对外访问的 URL
            String relativePath = subFolder + "/" + fileName;
            String fileUrl = baseUrl + relativePath;

            // 10. 调用 MerchantService 更新商家的头像路径到数据库
            Long merchantId = UserHolder.getId();
            boolean success = merchantService.updateAvatar(merchantId, relativePath);
            if (!success) {
                System.out.println("更新商家数据库失败！");
                return ResponseBuilder.fail("商家头像更新失败");
            }

            // 11. 返回文件的完整 URL（前端可以直接用于展示）
            System.out.println("返回商家头像 URL: " + fileUrl);
            return ResponseBuilder.ok(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("商家头像上传失败：" + e.getMessage());
            return ResponseBuilder.fail("商家头像上传失败：" + e.getMessage());
        }
    }

    /**
     * 【普通用户头像上传接口】
     * 上传普通用户头像图片并将其存储到本地磁盘，返回图片的相对路径。
     */
    @PostMapping("/upload-user-avatar")
    public CommonResponse uploadUserAvatar(@RequestParam("file") MultipartFile file) {
        // 1. 校验文件是否为空
        if (file.isEmpty()) {
            System.out.println("上传文件为空！");
            return ResponseBuilder.fail("上传文件不能为空");
        }

        try {
            // 2. 获取当前日期，并格式化路径
            String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String subFolder = "user/avatar/" + dateFolder;
            Path folderPath = Paths.get(uploadDir, subFolder);

            // 3. 打印调试输出：输出 uploadDir 和 folderPath
            System.out.println("上传目录: " + uploadDir);
            System.out.println("文件保存路径: " + folderPath);

            // 4. 如果目录不存在，则创建目录
            if (!Files.exists(folderPath)) {
                System.out.println("目录不存在，创建目录...");
                Files.createDirectories(folderPath);
            }

            // 5. 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String ext = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0) {
                ext = originalFilename.substring(dotIndex);
            }

            // 6. 生成唯一的文件名，避免文件名冲突
            String fileName = UUID.randomUUID().toString() + ext;
            Path destination = folderPath.resolve(fileName);

            // 7. 打印文件名
            System.out.println("文件保存名: " + fileName);
            System.out.println("文件保存路径: " + destination);

            // 8. 将文件写入磁盘
            file.transferTo(destination.toFile());
            System.out.println("文件成功保存至: " + destination);

            // 9. 构造相对路径（存入数据库），以及可对外访问的 URL
            String relativePath = subFolder + "/" + fileName;
            String fileUrl = baseUrl + relativePath;

            // 10. 调用 UserService 更新普通用户的头像路径到数据库
            Long userId = UserHolder.getId();
            boolean success = userService.updateAvatar(userId, relativePath);
            if (!success) {
                System.out.println("更新用户数据库失败！");
                return ResponseBuilder.fail("用户头像更新失败");
            }

            // 11. 返回文件的完整 URL（前端可以直接用于展示）
            System.out.println("返回用户头像 URL: " + fileUrl);
            return ResponseBuilder.ok(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("用户头像上传失败：" + e.getMessage());
            return ResponseBuilder.fail("用户头像上传失败：" + e.getMessage());
        }
    }
}
