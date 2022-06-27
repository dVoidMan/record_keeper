package com.catalogue.repository;

import com.catalogue.model.RecordsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RecordsRepo extends JpaRepository<RecordsModel,String> {

    // returns true if email and password of a user is found in the DB
    @Query("SELECT CASE WHEN COUNT(email) > 0 THEN true ELSE false END FROM RecordsModel recordsModel WHERE recordsModel.email= :email")
    boolean cannotAddRecord(String email);

    @Query("SELECT COUNT(gender) FROM RecordsModel recordsModel WHERE recordsModel.gender = :gender AND recordsModel.userId= :userId")
    int numberOfMaleRecords(String userId, String gender);

    @Query("SELECT COUNT(gender) FROM RecordsModel recordsModel WHERE recordsModel.gender = :gender AND recordsModel.userId= :userId")
    int numberOfFemaleRecords(String userId, String gender);

    @Query("SELECT records FROM RecordsModel records WHERE records.userId= :userId")
    Page<RecordsModel> getAddedRecords(Pageable pageable, String userId);

    @Query("SELECT record FROM RecordsModel record WHERE record.id= :recordId")
    RecordsModel getARecord(String recordId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RecordsModel record WHERE record.id= :recordId")
    void deleteRecord(String recordId);
}