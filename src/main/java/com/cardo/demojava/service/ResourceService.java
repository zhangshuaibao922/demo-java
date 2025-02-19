package com.cardo.demojava.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cardo.demojava.entity.Resource;
import com.cardo.demojava.dto.ResourceDto;
import com.cardo.demojava.entity.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceService extends IService<Resource> {
    Response<List<ResourceDto>> getAllResources(String taskId);
    Response<String> addResource(String taskId, String userId, MultipartFile file);
    Response<String> deleteResource(String id);
}
