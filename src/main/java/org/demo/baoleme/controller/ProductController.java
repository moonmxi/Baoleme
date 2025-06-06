package org.demo.baoleme.controller;

import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.dto.request.product.*;
import org.demo.baoleme.dto.request.user.UserGetProductInfoRequest;
import org.demo.baoleme.dto.response.product.*;
import org.demo.baoleme.dto.response.user.UserGetProductInfoResponse;
import org.demo.baoleme.dto.response.user.UserGetProductResponse;
import org.demo.baoleme.mapper.StoreMapper;
import org.demo.baoleme.pojo.Page;
import org.demo.baoleme.pojo.Product;
import org.demo.baoleme.pojo.Review;
import org.demo.baoleme.service.ProductService;
import org.demo.baoleme.service.ReviewService;
import org.demo.baoleme.service.SalesStatsService;
import org.demo.baoleme.service.StoreService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final StoreService storeService;

    @Autowired
    private ReviewService  reviewService;
    @Autowired
    private SalesStatsService salesStatsService;

    @Autowired
    public ProductController(
            ProductService productService,
            StoreService storeService
    ) {
        this.productService = productService;
        this.storeService = storeService;
    }

    @PostMapping("/create")
    public CommonResponse createProduct(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody ProductCreateRequest request
    ) {
        System.out.println("=== Create Product Request ===");
        System.out.println("Request Body: " + request);

        // Step1: 创建 Product 对象并拷贝属性
        Product product = new Product();
        BeanUtils.copyProperties(request, product);

        if (!validateStoreOwnerShip(product)) return ResponseBuilder.fail("商品创建失败，商家没有权限");

        // Step2: 调用 Service 创建商品
        Product createdProduct = productService.createProduct(product);

        // Step3: 处理创建结果
        if (createdProduct == null) {
            CommonResponse errorResponse = ResponseBuilder.fail("商品创建失败，请检查输入参数");
            System.out.println("Response Body: " + errorResponse);
            return errorResponse;
        }

        // Step4: 构建响应体
        ProductCreateResponse response = new ProductCreateResponse();
        response.setProductId(createdProduct.getId());
        System.out.println("Response Body: " + response);
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/view")
    public CommonResponse getProductById(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody ProductViewRequest request
    ) {
        System.out.println("=== View Product Request ===");
        System.out.println("Request Body: " + request);

        Long productId = request.getProductId();

        // Step1: 查询商品详情
        Product product = productService.getProductById(productId);

        // Step2: 验证查询结果
        if (product == null) {
            CommonResponse errorResponse = ResponseBuilder.fail("商品不存在");
            System.out.println("Response Body: " + errorResponse);
            return errorResponse;
        }

        if (!validateStoreOwnerShip(product)) return ResponseBuilder.fail("商品创建失败，商家没有权限");

        // Step3: 构建响应体
        ProductViewResponse response = new ProductViewResponse();
        BeanUtils.copyProperties(product, response);
        System.out.println("Response Body: " + response);
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/store-products")
    public CommonResponse getProductsByStore(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody ProductViewRequest request
    ) {
        System.out.println("=== Store Products Request ===");
        System.out.println("Request Body: " + request);

        Long storeId = request.getStoreId();
        int currentPage = request.getPage();       // 新增页码参数
        int pageSize = request.getPageSize(); // 新增分页大小参数

        if (!storeService.validateStoreOwnership(storeId, UserHolder.getId()))
            return ResponseBuilder.fail("商品创建失败，商家没有权限");

        // Step2: 调用Service获取分页数据
        Page<Product> page = productService.getProductsByStore(storeId, currentPage, pageSize);

        // Step3: 构建响应列表
        List<ProductViewResponse> responses = page.getList().stream()
                .map(product -> {
                    ProductViewResponse resp = new ProductViewResponse();
                    BeanUtils.copyProperties(product, resp);

                    // Step: 获取销量
                    resp.setVolume(salesStatsService.getProductVolume(product.getId()));
                    return resp;
                })
                .collect(Collectors.toList());

        // Step4: 封装分页响应
        ProductPageResponse pageResponse = new ProductPageResponse();
        pageResponse.setProducts(responses);
        pageResponse.setCurrentPage(page.getCurrPage());
        pageResponse.setTotalPages(page.getPageCount());
        pageResponse.setTotalItems(page.getCount());

        System.out.println("Response Body: " + pageResponse);
        return ResponseBuilder.ok(pageResponse);
    }

    @PutMapping("/update")
    public CommonResponse updateProduct(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody ProductUpdateRequest request
    ) {
        System.out.println("=== Update Product Request ===");
        System.out.println("Request Body: " + request);

        // Step1: 创建 Product 对象并设置 ID
        Long productId = request.getProductId();
        Product product = new Product();
        product.setId(productId);

        // Step2: 拷贝请求参数
        // 可能会出现属性拷贝后数据类型不匹配的情况
        BeanUtils.copyProperties(request, product);

        // Step3: 调用 Service 更新数据
        boolean success = productService.updateProduct(product);

        Product newProduct = productService.getProductById(productId);

        // Step4: 处理更新结果
        if (!success) {
            CommonResponse errorResponse = ResponseBuilder.fail("更新失败，请检查商品 ID 是否存在");
            System.out.println("Response Body: " + errorResponse);
            return errorResponse;
        }

        // Step5: 构建响应体
        ProductUpdateResponse response = new ProductUpdateResponse();
        BeanUtils.copyProperties(newProduct, response);

        System.out.println("Response Body: " + response);
        return ResponseBuilder.ok(response);
    }

    @PutMapping("/status")
    public CommonResponse updateProductStatus(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody ProductUpdateRequest request
    ) {
        System.out.println("=== Update Status Request ===");
        System.out.println("Request Body: " + request);

        Long productId = request.getProductId();
        int status = request.getStatus();

        // Step1: 执行状态更新
        boolean success = productService.updateProductStatus(productId, status);

        // Step2: 返回操作结果
        CommonResponse response = success ?
                ResponseBuilder.ok("商品状态更新成功") :
                ResponseBuilder.fail("状态更新失败");
        System.out.println("Response Body: " + response);
        return response;
    }

    @PostMapping("/delete")
    public CommonResponse deleteProduct(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody ProductDeleteRequest request
    ) {
        System.out.println("=== Delete Product Request ===");
        System.out.println("Request Body: " + request);

        // Step1: 从请求体中获取商品ID
        Long productId = request.getId();

        // Step2: 执行删除操作
        boolean success = productService.deleteProduct(productId);

        // Step3: 返回操作结果
        CommonResponse response = success ?
                ResponseBuilder.ok("商品删除成功") :
                ResponseBuilder.fail("删除失败，商品可能不存在");
        System.out.println("Response Body: " + response);
        return response;
    }
    @PostMapping("/productInfo")
    public CommonResponse getProductInfo(@RequestBody UserGetProductInfoRequest request){
        Long id = request.getId();
        Product product = productService.getProductById(id);
        UserGetProductInfoResponse response = new UserGetProductInfoResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setCategory(product.getCategory());
        response.setDescription(product.getDescription());
        response.setImage(product.getImage());
        response.setPrice(product.getPrice());
        response.setRating(product.getRating());
        response.setStock(product.getStock());
        response.setStatus(product.getStatus());
        response.setCreatedAt(product.getCreatedAt());

        List<Review> reviews = reviewService.getReviewsByProductId(id);
        response.setReviews(reviews);

        return ResponseBuilder.ok(response);
    }

    private boolean validateStoreOwnerShip(Product product) {
        Long merchantId = UserHolder.getId();
        return storeService.validateStoreOwnership(product.getStoreId(), merchantId);
    }
}