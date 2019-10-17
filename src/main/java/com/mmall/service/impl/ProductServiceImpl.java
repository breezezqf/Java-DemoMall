package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtils;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vo.ProductDetailVo;
import vo.ProductListVo;

import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {


    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ProductMapper productMapper;

    public ServerResponse saveOrUpdateProduct(Product product){
        if (product != null){//保存
            if (StringUtils.isNotBlank(product.getSubImages())){
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length > 0){
                    product.setMainImage(subImageArray[0]);
                }
            }
            if (product.getId() != null) {//更新
                int rowCount = productMapper.updateByPrimaryKey(product);
                if (rowCount > 0){
                    return ServerResponse.createBySuccess("更新产品成功");
                }
                return ServerResponse.createBySuccess("更新产品失败");
            }else {
                int rowCount = productMapper.insert(product);
                if (rowCount > 0){
                    return ServerResponse.createBySuccess("新增产品成功");
                }
                return ServerResponse.createBySuccess("新增产品失败");
            }
        }
        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }

    public ServerResponse<String> setSaleStatus(Integer productId, Integer status){
        if (productId == null || status == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product =new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0){
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }

    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if (productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        ProductDetailVo po = assembleProductDateiVo(product);
        return ServerResponse.createBySuccess(po);
    }

    //封装业务所需要的VO
    private ProductDetailVo assembleProductDateiVo(Product product){
        ProductDetailVo po = new ProductDetailVo();
        po.setId(product.getId());
        po.setName(product.getName());
        po.setSubImages(product.getSubImages());
        po.setSubtitle(product.getSubtitle());
        po.setPrice(product.getPrice());
        po.setMainImage(product.getMainImage());
        po.setCategoryId(product.getCategoryId());
        po.setDetail(product.getDetail());
        po.setStatus(product.getStatus());
        po.setStock(product.getStock());

        //imageHost-->从配置文件读取
        po.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null){
            po.setParentCategoryId(String.valueOf(0));//默认跟节点
        }else {
            po.setParentCategoryId(String.valueOf(category.getParentId()));
        }
        po.setCreateTime(DateTimeUtils.dateToStr(product.getCreateTime()));
        po.setUpdateTime(DateTimeUtils.dateToStr(product.getUpdateTime()));
        return po;
    }

    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize){
        //startPage--start 记录开始
        PageHelper.startPage(pageNum, pageSize);
        //填充自己的SQL查询逻辑
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        //pageHelper收尾
        PageInfo pageResult =new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    //ProductList组装VO
    public ProductListVo assembleProductListVo(Product product){
        ProductListVo po = new ProductListVo();
        po.setId(product.getId());
        po.setName(product.getName());
        po.setCategoryId(product.getCategoryId());
        po.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        po.setMainImage(product.getMainImage());
        po.setPrice(product.getPrice());
        po.setSubtitle(product.getSubtitle());
        po.setStatus(product.getStatus());
        return po;
    }

    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(productName)){
            productName = new StringBuffer().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult =new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }
}
