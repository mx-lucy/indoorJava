package cf.indoor.test.dao;


import org.springframework.data.jpa.repository.JpaRepository;

import cf.indoor.test.pojo.Category;

public interface CategoryDAO extends JpaRepository<Category, Integer> {

}

