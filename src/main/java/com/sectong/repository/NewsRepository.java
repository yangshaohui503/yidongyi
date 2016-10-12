package com.sectong.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import com.sectong.domain.News;

/**
 * 新闻表Repository定义
 * 
 * @author jiekechoo
 *
 */
@RestResource(exported = false)

public interface NewsRepository extends PagingAndSortingRepository<News, Long> {

	Page<News> findByIdGreaterThan(Long startid, Pageable p);

}
