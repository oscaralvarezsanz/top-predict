package com.topleague.predict.infrastructure.out.db.group;

import com.topleague.predict.application.port.out.group.GroupGetByIdRepository;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.infrastructure.out.db.group.mapper.GroupEntityConverter;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MySqlGroupGetByIdRepository implements GroupGetByIdRepository {

    private final JpaGroupRepository jpaRepository;
    private final GroupEntityConverter entityConverter;

    public MySqlGroupGetByIdRepository(JpaGroupRepository jpaRepository,
                                       GroupEntityConverter entityConverter) {
        this.jpaRepository = jpaRepository;
        this.entityConverter = entityConverter;
    }

    @Override
    public Optional<Group> getGroupById(Integer id) {
        return jpaRepository.findById(id)
                .map(entityConverter::toDomain);
    }
}
