package com.duong.mycase41.repository;

import com.duong.mycase41.model.Classes;
import com.duong.mycase41.model.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITeacherRepository extends PagingAndSortingRepository<Teacher, Long> {
    Page<Teacher> findAllByFullNameContaining(String fullName, Pageable pageable);
}
