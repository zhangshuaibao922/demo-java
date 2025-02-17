package com.cardo.demojava.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cardo.demojava.service.ResourceService;
import com.cardo.demojava.entity.Response;
import java.util.List;
import com.cardo.demojava.dto.ResourceDto;

@Api(tags = "资源类接口")
@RestController
@RequestMapping("/resource")
public class ResourceController {
    @Value("${ENDPOINT}")
    private String ENDPOINT;
    @Value("${ACCESS_KEY_ID}")
    private String ACCESS_KEY_ID;
    @Value("${ACCESS_KEY_SECRET}")
    private String ACCESS_KEY_SECRET;
    @Value("${BUCKET_NAME}")
    private String BUCKET_NAME;

    @Autowired
    private ResourceService resourceService;

    @GetMapping("/all")
    public Response<List<ResourceDto>> getAllResources(@RequestParam String taskId) {
        return resourceService.getAllResources(taskId);
    }
    @PostMapping("/add/{taskId}/{userId}")
    public Response<String> addResource(@PathVariable String taskId, @PathVariable String userId, @RequestParam("file") MultipartFile file) {
        return resourceService.addResource(taskId, userId, file);
    }
}
