package com.cardo.demojava.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cardo.demojava.entity.Resource;
import com.cardo.demojava.dto.ResourceDto;
import com.cardo.demojava.mapper.ResourceMapper;
import com.cardo.demojava.mapper.UserMapper;
import com.cardo.demojava.service.ResourceService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.User;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import java.io.IOException;
import static com.cardo.demojava.contant.Code.*;   
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

    private static final Logger log = LoggerFactory.getLogger(ResourceServiceImpl.class);

    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private UserMapper userMapper;

    @Value("${ENDPOINT}")
    private String ENDPOINT;
    @Value("${ACCESS_KEY_ID}")
    private String ACCESS_KEY_ID;
    @Value("${ACCESS_KEY_SECRET}")
    private String ACCESS_KEY_SECRET;
    @Value("${BUCKET_NAME}")
    private String BUCKET_NAME;

    @Override
    public Response<List<ResourceDto>> getAllResources(String taskId) {
        List<Resource> resources = resourceMapper.selectList(new LambdaQueryWrapper<Resource>().eq(Resource::getTaskId, taskId));
        List<ResourceDto> resourceDtos = resources.stream()
                .map(resource -> {
                    ResourceDto dto = new ResourceDto();
                    BeanUtils.copyProperties(resource, dto);
                    return dto;
                })
                .collect(Collectors.toList());
        for (ResourceDto resourceDto : resourceDtos) {
            User user = userMapper.selectById(resourceDto.getUserId());
            resourceDto.setUserName(user.getName());
        }
        return Response.ok(resourceDtos);
    }

    @Override
    public Response<String> addResource(String taskId, String userId, MultipartFile file) {
        try {
            // 获取文件信息
            String originalFilename = file.getOriginalFilename();
            String fileType = file.getContentType();
            
            // 生成OSS中的对象名称（使用时间戳避免重名）
            String objectName = System.currentTimeMillis() + "_" + originalFilename;
            
            // 创建OSS客户端
            OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            
            try {
                // 上传文件到OSS
                ossClient.putObject(BUCKET_NAME, objectName, file.getInputStream());
                
                // 构建文件访问URL
                String fileUrl = "https://zhangshuaibao.top/" + objectName;
                
                // 保存资源信息到数据库
                Resource resource = new Resource();
                resource.setTaskId(taskId);
                resource.setUserId(userId);
                resource.setResourceUrl(fileUrl);
                resource.setResourceType(fileType);
                resourceMapper.insert(resource);
                return Response.ok(fileUrl);
            } finally {
                ossClient.shutdown();
            }
        } catch (IOException e) {
            return Response.error(FILE_UPLOAD_FAIL);
        }
    }

    @Override
    public Response<String> deleteResource(String id) {
        // 1. 先查询资源信息
        Resource resource = this.getById(id);
        if (resource == null) {
            return Response.error(RESOURCE_NOT_FOUND);
        }
        try {
            // 2. 删除OSS中的文件
            String ossUrl = resource.getResourceUrl(); // 假设资源URL存储在resourceUrl字段
            // 创建OSS客户端
            OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            try {
                // 从URL中提取objectName
                String objectName = ossUrl.substring(ossUrl.lastIndexOf("/") + 1);
                // 删除OSS对象
                ossClient.deleteObject(BUCKET_NAME, objectName);
            } finally {
                ossClient.shutdown();
            }
            // 3. 删除数据库记录
            this.removeById(id);
            return Response.ok("删除成功");
        } catch (Exception e) {
            log.error("删除资源失败", e);
            return Response.error(SYSTEM_ERROR);
        }
    }
}
