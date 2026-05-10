package ru.trader.kairos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.trader.kairos.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
