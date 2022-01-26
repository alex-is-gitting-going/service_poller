package com.kry.utility.servicepoller.poller.registeredservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RegisteredServiceRepository extends JpaRepository<RegisteredService, Long> {

    List<RegisteredService> findByStatusNot(ServiceStatus status);

    List<RegisteredService> findByUsername(String username);

    List<RegisteredService> findFirst100ByOrderByLastPolledDateAsc();

    @Transactional
    @Modifying
    @Query("update RegisteredService rs set rs.status= :status, rs.lastPolledDate=CURRENT_TIMESTAMP where rs.id = :id")
    void updateServiceStatus(@Param("id") long id, @Param("status") ServiceStatus status);

    @Transactional
    @Modifying
    @Query("update RegisteredService rs set rs.name= :name, rs.url = :url, rs.status= :status, rs.lastPolledDate = null where rs.id = :id")
    void updateServiceSettings(@Param("id") long id, @Param("name") String name, @Param("url") String url, @Param("status") ServiceStatus status);


}
