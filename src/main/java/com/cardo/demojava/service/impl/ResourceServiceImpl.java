package com.cardo.demojava.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cardo.demojava.entity.Resource;
import com.cardo.demojava.dto.ResourceDto;
import com.cardo.demojava.mapper.ResourceMapper;
import com.cardo.demojava.service.ResourceService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cardo.demojava.entity.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import java.io.IOException;
import static com.cardo.demojava.contant.Code.*;   

@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

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
                String fileUrl = "https://" + BUCKET_NAME + "." + ENDPOINT + "/" + objectName;
                
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
}
