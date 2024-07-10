package com.example.Buoi2.controller;

import com.example.Buoi2.entity.Product;
import com.example.Buoi2.services.CategoryService;
import com.example.Buoi2.services.ProductService;
import com.example.Buoi2.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RatingService ratingService;

    @GetMapping
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "/products/products-list";
    }

    @GetMapping("/sorted")
    public String showSortedProductList(@RequestParam(required = false) String sortField,
                                        @RequestParam(required = false) String sortDir,
                                        Model model) {
        List<Product> sortedProducts = productService.getAllProducts(sortField, sortDir);
        model.addAttribute("products", sortedProducts);
        return "/products/products-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "/products/add-product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult result, @RequestParam("image") MultipartFile imageFile) {
        if (result.hasErrors()) {
            return "/products/add-product";
        }
        if (!imageFile.isEmpty()) {
            try {
                String imageName = saveImageStatic(imageFile);
                product.setImageData("/images/" +imageName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        productService.addProduct(product);
        return "redirect:/products";
    }

    private String saveImageStatic(MultipartFile image) throws IOException {
        String fileName = UUID.randomUUID()+ "." + org.springframework.util.StringUtils.getFilenameExtension(image.getOriginalFilename());
        java.nio.file.Path path = java.nio.file.Paths.get(new org.springframework.core.io.ClassPathResource("static/images").getFile().getAbsolutePath() + java.io.File.separator + fileName);
        java.nio.file.Files.copy(image.getInputStream(), path);
        return fileName;
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "/products/update-product";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product,
                                BindingResult result, @RequestParam("image") MultipartFile imageFile) {
        if (result.hasErrors()) {
            product.setId(id);
            return "/products/update-product";
        }

        Product existingProduct = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

        existingProduct.setName(product.getName());
        existingProduct.setOriginalPrice(product.getOriginalPrice());
        existingProduct.setDiscountedPrice(product.getDiscountedPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setQuantity(product.getQuantity());

        if (!imageFile.isEmpty()) {
            try {
                String imageName = saveImageStatic(imageFile);
                existingProduct.setImageData("/images/" + imageName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        productService.updateProduct(existingProduct);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }

    @GetMapping("/detail/{id}")
    public String showProductDetails(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("ratings", ratingService.getRatingsByProductId(id));
        return "/products/product-detail";
    }
    @GetMapping("/search")
    public String searchProducts(@RequestParam("name") String name, Model model) {
        List<Product> products = productService.searchProductsByName(name);
        model.addAttribute("products", products);
        return "/products/products-list";
    }
}
