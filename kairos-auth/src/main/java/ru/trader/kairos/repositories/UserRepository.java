package ru.trader.kairos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.trader.kairos.entity.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
