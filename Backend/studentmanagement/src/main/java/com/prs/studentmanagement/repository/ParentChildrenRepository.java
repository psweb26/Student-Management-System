package com.prs.studentmanagement.repository;

import com.prs.studentmanagement.model.ParentChildren;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParentChildrenRepository extends JpaRepository<ParentChildren, Integer> {
    List<ParentChildren> findByParentId(String parentId);
}