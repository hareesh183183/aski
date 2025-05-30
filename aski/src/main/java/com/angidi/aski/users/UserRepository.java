package com.angidi.aski.users;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Query("""
			SELECT u from users u where enabled = true
			AND case when :id is not null then u.id = :id else true end
			AND case when :name is not null then u.name = :name else true end
			AND case when :email is not null then u.email = :email else true end
			AND case when :roleId is not null then u.roleId = :roleId else true end
			AND case when :country is not null then u.country = :country else true end
			AND case when :city is not null then u.city = :city else true end
			""")
	List<User> filter(@Param("id") Long id, @Param("name") String name, @Param("email") String email,
			@Param("roleId") Long roleId, @Param("country") String country, @Param("city") String city);

}
