package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 描述:
 * cms配置管理逻辑层
 *
 * @author XueGuCheng
 * @create 2020-03-01 19:22
 */
@Service
public class CmsConfigService {

    @Autowired
    CmsConfigRepository cmsConfigRepository;

    /**
     * 根据id查询配置管理信息
     * @param id cms配置id
     * @return
     */
    public CmsConfig getConfigById(String id){
        Optional<CmsConfig> optional = cmsConfigRepository.findById(id);
        if (optional.isPresent()){
            CmsConfig cmsConfig = optional.get();
            return cmsConfig;
        }
        return null;
    }

}
