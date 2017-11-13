package io.union.admin.service;

import io.union.admin.dao.AdsArticleRepository;
import io.union.admin.entity.AdsArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shell Li on 2017/8/28.
 */
@Service
public class AdsArticleService {

    @Autowired
    private AdsArticleRepository adsArticleRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsArticle findOne(long id) {
        return adsArticleRepository.findOne(id);
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<AdsArticle> findAll(int page, int size) {
        Page<AdsArticle> list;
        if (size == 0) size = 20;
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "atop"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "asort"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "id"));
        Pageable pageable = new PageRequest(page, size, new Sort(orders));
        list = adsArticleRepository.findAll(pageable);
        return list;
    }


    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<AdsArticle> findAllByTitle(int page, int size, String title) {
        Page<AdsArticle> list;
        if (size == 0) size = 20;
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "atop"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "asort"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "id"));
        Pageable pageable = new PageRequest(page, size, new Sort(orders));
        list = adsArticleRepository.findAllByTitleContaining(title, pageable);
        return list;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AdsArticle saveOne(AdsArticle adsArticle) {
        return adsArticleRepository.save(adsArticle);
    }
}
