package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.entity.Condition;

@Repository
public interface ConditionRepository extends JpaRepository<Condition, Long> {}