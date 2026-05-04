package com.sit.qb.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sit.qb.dtos.TopItemDto;
import com.sit.qb.entity.MenuItem;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long>, JpaSpecificationExecutor<MenuItem> {

	@Query("SELECT m FROM MenuItem m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<MenuItem> searchByKeyword(@Param("keyword") String keyword);

	@Query("SELECT NEW com.sit.qb.dtos.TopItemDto(m.name, COUNT(oi)) "
			+ "FROM OrderItem oi JOIN oi.menuItem m GROUP BY m.id ORDER BY COUNT(oi) DESC")
	List<TopItemDto> findTop3OrderedItems(Pageable pageable);

}
