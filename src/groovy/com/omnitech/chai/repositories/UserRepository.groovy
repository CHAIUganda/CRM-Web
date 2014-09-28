package com.omnitech.chai.repositories

import com.omnitech.chai.model.Device
import com.omnitech.chai.model.User
import org.springframework.data.neo4j.annotation.Query
import org.springframework.data.neo4j.repository.GraphRepository
import org.springframework.data.repository.query.Param

/**
 * Created by kay on 9/23/14.
 */
interface UserRepository extends GraphRepository<User> {
    User findByUsername(String username)
}


interface DeviceRepository extends GraphRepository<Device> {

    @Query('start n = node({userId}) Match n-[:HAS_DEVICE]->d return d AS model UNION MATCH (d:Device) WHERE NOT (()-[:HAS_DEVICE ]->(d)) RETURN d AS model')
    Iterable<Device> findAllFreeDevices(@Param('userId') Long userId)

    @Query('MATCH (d:Device) WHERE NOT (()-[:HAS_DEVICE ]->(d)) RETURN d AS model')
    Iterable<Device> findAllFreeDevices()

}