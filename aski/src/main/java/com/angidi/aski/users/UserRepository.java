package com.angidi.aski.users;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Query("""
			SELECT u from users u where enabled = true 
			AND case when :#{#user.id} is not null then u.id = :#{#user.id} else true end
			AND case when :#{#user.name} is not null then u.name = :#{#user.name} else true end
			AND case when :#{#user.email} is not null then u.email = :#{#user.email} else true end
			AND case when :#{#user.roleId} is not null then u.roleId = :#{#user.roleId} else true end
			AND case when :#{#user.country} is not null then u.country = :#{#user.country} else true end
			AND case when :#{#user.city} is not null then u.city = :#{#user.city} else true end
			""")
	List<User> filter(User user);

}
